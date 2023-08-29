package br.com.fgto.customers.scheduled;

import br.com.fgto.customers.repository.MessageRepository;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageProcessor {
    @Autowired
    private MessageRepository repository;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Tracer tracer;

    @Autowired
    private OpenTelemetry telemetry;

    private TextMapGetter<br.com.fgto.customers.entity.Message> getter;

    public MessageProcessor() {
        getter = new TextMapGetter<br.com.fgto.customers.entity.Message>() {
            @Override
            public Iterable<String> keys(br.com.fgto.customers.entity.Message carrier) {
                Map<String, String> ctx = parseContext(carrier.getContext());
                return ctx.keySet();
            }

            @Override
            public String get(br.com.fgto.customers.entity.Message carrier, String key) {
                Map<String, String> ctx = parseContext(carrier.getContext());
                return ctx.get(key);
            }

            private Map<String, String> parseContext(String context) {
                Map<String, String> map = new HashMap<>();
                String[] ctx = context.split(";");
                for (String entry : ctx) {
                    String[] pair = entry.split(":");
                    map.put(pair[0], pair[1]);
                }
                return map;
            }
        };
    }

    @Scheduled(fixedDelay = 1000)
    public void processMessages() {
        for (br.com.fgto.customers.entity.Message message : repository.findAll()) {
            Context ctx = telemetry.getPropagators().getTextMapPropagator().extract(Context.current(), message, getter);
            Span span = tracer.spanBuilder(message.getRoutingKey()).setParent(ctx).setSpanKind(SpanKind.PRODUCER).startSpan();

            try (Scope scope = span.makeCurrent()) {
                Message event = new Message(message.getBody());

                telemetry.getPropagators().getTextMapPropagator().inject(ctx, event, (carrier, key, value) -> {
                    carrier.getMessageProperties().setHeader(key, value);
                });

                span.setAttribute("ms", InetAddress.getLoopbackAddress().getHostAddress());
                template.send(message.getExchange(), message.getRoutingKey(), event);

                repository.delete(message);
            } catch (AmqpException exception) {
                message.attempt();
                repository.save(message);
                span.recordException(exception);
            } finally {
                span.end();
            }
        }
    }
}

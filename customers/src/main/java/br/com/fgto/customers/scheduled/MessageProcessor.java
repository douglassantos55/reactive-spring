package br.com.fgto.customers.scheduled;

import br.com.fgto.customers.repository.MessageRepository;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

    @Scheduled(fixedDelay = 1000)
    public void processMessages() {
        for (br.com.fgto.customers.entity.Message message : repository.findAll()) {
            Span span = tracer.spanBuilder(message.getRoutingKey()).setSpanKind(SpanKind.PRODUCER).startSpan();

            try (Scope scope = span.makeCurrent()) {
                Message event = new Message(message.getBody());

                telemetry.getPropagators().getTextMapPropagator().inject(Context.current(), event, (carrier, key, value) -> {
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

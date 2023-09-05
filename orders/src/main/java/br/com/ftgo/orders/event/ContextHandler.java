package br.com.ftgo.orders.event;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.function.Function;

@Component
public class ContextHandler {
    private MessageContextHandler messageHandler;

    private AmqpContextHandler amqpHandler;

    private TextMapPropagator propagator;

    private Tracer tracer;

    public ContextHandler(AmqpContextHandler amqpHandler, MessageContextHandler messageHandler, Tracer tracer, TextMapPropagator propagator) {
        this.tracer = tracer;
        this.propagator = propagator;
        this.amqpHandler = amqpHandler;
        this.messageHandler = messageHandler;
    }

    public void inject(Message message) {
        propagator.inject(Context.current(), message, amqpHandler);
    }

    public void inject(br.com.ftgo.orders.entity.Message message) {
        propagator.inject(Context.current(), message, messageHandler);
    }

    public void withMessageContext(Message message, Function<Span, Object> callback) {
        Context context = propagator.extract(Context.current(), message, amqpHandler);

        try (Scope scope = context.makeCurrent()) {
            Span span = tracer
                    .spanBuilder(message.getMessageProperties().getReceivedRoutingKey())
                    .setSpanKind(SpanKind.CONSUMER)
                    .setAttribute("mr", InetAddress.getLoopbackAddress().getHostAddress())
                    .startSpan();

            try (Scope spanScope = span.makeCurrent()) {
                callback.apply(span);
            } finally {
                span.end();
            }
        }
    }

    public Mono<?> withMessageContext(br.com.ftgo.orders.entity.Message message, Function<Span, Mono<?>> callback) {
        Context context = propagator.extract(Context.current(), message, messageHandler);

        try (Scope scope = context.makeCurrent()){
            Span span = tracer.spanBuilder(message.getRoutingKey())
                    .setSpanKind(SpanKind.PRODUCER)
                    .setAttribute("ms", InetAddress.getLoopbackAddress().getHostAddress())
                    .startSpan();

            try (Scope spanScope = span.makeCurrent()) {
                return callback.apply(span);
            } finally {
                span.end();
            }
        }
    }
}

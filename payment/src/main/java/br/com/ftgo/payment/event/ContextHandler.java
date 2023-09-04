package br.com.ftgo.payment.event;

import br.com.ftgo.payment.entity.Message;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.function.Function;

@Component
public class ContextHandler {
    private MessageContextHandler messageHandler;

    private AmqpContextHandler amqpHandler;

    private TextMapPropagator propagator;

    private Tracer tracer;

    public ContextHandler(MessageContextHandler messageHandler, AmqpContextHandler amqpHandler, Tracer tracer, TextMapPropagator propagator) {
        this.tracer = tracer;
        this.propagator = propagator;
        this.amqpHandler = amqpHandler;
        this.messageHandler = messageHandler;
    }

    public void inject(Message message) {
        propagator.inject(Context.current(), message, messageHandler);
    }

    public void inject(org.springframework.amqp.core.Message message) {
        propagator.inject(Context.current(), message, amqpHandler);
    }

    public void withMessageContext(Message message, Function<Span, Void> callback) {
        Context context = propagator.extract(Context.current(), message, messageHandler);

        try (Scope scope = context.makeCurrent()) {
            Span span = tracer.spanBuilder(message.getRoutingKey())
                    .setSpanKind(SpanKind.PRODUCER)
                    .setAttribute("ms", InetAddress.getLoopbackAddress().getHostAddress())
                    .startSpan();

            try (Scope spanScope = span.makeCurrent()) {
                callback.apply(span);
            } finally {
                span.end();
            }
        }
    }

    public void withMessageContext(org.springframework.amqp.core.Message message, Function<Span, Void> callback) {
        Context context = propagator.extract(Context.current(), message, amqpHandler);

        try (Scope scope = context.makeCurrent()) {
            Span span = tracer.spanBuilder(message.getMessageProperties().getReceivedRoutingKey())
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
}

package br.com.fgto.customers.message;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.function.Function;

@Component
public class ContextHandler {
    private Tracer tracer;

    private AmqpContextHandler amqpHandler;

    private MessageContextHandler messageHandler;

    private TextMapPropagator propagator;

    public ContextHandler(
            AmqpContextHandler amqpHandler,
            MessageContextHandler messageHandler,
            Tracer tracer,
            TextMapPropagator propagator
    ) {
        this.tracer = tracer;
        this.propagator = propagator;
        this.amqpHandler = amqpHandler;
        this.messageHandler = messageHandler;
    }

    public void injectContext(Message message) {
        propagator.inject(Context.current(), message, amqpHandler);
    }

    public void injectContext(br.com.fgto.customers.entity.Message message) {
        propagator.inject(Context.current(), message, messageHandler);
    }

    public void withMessageContext(br.com.fgto.customers.entity.Message message, Function<Span, Object> callback) {
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
}

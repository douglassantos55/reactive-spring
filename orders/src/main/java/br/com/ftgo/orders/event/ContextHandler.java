package br.com.ftgo.orders.event;

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
    private TextMapPropagator propagator;

    private Tracer tracer;

    private AmqpMessageContextExtractor getter;

    public ContextHandler(Tracer tracer, TextMapPropagator propagator, AmqpMessageContextExtractor getter) {
        this.tracer = tracer;
        this.getter = getter;
        this.propagator = propagator;
    }

    public void withMessageContext(Message message, Function<Span, Object> callback) {
        Context context = propagator.extract(Context.current(), message, getter);

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
}

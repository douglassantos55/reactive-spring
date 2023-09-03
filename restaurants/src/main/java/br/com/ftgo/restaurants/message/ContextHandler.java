package br.com.ftgo.restaurants.message;

import br.com.ftgo.restaurants.entity.Message;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.function.Function;

@Component
public class ContextHandler {
    private Tracer tracer;

    private TextMapPropagator propagator;

    private MessageContextHandler messageHandler;

    private AmqpContextHandler amqpHandler;

    public ContextHandler(
            MessageContextHandler messageHandler,
            Tracer tracer,
            TextMapPropagator propagator,
            AmqpContextHandler amqpHandler
    ) {
        this.tracer = tracer;
        this.propagator = propagator;
        this.amqpHandler = amqpHandler;
        this.messageHandler = messageHandler;
    }

    public void injectContext(org.springframework.amqp.core.Message message) {
        propagator.inject(Context.current(), message, amqpHandler);
    }

    public void injectContext(Message message) {
        propagator.inject(Context.current(), message, messageHandler);
    }

    public Mono<?> withMessageContext(Message message, Function<Span, Mono<?>> callback) {
        Context context = propagator.extract(Context.current(), message, messageHandler);

        try (Scope scope = context.makeCurrent()) {
            Span span = tracer.spanBuilder(message.getKey())
                    .setSpanKind(SpanKind.PRODUCER)
                    .setAttribute("ms", InetAddress.getLoopbackAddress().getHostAddress())
                    .startSpan();

            try (Scope spanScope = span.makeCurrent()) {
                return callback.apply(span);
            } catch (Exception e) {
                return Mono.error(e);
            } finally {
                span.end();
            }
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

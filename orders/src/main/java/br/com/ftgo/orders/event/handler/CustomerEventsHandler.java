package br.com.ftgo.orders.event.handler;

import br.com.ftgo.orders.entity.Customer;
import br.com.ftgo.orders.event.AmqpMessageContextExtractor;
import br.com.ftgo.orders.repository.CustomersRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class CustomerEventsHandler {
    @Autowired
    private CustomersRepository repository;

    @Autowired
    private Tracer tracer;

    @Autowired
    private TextMapPropagator propagator;

    @Autowired
    private AmqpMessageContextExtractor getter;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "orders.customer.created_updated", durable = "true"),
                    exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
                    key = "customer.created"
            ),
            @QueueBinding(
                    value = @Queue(value = "orders.customer.created_updated", durable = "true"),
                    exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
                    key = "customer.updated"
            )
    })
    public void handleCreated(Customer customer, Message message) {
        Context context = propagator.extract(Context.current(), message, getter);

        try (Scope scope = context.makeCurrent()) {
            Span span = tracer
                    .spanBuilder(message.getMessageProperties().getReceivedRoutingKey())
                    .setSpanKind(SpanKind.CONSUMER)
                    .setAttribute("mr", InetAddress.getLoopbackAddress().getHostAddress())
                    .startSpan();

            try (Scope spanScope = span.makeCurrent()) {
                repository.save(customer).doOnError(exception -> span.recordException(exception)).block();
            } finally {
                span.end();
            }
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "orders.customer.deleted", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "customer.deleted"
    ))
    public void handleDeleted(Customer customer, Message message) {
        Context context = propagator.extract(Context.current(), message, getter);

        try (Scope scope = context.makeCurrent()) {
            Span span = tracer.spanBuilder(message.getMessageProperties().getReceivedRoutingKey())
                    .setSpanKind(SpanKind.CONSUMER)
                    .setAttribute("mr", InetAddress.getLoopbackAddress().getHostAddress())
                    .startSpan();

            try (Scope spanScope = span.makeCurrent()) {
                repository.delete(customer).doOnError(exception -> span.recordException(exception)).block();
            } finally {
                span.end();
            }
        }
    }
}

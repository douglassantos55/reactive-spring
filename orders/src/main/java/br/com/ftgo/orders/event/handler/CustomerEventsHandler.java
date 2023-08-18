package br.com.ftgo.orders.event.handler;

import br.com.ftgo.orders.entity.Customer;
import br.com.ftgo.orders.repository.CustomersRepository;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
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
    private OpenTelemetry telemetry;

    private TextMapGetter<Message> getter;

    public CustomerEventsHandler() {
        getter = new TextMapGetter<>() {
            @Override
            public Iterable<String> keys(Message o) {
                return o.getMessageProperties().getHeaders().keySet();
            }

            @Override
            public String get(Message o, String s) {
                return o.getMessageProperties().getHeader(s);
            }
        };
    }

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
        Context context = telemetry.getPropagators().getTextMapPropagator().extract(Context.current(), message, getter);

        try (Scope scope = context.makeCurrent()) {
            Span span = tracer
                    .spanBuilder(message.getMessageProperties().getReceivedRoutingKey())
                    .setSpanKind(SpanKind.CONSUMER)
                    .startSpan();

            try (Scope spanScope = span.makeCurrent()) {
                span.setAttribute("mr", InetAddress.getLoopbackAddress().getHostAddress());
                repository.save(customer).block();
            } catch (Exception exception) {
                span.recordException(exception);
                throw exception;
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
    public void handleDeleted(Customer customer) {
        repository.delete(customer).block();
    }
}

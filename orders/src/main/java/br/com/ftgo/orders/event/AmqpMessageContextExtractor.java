package br.com.ftgo.orders.event;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class AmqpMessageContextExtractor implements TextMapGetter<Message> {
    @Override
    public Iterable<String> keys(Message message) {
        return message.getMessageProperties().getHeaders().keySet();
    }

    @Override
    public String get(Message message, String s) {
        return message.getMessageProperties().getHeader(s);
    }
}

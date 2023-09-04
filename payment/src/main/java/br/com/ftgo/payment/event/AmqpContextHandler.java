package br.com.ftgo.payment.event;

import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class AmqpContextHandler implements TextMapGetter<Message>, TextMapSetter<Message> {
    @Override
    public Iterable<String> keys(Message message) {
        return message.getMessageProperties().getHeaders().keySet();
    }

    @Override
    public String get(Message message, String key) {
        return message.getMessageProperties().getHeader(key);
    }

    @Override
    public void set(Message message, String key, String value) {
        message.getMessageProperties().setHeader(key, value);
    }
}

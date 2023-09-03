package br.com.ftgo.restaurants.message;

import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class AmqpContextHandler implements TextMapSetter<Message> {
    @Override
    public void set(Message message, String key, String value) {
        message.getMessageProperties().setHeader(key, value);
    }
}

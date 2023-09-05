package br.com.ftgo.orders.event;

import br.com.ftgo.orders.entity.Message;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.stereotype.Component;

@Component
public class MessageContextHandler implements TextMapGetter<Message>, TextMapSetter<Message> {
    @Override
    public Iterable<String> keys(Message message) {
        return message.getContext().keySet();
    }

    @Override
    public String get(Message message, String key) {
        return message.getContext().get(key);
    }

    @Override
    public void set(Message message, String key, String value) {
        message.setContext(key, value);
    }
}

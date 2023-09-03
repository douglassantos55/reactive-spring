package br.com.ftgo.restaurants.message;

import br.com.ftgo.restaurants.entity.Message;
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
        if (message == null) {
            System.out.println("GET NULL MESSAGE NONONONONONONO");
            return "";
        } else {
            return message.getContext().get(key);
        }
    }

    @Override
    public void set(Message message, String key, String value) {
        if (message == null) {
            System.out.println("SET NULL MESSAGE NONONONONONONO");
        } else {
            message.setContext(key, value);
        }
    }
}

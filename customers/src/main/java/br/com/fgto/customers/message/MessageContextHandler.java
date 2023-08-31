package br.com.fgto.customers.message;

import br.com.fgto.customers.entity.Message;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageContextHandler implements TextMapGetter<Message>, TextMapSetter<Message> {
    @Override
    public Iterable<String> keys(Message carrier) {
        Map<String, String> context = carrier.getContext();
        return context.keySet();
    }

    @Override
    public String get(Message carrier, String key) {
        Map<String, String> context = carrier.getContext();
        return context.get(key);
    }

    @Override
    public void set(Message carrier, String key, String value) {
        carrier.setContext(key, value);
    }
}
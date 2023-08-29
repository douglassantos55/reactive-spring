package br.com.fgto.customers.scheduled;

import br.com.fgto.customers.entity.Message;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageContextExtractor implements TextMapGetter<Message> {
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
}

package br.com.fgto.customers.scheduled;

import br.com.fgto.customers.entity.Message;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.stereotype.Component;

@Component
public class MessageContextInjector implements TextMapSetter<Message> {
    @Override
    public void set(Message carrier, String key, String value) {
        carrier.setContext(key, value);
    }
}

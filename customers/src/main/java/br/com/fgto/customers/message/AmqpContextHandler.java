package br.com.fgto.customers.message;

import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class AmqpContextHandler implements TextMapGetter<Message>, TextMapSetter<Message> {
    @Override
    public void set(Message carrier, String key, String value) {
        carrier.getMessageProperties().setHeader(key, value);
    }

    @Override
    public Iterable<String> keys(Message carrier) {
        return carrier.getMessageProperties().getHeaders().keySet();
    }

    @Override
    public String get(Message carrier, String key) {
        return carrier.getMessageProperties().getHeader(key);
    }
}

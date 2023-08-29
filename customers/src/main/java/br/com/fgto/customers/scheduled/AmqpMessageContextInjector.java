package br.com.fgto.customers.scheduled;

import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class AmqpMessageContextInjector implements TextMapSetter<Message> {
    @Override
    public void set(Message carrier, String key, String value) {
        carrier.getMessageProperties().setHeader(key, value);
    }
}

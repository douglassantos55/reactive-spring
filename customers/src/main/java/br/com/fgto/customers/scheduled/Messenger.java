package br.com.fgto.customers.scheduled;

import br.com.fgto.customers.entity.Message;
import br.com.fgto.customers.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.stereotype.Component;

@Component
public class Messenger {
    private MessageRepository repository;

    private ObjectMapper mapper;

    private TextMapPropagator propagator;

    private MessageContextInjector injector;

    public Messenger(
            ObjectMapper mapper,
            MessageRepository repository,
            TextMapPropagator propagator,
            MessageContextInjector injector
    ) {
        this.mapper = mapper;
        this.injector = injector;
        this.propagator = propagator;
        this.repository = repository;
    }

    public void saveMessage(String routingKey, String exchange, Object value) throws JsonProcessingException {
        Message message = new Message();

        message.setRoutingKey(routingKey);
        message.setExchange(exchange);
        message.setBody(mapper.writeValueAsBytes(value));

        propagator.inject(Context.current(), message, injector);

        repository.save(message);
    }
}

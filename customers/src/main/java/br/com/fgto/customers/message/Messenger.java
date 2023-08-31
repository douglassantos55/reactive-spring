package br.com.fgto.customers.message;

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

    private ContextHandler contextHandler;

    public Messenger(
            ObjectMapper mapper,
            MessageRepository repository,
            ContextHandler contextHandler
    ) {
        this.mapper = mapper;
        this.repository = repository;
        this.contextHandler = contextHandler;
    }

    public void saveMessage(String routingKey, String exchange, Object value) throws JsonProcessingException {
        Message message = new Message();

        message.setRoutingKey(routingKey);
        message.setExchange(exchange);
        message.setBody(mapper.writeValueAsBytes(value));

        contextHandler.injectContext(message);

        repository.save(message);
    }
}

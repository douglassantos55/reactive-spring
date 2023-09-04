package br.com.ftgo.payment.event;

import br.com.ftgo.payment.entity.Message;
import br.com.ftgo.payment.repository.MessagesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class Messenger {
    private ObjectMapper mapper;

    private ContextHandler contextHandler;

    private MessagesRepository repository;

    public Messenger(ContextHandler contextHandler, MessagesRepository repository, ObjectMapper mapper) {
        this.mapper = mapper;
        this.repository = repository;
        this.contextHandler = contextHandler;
    }

    public Message saveMessage(String routingKey, String exchange, Object value) throws JsonProcessingException {
        Message message = new Message();

        message.setExchange(exchange);
        message.setRoutingKey(routingKey);
        message.setBody(mapper.writeValueAsBytes(value));

        contextHandler.inject(message);

        return repository.save(message);
    }
}

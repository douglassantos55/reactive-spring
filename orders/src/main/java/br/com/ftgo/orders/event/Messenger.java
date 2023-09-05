package br.com.ftgo.orders.event;

import br.com.ftgo.orders.entity.Message;
import br.com.ftgo.orders.repository.MessagesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Messenger {
    private MessagesRepository repository;

    private ContextHandler contextHandler;

    private ObjectMapper mapper;
    
    public Messenger(MessagesRepository repository, ContextHandler contextHandler, ObjectMapper mapper) {
        this.mapper = mapper;
        this.repository = repository;
        this.contextHandler = contextHandler;
    }

    public Mono<Message> saveMessage(String routingKey, String exchange, Object value) {
        try {
            Message message = new Message();

            message.setExchange(exchange);
            message.setRoutingKey(routingKey);
            message.setBody(mapper.writeValueAsBytes(value));

            contextHandler.inject(message);

            return repository.save(message);
        } catch (JsonProcessingException exception) {
            return Mono.error(exception);
        }
    }
}

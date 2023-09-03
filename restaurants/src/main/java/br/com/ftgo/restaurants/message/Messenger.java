package br.com.ftgo.restaurants.message;

import br.com.ftgo.restaurants.entity.Message;
import br.com.ftgo.restaurants.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Messenger {
    private MessageRepository repository;

    private ObjectMapper mapper;

    private ContextHandler contextHandler;

    public Messenger(MessageRepository repository, ContextHandler contextHandler, ObjectMapper mapper) {
        this.mapper = mapper;
        this.repository = repository;
        this.contextHandler = contextHandler;
    }

    public Mono<Message> saveMessage(String routingKey, String exchange, Object value) {
        try {
            Message message = new Message();

            contextHandler.injectContext(message);

            message.setKey(routingKey);
            message.setExchange(exchange);
            message.setBody(mapper.writeValueAsBytes(value));

            return repository.save(message);
        } catch (JsonProcessingException exception) {
            return Mono.error(exception);
        }
    }
}

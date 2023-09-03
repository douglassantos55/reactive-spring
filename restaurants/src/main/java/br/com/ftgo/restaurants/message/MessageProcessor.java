package br.com.ftgo.restaurants.message;

import br.com.ftgo.restaurants.repository.MessageRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {
    @Autowired
    private MessageRepository repository;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ContextHandler contextHandler;

    @Scheduled(fixedRate = 1000)
    public void processMessages() {
        repository.findAll().flatMap(message ->
            contextHandler.withMessageContext(message, span -> {
                try {
                    Message event = new Message(message.getBody());
                    event.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                    contextHandler.injectContext(event);
                    template.send(message.getExchange(), message.getKey(), event);

                    return repository.delete(message);
                } catch (AmqpException exception) {
                    message.attempt();
                    span.recordException(exception);
                    return repository.save(message);
                }
            }).thenReturn(message)
        ).onErrorComplete().subscribe();
    }
}

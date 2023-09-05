package br.com.ftgo.orders.event;

import br.com.ftgo.orders.repository.MessagesRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {
    private MessagesRepository repository;

    private RabbitTemplate template;

    private ContextHandler contextHandler;

    public MessageProcessor(MessagesRepository repository, ContextHandler contextHandler, RabbitTemplate template) {
        this.template = template;
        this.repository = repository;
        this.contextHandler = contextHandler;
    }

    @Scheduled(fixedRate = 1000)
    public void processMessages() {
        repository.findAll().flatMap(event ->
            contextHandler.withMessageContext(event, span -> {
                try {
                    Message message = new Message(event.getBody());
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                    contextHandler.inject(message);

                    template.send(event.getExchange(), event.getRoutingKey(), message);
                    return repository.delete(event);
                } catch (AmqpException exception) {
                    event.attempt();
                    return repository.save(event);
                }
            }).thenReturn(event)
        ).onErrorComplete().subscribe();
    }
}

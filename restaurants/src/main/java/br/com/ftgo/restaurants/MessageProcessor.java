package br.com.ftgo.restaurants;

import br.com.ftgo.restaurants.repository.MessageRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MessageProcessor {
    @Autowired
    private MessageRepository repository;

    @Autowired
    private RabbitTemplate template;

    @Scheduled(fixedRate = 1000)
    public void processMessages() {
        repository.findAll().flatMap(message -> {
            try {
                Message event = new Message(message.getBody());
                event.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                template.send(message.getExchange(), message.getKey(), event);
                return repository.delete(message).thenReturn(message);
            } catch (AmqpException exception) {
                message.attempt();
                return repository.save(message);
            }
        }).onErrorComplete().subscribe();
    }
}

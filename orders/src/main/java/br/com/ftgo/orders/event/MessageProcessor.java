package br.com.ftgo.orders.event;

import br.com.ftgo.orders.repository.MessagesRepository;
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
    private MessagesRepository repository;

    @Autowired
    private RabbitTemplate template;

    @Scheduled(fixedRate = 1000)
    public void processMessages() {
        repository.findAll().flatMap(event -> {
            try {
                Message message = new Message(event.getBody());
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                template.send(event.getExchange(), event.getRoutingKey(), message);
                return repository.delete(event);
            } catch (AmqpException exception) {
                event.attempt();
                return repository.save(event);
            }
        }).onErrorComplete().subscribe();
    }
}

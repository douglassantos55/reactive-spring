package br.com.ftgo.payment.event;

import br.com.ftgo.payment.repository.MessagesRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MessageProcessor {
    private MessagesRepository repository;

    private RabbitTemplate template;

    public MessageProcessor(MessagesRepository repository, RabbitTemplate template) {
        this.template = template;
        this.repository = repository;
    }

    @Scheduled(fixedRate = 1000)
    public void processMessages() {
        for (br.com.ftgo.payment.entity.Message message : repository.findAll()) {
            try {
                Message event = new Message(message.getBody());
                event.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                template.send(message.getExchange(), message.getRoutingKey(), event);
                repository.delete(message);
            } catch (Exception exception) {
                message.setLastAttempt(Instant.now());
                repository.save(message);
            }
        }
    }
}

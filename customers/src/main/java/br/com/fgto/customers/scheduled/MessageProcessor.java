package br.com.fgto.customers.scheduled;

import br.com.fgto.customers.repository.MessageRepository;
import org.springframework.amqp.core.Message;
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

    @Scheduled(fixedDelay = 1000)
    public void processFailedMessages() {
        repository.findAll().flatMap(message -> {
            Message event = new Message(message.getBody());
            template.send(message.getExchange(), message.getRoutingKey(), event);

            return repository.delete(message).thenReturn(message);
        }).onErrorComplete().subscribe();
    }
}

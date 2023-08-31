package br.com.fgto.customers.message;

import br.com.fgto.customers.repository.MessageRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class MessageProcessor {
    @Autowired
    private MessageRepository repository;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ContextHandler contextHandler;

    @Scheduled(fixedDelay = 1000)
    public void processMessages() {
        for (br.com.fgto.customers.entity.Message message : repository.findAll()) {
            contextHandler.withMessageContext(message, span -> {
                try {
                    Message event = new Message(message.getBody());

                    contextHandler.injectContext(event);
                    template.send(message.getExchange(), message.getRoutingKey(), event);

                    repository.delete(message);
                } catch (AmqpException exception) {
                    message.attempt();
                    repository.save(message);
                    span.recordException(exception);
                } finally {
                    return message;
                }
            });
        }
    }
}

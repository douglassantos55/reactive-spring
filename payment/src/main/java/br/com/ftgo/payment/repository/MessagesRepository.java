package br.com.ftgo.payment.repository;

import br.com.ftgo.payment.entity.Message;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface MessagesRepository extends ListCrudRepository<Message, Long> {
    Optional<Message> findByRoutingKey(String routingKey);
}

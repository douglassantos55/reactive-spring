package br.com.ftgo.orders.repository;

import br.com.ftgo.orders.entity.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MessagesRepository extends ReactiveMongoRepository<Message, String> {
}

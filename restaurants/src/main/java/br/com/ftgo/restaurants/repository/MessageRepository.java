package br.com.ftgo.restaurants.repository;

import br.com.ftgo.restaurants.entity.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
}

package br.com.ftgo.orders.repository;

import br.com.ftgo.orders.entity.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrdersRepository extends ReactiveMongoRepository<Order, String> {
}

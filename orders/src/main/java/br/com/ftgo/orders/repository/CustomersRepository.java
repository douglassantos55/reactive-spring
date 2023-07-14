package br.com.ftgo.orders.repository;

import br.com.ftgo.orders.entity.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomersRepository extends ReactiveMongoRepository<Customer, Long> {
}

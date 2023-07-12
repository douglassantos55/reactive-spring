package br.com.ftgo.orders.repository;

import br.com.ftgo.orders.entity.Restaurant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RestaurantsRepository extends ReactiveMongoRepository<Restaurant, String> {
}

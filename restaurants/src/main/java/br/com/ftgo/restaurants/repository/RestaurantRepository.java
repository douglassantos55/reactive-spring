package br.com.ftgo.restaurants.repository;


import br.com.ftgo.restaurants.entity.Restaurant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RestaurantRepository extends ReactiveMongoRepository<Restaurant, String> {

}

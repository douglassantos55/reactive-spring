package br.com.ftgo.restaurants.repository;


import br.com.ftgo.restaurants.entity.Restaurant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RestaurantRepository extends ReactiveMongoRepository<Restaurant, String> {
    public Flux<Restaurant> findByDeletedAtIsNullAndBlockedIsFalse();

}

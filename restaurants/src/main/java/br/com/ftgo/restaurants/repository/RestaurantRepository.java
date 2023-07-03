package br.com.ftgo.restaurants.repository;


import br.com.ftgo.restaurants.entity.Restaurant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RestaurantRepository extends ReactiveMongoRepository<Restaurant, String> {
    public Flux<Restaurant> findByDeletedAtIsNullAndBlockedIsFalse();

    public Mono<Restaurant> findByIdAndDeletedAtIsNullAndBlockedIsFalse(String id);
}

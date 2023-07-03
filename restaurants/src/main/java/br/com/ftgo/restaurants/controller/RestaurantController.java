package br.com.ftgo.restaurants.controller;

import br.com.ftgo.restaurants.entity.Restaurant;
import br.com.ftgo.restaurants.repository.RestaurantRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    @Autowired
    private RestaurantRepository repository;

    @GetMapping
    public Flux<Restaurant> list() {
        return repository.findByDeletedAtIsNullAndBlockedIsFalse();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Restaurant> create(@RequestBody @Valid Restaurant restaurant) {
        return repository.save(restaurant);

    @PutMapping("/{id}")
    public Mono<Restaurant> update(@PathVariable String id, @RequestBody @Valid Restaurant data) {
        return repository
                .findByIdAndDeletedAtIsNullAndBlockedIsFalse(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(Restaurant.class, id)))
                .map(restaurant -> {
                    restaurant.setName(data.getName());
                    restaurant.setDescription(data.getDescription());
                    restaurant.setMenu(data.getMenu());
                    restaurant.setBlocked(data.isBlocked());
                    restaurant.setAddress(data.getAddress());
                    restaurant.setPhone(data.getPhone());
                    restaurant.setWorkingHours(data.getWorkingHours());

                    return restaurant;
                }).flatMap(repository::save);
    }
    }
}

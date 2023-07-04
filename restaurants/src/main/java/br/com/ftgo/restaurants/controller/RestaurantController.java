package br.com.ftgo.restaurants.controller;

import br.com.ftgo.restaurants.entity.Restaurant;
import br.com.ftgo.restaurants.exception.ResourceNotFoundException;
import br.com.ftgo.restaurants.repository.RestaurantRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    @Autowired
    private RestaurantRepository repository;

    @GetMapping
    public Flux<Restaurant> list() {
        return repository.findByDeletedAtIsNullAndBlockedIsFalse();
    }

    @GetMapping("/{id}")
    public Mono<Restaurant> get(@PathVariable String id) {
        return repository
                .findByIdAndDeletedAtIsNullAndBlockedIsFalse(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(Restaurant.class, id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Restaurant> create(@RequestBody @Valid Restaurant restaurant) {
        return repository.save(restaurant);

    @PutMapping("/{id}")
    public Mono<Restaurant> update(@PathVariable String id, @RequestBody @Valid Restaurant data) {
        return get(id).map(restaurant -> {
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Restaurant> delete(@PathVariable String id) {
        return get(id)
                .flatMap(restaurant -> {
                    restaurant.setDeletedAt(Instant.now());
                    return repository.save(restaurant);
                });
    }
}

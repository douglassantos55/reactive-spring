package br.com.ftgo.restaurants.controller;

import br.com.ftgo.restaurants.entity.Message;
import br.com.ftgo.restaurants.entity.Restaurant;
import br.com.ftgo.restaurants.exception.ResourceNotFoundException;
import br.com.ftgo.restaurants.repository.MessageRepository;
import br.com.ftgo.restaurants.repository.RestaurantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    @Autowired
    private RestaurantRepository repository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private DataBufferFactory bufferFactory;

    @Autowired
    private ObjectMapper mapper;

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
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Restaurant> create(@Valid Restaurant restaurant, @RequestPart(required = false) Mono<FilePart> logoFile) {
        return logoFile
                .flatMap(file -> {
                    Path uploadPath = Paths.get("upload-dir").resolve(file.filename()).normalize().toAbsolutePath();
                    return file.transferTo(uploadPath).thenReturn(uploadPath);
                })
                .doOnNext(uploadPath -> {
                    restaurant.setLogo(uploadPath.toUri().toString());
                })
                .thenReturn(restaurant)
                .flatMap(repository::save)
                .flatMap(result -> {
                    try {
                        Message message = new Message();

                        message.setKey("restaurant.created");
                        message.setExchange("notifications.exchange");
                        message.setBody(mapper.writeValueAsBytes(result));

                        return messageRepository.save(message).thenReturn(result);
                    } catch (JsonProcessingException exception) {
                        return Mono.error(exception);
                    }
                });
    }

    @PutMapping("/{id}")
    public Mono<Restaurant> update(@Valid Restaurant data, @PathVariable String id, @RequestPart(required = false) Mono<FilePart> logoFile) {
        return get(id)
                .flatMap(restaurant ->
                    logoFile
                            .flatMap(file -> {
                                Path uploadPath = Paths.get("upload-dir").resolve(file.filename()).normalize().toAbsolutePath();
                                return file.transferTo(uploadPath).thenReturn(uploadPath);
                            })
                            .doOnNext(uploadPath -> restaurant.setLogo(uploadPath.toUri().toString()))
                            .thenReturn(restaurant)
                )
                .map(restaurant -> {
                    restaurant.setName(data.getName());
                    restaurant.setDescription(data.getDescription());
                    restaurant.setMenu(data.getMenu());
                    restaurant.setBlocked(data.isBlocked());
                    restaurant.setAddress(data.getAddress());
                    restaurant.setPhone(data.getPhone());
                    restaurant.setWorkingHours(data.getWorkingHours());

                    return restaurant;
                })
                .flatMap(repository::save)
                .flatMap(result -> {
                    try {
                        Message message = new Message();

                        message.setKey("restaurant.updated");
                        message.setExchange("notifications.exchange");
                        message.setBody(mapper.writeValueAsBytes(result));

                        return messageRepository.save(message).thenReturn(result);
                    } catch (JsonProcessingException exception) {
                        return Mono.error(exception);
                    }
                });
    }

    @Transactional
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Restaurant> delete(@PathVariable String id) {
        return get(id)
                .flatMap(restaurant -> {
                    restaurant.setDeletedAt(Instant.now());
                    return repository.save(restaurant);
                })
                .flatMap(restaurant -> {
                    try {
                        Message message = new Message();

                        message.setKey("restaurant.deleted");
                        message.setExchange("notifications.exchange");
                        message.setBody(mapper.writeValueAsBytes(restaurant));

                        return messageRepository.save(message).thenReturn(restaurant);
                    } catch (JsonProcessingException exception) {
                        return Mono.error(exception);
                    }
                });
    }
}

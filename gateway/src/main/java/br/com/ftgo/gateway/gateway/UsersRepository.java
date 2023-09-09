package br.com.ftgo.gateway.gateway;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

public interface UsersRepository extends ReactiveUserDetailsService, ReactiveMongoRepository<User, Integer> {
}

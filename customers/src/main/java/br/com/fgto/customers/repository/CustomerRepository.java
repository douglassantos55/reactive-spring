package br.com.fgto.customers.repository;

import br.com.fgto.customers.entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends R2dbcRepository<Customer, Long> {
    Flux<Customer> findByDeletedAtIsNull();

    Mono<Customer> findByIdAndDeletedAtIsNull(Long id);
}

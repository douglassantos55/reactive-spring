package br.com.reconcip.customers.repository;

import br.com.reconcip.customers.entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CustomerRepository extends R2dbcRepository<Customer, Long> {
}

package br.com.fgto.customers.repository;

import br.com.fgto.customers.entity.Customer;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends ListCrudRepository<Customer, Long> {
    List<Customer> findByDeletedAtIsNull();

    Optional<Customer> findByIdAndDeletedAtIsNull(Long id);
}

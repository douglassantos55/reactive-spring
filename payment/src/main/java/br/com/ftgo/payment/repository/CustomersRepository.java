package br.com.ftgo.payment.repository;

import br.com.ftgo.payment.entity.Customer;
import org.springframework.data.repository.ListCrudRepository;

public interface CustomersRepository extends ListCrudRepository<Customer, Long> {
}

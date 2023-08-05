package br.com.ftgo.payment.repository;

import br.com.ftgo.payment.entity.Invoice;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface InvoicesRepository extends ListCrudRepository<Invoice, Long> {
    Optional<Invoice> findByOrderId(String orderId);
}

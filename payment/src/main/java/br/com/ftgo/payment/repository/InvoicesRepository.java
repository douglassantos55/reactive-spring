package br.com.ftgo.payment.repository;

import br.com.ftgo.payment.entity.Invoice;
import org.springframework.data.repository.ListCrudRepository;

public interface InvoicesRepository extends ListCrudRepository<Invoice, Long> {
}

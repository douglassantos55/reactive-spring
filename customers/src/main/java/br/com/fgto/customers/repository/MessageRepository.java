package br.com.fgto.customers.repository;

import br.com.fgto.customers.entity.Message;
import org.springframework.data.repository.ListCrudRepository;

public interface MessageRepository extends ListCrudRepository<Message, Long> {
}

package br.com.reconcip.customers.repository;

import br.com.reconcip.customers.entity.Message;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
public interface MessageRepository extends R2dbcRepository<Message, Long> {
}

package br.com.fgto.customers.repository;

import br.com.fgto.customers.entity.Message;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
public interface MessageRepository extends R2dbcRepository<Message, Long> {
}

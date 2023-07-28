package br.com.ftgo.payment.repository;

import br.com.ftgo.payment.entity.PaymentMethod;
import org.springframework.data.repository.ListCrudRepository;

public interface PaymentMethodsRepository extends ListCrudRepository<PaymentMethod, Long> {
    boolean existsByGatewayId(String gatewayId);
}

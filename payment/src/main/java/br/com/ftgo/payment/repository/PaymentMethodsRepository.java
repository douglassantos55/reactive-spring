package br.com.ftgo.payment.repository;

import br.com.ftgo.payment.entity.PaymentMethod;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface PaymentMethodsRepository extends ListCrudRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByGatewayId(String gatewayId);

    Optional<PaymentMethod> findByIsDefault(boolean isDefault);
}

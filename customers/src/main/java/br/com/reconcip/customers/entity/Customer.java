package br.com.reconcip.customers.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Entity
@Table(name="customers")
public class Customer {
    @Id
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String billingAddress;

    private String deliveryAddress;

    private Instant deletedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String address) {
        this.billingAddress = address;
    }

    public String getDeliveryAddress() {
        if (deliveryAddress == null) {
            return getBillingAddress();
        }
        return deliveryAddress;

    }

    public void setDeliveryAddress(String address) {
        this.deliveryAddress = address;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
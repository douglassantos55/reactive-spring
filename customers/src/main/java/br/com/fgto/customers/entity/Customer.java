package br.com.fgto.customers.entity;

import br.com.fgto.customers.validation.CpfCnpj;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(name="customers")
public class Customer {
    @Id
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    @CpfCnpj
    private String document;

    @NotEmpty
    private String billingAddress;

    private String deliveryAddress;

    @CreatedDate
    private Instant createdAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}

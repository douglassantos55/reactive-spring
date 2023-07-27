package br.com.fgto.customers.entity;

import br.com.fgto.customers.validation.CpfCnpj;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    @CpfCnpj
    @NotEmpty
    private String document;

    @NotNull
    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "number", column = @Column(name = "billing_number")),
            @AttributeOverride(name = "neighborhood", column = @Column(name = "billing_neighborhood")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "billing_zipcode")),
    })
    private Address billingAddress;

    @Embedded
    @Valid
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street")),
            @AttributeOverride(name = "number", column = @Column(name = "delivery_number")),
            @AttributeOverride(name = "neighborhood", column = @Column(name = "delivery_neighborhood")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "state", column = @Column(name = "delivery_state")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "delivery_zipcode")),
    })
    private Address deliveryAddress;

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

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address address) {
        this.billingAddress = address;
    }

    public Address getDeliveryAddress() {
        if (deliveryAddress == null) {
            return getBillingAddress();
        }
        return deliveryAddress;

    }

    public void setDeliveryAddress(Address address) {
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

package br.com.ftgo.orders.entity;

import br.com.ftgo.orders.dto.CardInformation;
import br.com.ftgo.orders.dto.OrderDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document
public class Order {
    @Id
    @MongoId
    private String id;

    @NotNull
    private Long customerId;

    @Transient
    private Customer customer;

    @NotEmpty
    private String restaurantId;

    @Transient
    private Restaurant restaurant;

    @NotEmpty
    private String paymentType;

    private OrderStatus status;

    @Transient
    private CardInformation card;

    @Valid
    @NotEmpty
    private List<OrderItem> items = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public static Order from(OrderDTO data) {
        Order order = new Order();
        order.setCustomer(data.getCustomer());
        order.setRestaurant(data.getRestaurant());
        order.setCustomerId(data.getCustomerId());
        order.setRestaurantId(data.getRestaurantId());
        order.setItems(data.getItems());
        order.setCard(data.getCard());
        order.setPaymentType(data.getPaymentType());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public CardInformation getCard() {
        return card;
    }

    public void setCard(CardInformation card) {
        this.card = card;
    }
}

package br.com.ftgo.orders.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Document
public class Order {
    @Id
    @MongoId
    private String id;

    @NotNull
    private Long customerId;

    @NotEmpty
    private String restaurantId;

    @Valid
    @NotEmpty
    private List<OrderItem> items = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomerId(Long id) {
        customerId = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setRestaurantId(String id) {
        restaurantId = id;
    }

    public String getRestaurantId() {
        return restaurantId;
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
}

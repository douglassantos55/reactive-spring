package br.com.ftgo.orders.dto;

import br.com.ftgo.orders.entity.Customer;
import br.com.ftgo.orders.entity.OrderItem;
import br.com.ftgo.orders.entity.Restaurant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderDTO {
        @NotNull
        private Long customerId;

        private Customer customer;

        @NotEmpty
        private String restaurantId;

        private Restaurant restaurant;

        @NotEmpty
        private String paymentType;

        private CardInformation card;

        @Valid
        @NotEmpty
        private List<OrderItem>items;

        public Long getCustomerId() {
                return customerId;
        }

        public void setCustomerId(Long customerId) {
                this.customerId = customerId;
        }

        public Customer getCustomer() {
                return customer;
        }

        public void setCustomer(Customer customer) {
                this.customer = customer;
        }

        public String getRestaurantId() {
                return restaurantId;
        }

        public void setRestaurantId(String restaurantId) {
                this.restaurantId = restaurantId;
        }

        public Restaurant getRestaurant() {
                return restaurant;
        }

        public void setRestaurant(Restaurant restaurant) {
                this.restaurant = restaurant;
        }

        public String getPaymentType() {
                return paymentType;
        }

        public void setPaymentType(String paymentType) {
                this.paymentType = paymentType;
        }

        public CardInformation getCard() {
                return card;
        }

        public void setCard(CardInformation card) {
                this.card = card;
        }

        public List<OrderItem> getItems() {
                return items;
        }

        public void setItems(List<OrderItem> items) {
                this.items = items;
        }
}
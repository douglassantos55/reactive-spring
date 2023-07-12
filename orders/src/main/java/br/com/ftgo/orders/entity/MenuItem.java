package br.com.ftgo.orders.entity;

import jakarta.validation.constraints.NotEmpty;

public class MenuItem {
    @NotEmpty
    private String name;

    @NotEmpty
    private double price;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}

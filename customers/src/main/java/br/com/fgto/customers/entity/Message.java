package br.com.fgto.customers.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(name = "messages")
public class Message {
    @Id
    private Long id;

    private byte[] body;

    private String routingKey;

    private String exchange;

    @CreatedDate
    private Instant createdAt;

    private Instant lastAttempt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Instant getCreateAt() {
        return createdAt;
    }

    public void setCreateAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastAttempt() {
        return lastAttempt;
    }

    public void attempt() {
        lastAttempt = Instant.now();
    }
}

package br.com.fgto.customers.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "messages")
@EntityListeners(AuditingEntityListener.class)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private byte[] body;

    private String routingKey;

    private String exchange;

    private String context;

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

    public Map<String, String> getContext() {
        Map<String, String> map = new HashMap<>();
        String[] parts = context.split(";");

        for (String entry : parts) {
            String[] pair = entry.split(":");
            map.put(pair[0], pair[1]);
        }

        return map;
    }

    public void setContext(String key, String value) {
        if (context == null) {
            context = "";
        }
        context += String.format("%s:%s;", key, value);
    }
}

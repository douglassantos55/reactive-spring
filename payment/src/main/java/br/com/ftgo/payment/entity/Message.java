package br.com.ftgo.payment.entity;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String exchange;

    private String routingKey;

    private byte[] body;

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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getContext() {
        Map<String, String> ctx = new HashMap<>();

        if (context != null) {
            String[] parts = context.split(";");

            for (String part : parts) {
                String[] keyValue = part.split(":");
                ctx.put(keyValue[0], keyValue[1]);
            }
        }

        return ctx;
    }

    public void setContext(String key, String value) {
        if (context == null) {
            context = "";
        }
        context += key + ":" + value + ";";
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(Instant lastAttempt) {
        this.lastAttempt = lastAttempt;
    }
}

package br.com.ftgo.restaurants.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document
public class Message {
    @Id
    private String id;

    private String exchange;

    private String key;

    private byte[] body;

    private String context;

    @CreatedDate
    private Instant createdAt;

    private Instant lastAttempt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getContext() {
        Map<String, String> map = new HashMap<>();
        if (context != null) {
            String[] parts = context.split(";");

            for (String entry : parts) {
                String[] pair = entry.split(":");
                map.put(pair[0], pair[1]);
            }
        }
        return map;
    }

    public void setContext(String key, String value) {
        if (context == null) {
            context = "";
        }
        context += String.format("%s:%s;", key, value);
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

    public void attempt() {
        lastAttempt = Instant.now();
    }
}

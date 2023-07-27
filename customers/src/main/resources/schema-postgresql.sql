CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    document VARCHAR(20) NOT NULL,
    billing_street VARCHAR(255) NOT NULL,
    billing_number VARCHAR(255) NOT NULL,
    billing_neighborhood VARCHAR(255) NOT NULL,
    billing_city VARCHAR(255) NOT NULL,
    billing_state VARCHAR(255) NOT NULL,
    billing_zipcode VARCHAR(255) NOT NULL,
    delivery_street VARCHAR(255) DEFAULT NULL,
    delivery_number VARCHAR(255) DEFAULT NULL,
    delivery_neighborhood VARCHAR(255) DEFAULT NULL,
    delivery_city VARCHAR(255) DEFAULT NULL,
    delivery_state VARCHAR(255) DEFAULT NULL,
    delivery_zipcode VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT NULL,
    deleted_at TIMESTAMP DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    routing_key VARCHAR(104) NOT NULL,
    exchange VARCHAR(100) NOT NULL,
    body BYTEA NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_attempt TIMESTAMP DEFAULT NULL
);

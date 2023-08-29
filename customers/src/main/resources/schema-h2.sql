CREATE TABLE IF NOT EXISTS customers (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
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
    created_at DATETIME DEFAULT NULL,
    deleted_at DATETIME DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    routing_key VARCHAR(104) NOT NULL,
    exchange VARCHAR(100) NOT NULL,
    body VARBINARY(1000) NOT NULL,
    context TEXT DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_attempt DATETIME DEFAULT NULL
);
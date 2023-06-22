CREATE TABLE IF NOT EXISTS customers (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    billing_address VARCHAR(255) NOT NULL,
    delivery_address VARCHAR(255) DEFAULT NULL,
    deleted_at DATETIME DEFAULT NULL
);
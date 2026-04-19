CREATE TABLE customer
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    customer_ref  VARCHAR(100) NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    phone_number  VARCHAR(20)  NOT NULL,
    email_address VARCHAR(300),
    status        VARCHAR(30)  NOT NULL,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_customer_ref (customer_ref),
    UNIQUE KEY uk_customer_phone_number (phone_number),
    UNIQUE KEY uk_customer_email_address (email_address)
);

CREATE TABLE customer_limit
(
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    customer_id  BIGINT         NOT NULL,
    limit_amount DECIMAL(19, 2) NOT NULL,
    active       BOOLEAN        NOT NULL,
    created_at   DATETIME       NOT NULL,
    updated_at   DATETIME       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_customer_limit_customer
        FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE INDEX idx_customer_limit_customer_active
    ON customer_limit (customer_id, active);
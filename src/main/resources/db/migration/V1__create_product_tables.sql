CREATE TABLE product
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    status      VARCHAR(30)  NOT NULL,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (code)
);

CREATE TABLE product_config
(
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    product_id     BIGINT      NOT NULL,
    config_type    VARCHAR(50) NOT NULL,
    config_version INT         NOT NULL,
    active         BOOLEAN     NOT NULL,
    config_json    JSON        NOT NULL,
    created_at     DATETIME    NOT NULL,
    updated_at     DATETIME    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_config_product
        FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT uk_product_config_type_version
        UNIQUE KEY (product_id, config_type, config_version)
);

CREATE INDEX idx_product_config_product_type_active
    ON product_config (product_id, config_type, active);






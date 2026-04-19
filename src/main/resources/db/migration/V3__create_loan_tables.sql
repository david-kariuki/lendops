CREATE TABLE loan
(
    id                     BIGINT         NOT NULL AUTO_INCREMENT,
    loan_ref               VARCHAR(100)   NOT NULL,
    customer_id            BIGINT         NOT NULL,
    product_id             BIGINT         NOT NULL,
    principal_amount       DECIMAL(19, 2) NOT NULL,
    total_repayable_amount DECIMAL(19, 2) NOT NULL,
    disbursed_at           DATETIME       NOT NULL,
    due_date               DATETIME       NOT NULL,
    billing_type           VARCHAR(50)    NOT NULL,
    loan_structure_type    VARCHAR(50)    NOT NULL,
    status                 VARCHAR(30)    NOT NULL,
    created_at             DATETIME       NOT NULL,
    updated_at             DATETIME       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_loan_loan_ref (loan_ref),
    CONSTRAINT fk_loan_customer FOREIGN KEY (customer_id) REFERENCES customer (id),
    CONSTRAINT fk_loan_product FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE TABLE loan_installment
(
    id                 BIGINT         NOT NULL AUTO_INCREMENT,
    loan_id            BIGINT         NOT NULL,
    installment_number INT            NOT NULL,
    due_date           DATETIME       NOT NULL,
    principal_amount   DECIMAL(19, 2) NOT NULL,
    total_amount       DECIMAL(19, 2) NOT NULL,
    status             VARCHAR(30)    NOT NULL,
    created_at         DATETIME       NOT NULL,
    updated_at         DATETIME       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_loan_installment_loan FOREIGN KEY (loan_id) REFERENCES loan (id)
);

CREATE INDEX idx_loan_customer_id ON loan (customer_id);
CREATE INDEX idx_loan_product_id ON loan (product_id);
CREATE INDEX idx_loan_status ON loan (status);
CREATE INDEX idx_loan_installment_loan_id ON loan_installment (loan_id);
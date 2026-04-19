CREATE TABLE repayment
(
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    repayment_ref     VARCHAR(100)   NOT NULL,
    loan_id           BIGINT         NOT NULL,
    amount_paid       DECIMAL(19, 2) NOT NULL,
    payment_reference VARCHAR(100)   NOT NULL,
    paid_at           DATETIME       NOT NULL,
    status            VARCHAR(30)    NOT NULL,
    created_at        DATETIME       NOT NULL,
    updated_at        DATETIME       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_repayment_repayment_ref (repayment_ref),
    UNIQUE KEY uk_repayment_payment_reference (payment_reference),
    CONSTRAINT fk_repayment_loan
        FOREIGN KEY (loan_id) REFERENCES loan (id)
);

CREATE INDEX idx_repayment_loan_id ON repayment (loan_id);
CREATE INDEX idx_repayment_status ON repayment (status);
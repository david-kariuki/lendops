CREATE TABLE notification
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    notification_ref VARCHAR(100) NOT NULL,
    customer_ref     VARCHAR(100) NOT NULL,
    loan_ref         VARCHAR(100),
    type             VARCHAR(50)  NOT NULL,
    channel          VARCHAR(30)  NOT NULL,
    recipient        VARCHAR(150) NOT NULL,
    message          VARCHAR(500) NOT NULL,
    status           VARCHAR(30)  NOT NULL,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_notification_ref (notification_ref)
);

CREATE INDEX idx_notification_customer_ref ON notification (customer_ref);
CREATE INDEX idx_notification_loan_ref ON notification (loan_ref);
CREATE INDEX idx_notification_status ON notification (status);
CREATE INDEX idx_notification_type ON notification (type);
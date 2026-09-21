USE management_platform;

-- CUSTOMERS

CREATE TABLE customers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    created_at DATETIME NOT NULL,

    CONSTRAINT pk_customers
        PRIMARY KEY (id),

    CONSTRAINT uk_customer_email
        UNIQUE (email)
);


-- ACCOUNTS

CREATE TABLE accounts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    number VARCHAR(20) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    customer_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,

    CONSTRAINT pk_accounts
        PRIMARY KEY (id),

    CONSTRAINT uk_account_number
        UNIQUE (number),

    CONSTRAINT fk_account_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id),

    CONSTRAINT chk_account_balance
        CHECK (balance >= 0)
);


-- TRANSACTIONS

CREATE TABLE transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(30) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    account_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    description VARCHAR(255),

    CONSTRAINT pk_transactions
        PRIMARY KEY (id),

    CONSTRAINT fk_transaction_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id),

    CONSTRAINT chk_transaction_amount
        CHECK (amount > 0)
);
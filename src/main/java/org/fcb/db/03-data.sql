USE management_platform;

-- CUSTOMERS

INSERT INTO customers (
    name,
    email,
    phone,
    created_at
) VALUES
(
    'John Doe',
    'john.doe@example.com',
    '+258841234567',
    NOW()
),
(
    'Jane Smith',
    'jane.smith@example.com',
    '+258821234567',
    NOW()
);


-- ACCOUNTS

INSERT INTO accounts (
    number,
    balance,
    customer_id,
    created_at
) VALUES
(
    '1000000001',
    5000.00,
    1,
    NOW()
),
(
    '1000000002',
    2500.00,
    1,
    NOW()
),
(
    '1000000003',
    10000.00,
    2,
    NOW()
);


-- TRANSACTIONS

INSERT INTO transactions (
    type,
    amount,
    account_id,
    created_at,
    description
) VALUES
(
    'DEPOSIT',
    5000.00,
    1,
    NOW(),
    'Initial deposit'
),
(
    'DEPOSIT',
    2500.00,
    2,
    NOW(),
    'Initial deposit'
),
(
    'DEPOSIT',
    10000.00,
    3,
    NOW(),
    'Initial deposit'
);
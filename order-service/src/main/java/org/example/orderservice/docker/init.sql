CREATE TABLE IF NOT EXISTS fee (
    id SERIAL PRIMARY KEY,
    amount REAL NOT NULL CHECK ( amount > 0 ),
    currency CHARACTER(3) NOT NULL
);

CREATE TYPE delivery_type AS ENUM ('STANDARD', 'EXPRESS', 'SAME_DAY', 'NEXT_DAY', 'SCHEDULED', 'IN_STORE_PICKING');
CREATE TYPE delivery_status AS ENUM ('ORDER_RECEIVED', 'ORDER_PROCESSING', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS delivery (
    id SERIAL PRIMARY KEY,
    fee_id INT REFERENCES fee (id) ON DELETE CASCADE,
    status delivery_status NOT NULL,
    type delivery_type NOT NULL
);

CREATE TABLE IF NOT EXISTS quantity (
    id SERIAL PRIMARY KEY,
    quantity INT NOT NULL CHECK ( quantity > 0 )
);

CREATE TABLE IF NOT EXISTS order_short (
    id SERIAL PRIMARY KEY,
    delivery_id INT REFERENCES delivery (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_item_short (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES order_short (id),
    item_id INT REFERENCES product_short (id),
    quantity_id INT REFERENCES quantity (id)
);

CREATE TABLE IF NOT EXISTS address (
    id SERIAL PRIMARY KEY,
    city VARCHAR(64) NOT NULL,
    country VARCHAR(64) NOT NULL,
    street_address TEXT NOT NULL,
    apartment TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_long (
    id SERIAL PRIMARY KEY,
    address_id INT REFERENCES address (id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item_long (
    id SERIAL PRIMARY KEY REFERENCES order_item_short (id) ON DELETE CASCADE,
    order_id INT REFERENCES order_long (id),
    item_id INT REFERENCES product_long (id),
    quantity_id INT REFERENCES quantity (id)
);
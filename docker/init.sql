\c e_commerce_microservices;

CREATE TABLE IF NOT EXISTS price (
    id SERIAL PRIMARY KEY,
    amount REAL NOT NULL CHECK ( amount > 0 ),
    currency CHARACTER(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
--     parent_category_id INT DEFAULT NULL,
    name TEXT NOT NULL UNIQUE,
    count INT NOT NULL CHECK ( count >= 0 ) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS product_short (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    price_id INT REFERENCES price (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product_short_category (
    product_short_id INT REFERENCES product_short (id),
    category_id INT REFERENCES category (id),
    PRIMARY KEY (product_short_id, category_id)
);

CREATE TABLE IF NOT EXISTS product_long (
    id SERIAL PRIMARY KEY,
    product_short_id INT REFERENCES product_short (id) ON DELETE CASCADE,
    length_m REAL NOT NULL CHECK ( length_m > 0 ),
    width_m REAL NOT NULL CHECK ( width_m > 0 ),
    height_m REAL NOT NULL CHECK ( height_m > 0 ),
    net_weight_kg REAL NOT NULL CHECK ( net_weight_kg > 0 ),
    gross_weight_kg REAL NOT NULL CHECK ( gross_weight_kg > 0 )
);

CREATE OR REPLACE FUNCTION increment_category_count() RETURNS TRIGGER AS $$
BEGIN
    UPDATE category SET count = count + 1 WHERE id = NEW.category_id;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION decrement_category_count() RETURNS TRIGGER AS $$
BEGIN
    UPDATE category SET count = count - 1 WHERE id = OLD.category_id;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_insert AFTER INSERT ON product_short_category FOR EACH ROW EXECUTE PROCEDURE increment_category_count();

CREATE OR REPLACE TRIGGER tr_delete AFTER DELETE ON product_short_category FOR EACH ROW EXECUTE PROCEDURE decrement_category_count();

CREATE TABLE IF NOT EXISTS image (
    id SERIAL PRIMARY KEY,
    owner_id INT REFERENCES product_short (id),
    url TEXT NOT NULL
);

CREATE TYPE delivery_type AS ENUM ('STANDARD', 'EXPRESS', 'SAME_DAY', 'NEXT_DAY', 'SCHEDULED', 'IN_STORE_PICKING');
CREATE TYPE delivery_status AS ENUM ('ORDER_RECEIVED', 'ORDER_PROCESSING', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS fee (
    id INT PRIMARY KEY,
    amount REAL NOT NULL CHECK ( amount > 0 ),
    currency CHARACTER(3) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS fee_seq START 1 INCREMENT 20 OWNED BY fee.id;

CREATE TABLE IF NOT EXISTS delivery (
    id INT PRIMARY KEY,
    fee_id INT REFERENCES fee (id),
    status delivery_status NOT NULL,
    type delivery_type NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS delivery_seq START 1 INCREMENT 20 OWNED BY delivery.id;

CREATE TABLE IF NOT EXISTS "order" (
    id INT PRIMARY KEY,
    delivery_id INT REFERENCES delivery (id),
    user_id UUID NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS order_seq START 1 INCREMENT 20 OWNED BY "order".id;

CREATE TABLE IF NOT EXISTS order_item (
    id INT PRIMARY KEY,
    order_id INT REFERENCES "order" (id),
    item_id INT REFERENCES product_long (id),
    quantity INT NOT NULL CHECK ( quantity > 0 )
);

CREATE SEQUENCE IF NOT EXISTS order_item_seq START 1 INCREMENT 50 OWNED BY order_item.id;

CREATE TABLE IF NOT EXISTS address (
    id INT PRIMARY KEY,
    city VARCHAR(64) NOT NULL,
    country VARCHAR(64) NOT NULL,
    street_address TEXT NOT NULL,
    apartment TEXT NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS address_seq START 1 INCREMENT 20 OWNED BY address.id;

CREATE TABLE IF NOT EXISTS order_details (
    id INT PRIMARY KEY,
    order_id INT REFERENCES "order" (id),
    address_id INT REFERENCES address (id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS order_details_seq START 1 INCREMENT 20 OWNED BY order_details.id;

CREATE TABLE IF NOT EXISTS cart (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_item (
    id INT PRIMARY KEY,
    product_id INT REFERENCES product_long (id),
    quantity INT NOT NULL,
    cart_id UUID REFERENCES cart (id)
);

CREATE SEQUENCE cart_item_seq START 1 INCREMENT 75 OWNED BY cart_item.id;

CREATE OR REPLACE FUNCTION on_item_added_to_cart() RETURNS TRIGGER AS $$
BEGIN
    UPDATE cart SET updated_at = CURRENT_TIMESTAMP WHERE NEW.cart_id = id;
    UPDATE cart SET updated_at = CURRENT_TIMESTAMP WHERE OLD.cart_id = id;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_update_updated_at AFTER INSERT OR DELETE ON cart_item FOR EACH ROW EXECUTE PROCEDURE on_item_added_to_cart();
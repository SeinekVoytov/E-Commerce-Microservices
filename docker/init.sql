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
    img_uri TEXT NOT NULL,
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

CREATE TABLE IF NOT EXISTS cart (
    id UUID PRIMARY KEY,
    user_id UUID
);

CREATE TABLE IF NOT EXISTS cart_item (
    id INT PRIMARY KEY,
    product_id INT REFERENCES product_long (id),
    quantity INT NOT NULL,
    cart_id UUID REFERENCES cart (id)
);

CREATE SEQUENCE cart_item_seq START 1 INCREMENT 75 OWNED BY cart_item.id;
\c e_commerce_microservices;

CREATE TABLE IF NOT EXISTS price (
    id INT PRIMARY KEY,
    amount NUMERIC NOT NULL CHECK ( amount > 0 ),
    currency CHARACTER(3) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS price_seq START 1 INCREMENT 20 OWNED BY price.id;

CREATE TABLE IF NOT EXISTS category (
    id INT PRIMARY KEY,
    parent_category_id INT DEFAULT NULL REFERENCES category (id),
    name TEXT NOT NULL UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS category_seq START 1 INCREMENT 20 OWNED BY category.id;

CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    net_weight_kg DOUBLE PRECISION NOT NULL CHECK ( net_weight_kg > 0 ),
    price_id INT REFERENCES price (id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS product_seq START 1 INCREMENT 20 OWNED BY product.id;

CREATE TABLE IF NOT EXISTS product_category (
    product_id INT REFERENCES product (id),
    category_id INT REFERENCES category (id),
    PRIMARY KEY (product_id, category_id)
);

CREATE TABLE IF NOT EXISTS product_details (
    id INT PRIMARY KEY,
    product_id INT REFERENCES product (id) ON DELETE CASCADE,
    length_meters DOUBLE PRECISION NOT NULL CHECK ( length_meters > 0 ),
    width_meters DOUBLE PRECISION NOT NULL CHECK ( width_meters > 0 ),
    height_meters DOUBLE PRECISION NOT NULL CHECK ( height_meters > 0 ),
    gross_weight_kg DOUBLE PRECISION NOT NULL CHECK ( gross_weight_kg > 0 )
);

CREATE SEQUENCE IF NOT EXISTS product_details_seq START 1 INCREMENT 20 OWNED BY product_details.id;

CREATE TABLE IF NOT EXISTS image (
    id INT PRIMARY KEY,
    owner_id INT REFERENCES product (id),
    url VARCHAR(2048) UNIQUE NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS image_seq START 1 INCREMENT 20 OWNED BY image.id;

CREATE TABLE IF NOT EXISTS fee (
    id INT PRIMARY KEY,
    amount DECIMAL NOT NULL CHECK ( amount > 0 ),
    currency CHARACTER(3) NOT NULL
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
    status delivery_status NOT NULL DEFAULT 'ORDER_RECEIVED',
    type delivery_type NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS delivery_seq START 1 INCREMENT 20 OWNED BY delivery.id;

CREATE TABLE IF NOT EXISTS "order" (
    id INT PRIMARY KEY,
    delivery_id INT REFERENCES delivery (id),
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS order_seq START 1 INCREMENT 20 OWNED BY "order".id;

CREATE TABLE IF NOT EXISTS order_item (
    id INT PRIMARY KEY,
    order_id INT REFERENCES "order" (id),
    item_id INT REFERENCES product_details (id),
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
    product_id INT REFERENCES product (id),
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
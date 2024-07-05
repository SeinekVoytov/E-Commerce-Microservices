--liquibase formatted sql

--changeset SeinekVoytov:1 dbms:postgresql
CREATE TABLE IF NOT EXISTS price (
    id INT PRIMARY KEY,
    amount NUMERIC NOT NULL CHECK ( amount > 0 ),
    currency CHARACTER(3) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS price_seq START 1 INCREMENT 20 OWNED BY price.id;
--rollback DROP SEQUENCE IF EXISTS price_seq;
--rollback DROP TABLE IF EXISTS price;

--changeset SeinekVoytov:2 dbms:postgresql
CREATE TABLE IF NOT EXISTS category (
    id INT PRIMARY KEY,
    parent_category_id INT DEFAULT NULL REFERENCES category (id),
    name TEXT NOT NULL UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS category_seq START 1 INCREMENT 20 OWNED BY category.id;
--rollback DROP SEQUENCE IF EXISTS category_seq;
--rollback DROP TABLE IF EXISTS category;

--changeset SeinekVoytov:3 dbms:postgresql
CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY,
    name TEXT NOT NULL,
    price_id INT REFERENCES price (id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS product_seq START 1 INCREMENT 20 OWNED BY product.id;
--rollback DROP SEQUENCE IF EXISTS product_seq;
--rollback DROP TABLE IF EXISTS product;

--changeset SeinekVoytov:4 dbms:postgresql
CREATE TABLE IF NOT EXISTS product_category (
    product_id INT REFERENCES product (id),
    category_id INT REFERENCES category (id),
    PRIMARY KEY (product_id, category_id)
);
--rollback DROP TABLE IF EXISTS product_category;

--changeset SeinekVoytov:5 dbms:postgresql
CREATE TABLE IF NOT EXISTS product_details (
    id INT PRIMARY KEY,
    product_id INT REFERENCES product (id) ON DELETE CASCADE,
    length_meters DOUBLE PRECISION NOT NULL CHECK ( length_meters > 0 ),
    width_meters DOUBLE PRECISION NOT NULL CHECK ( width_meters > 0 ),
    height_meters DOUBLE PRECISION NOT NULL CHECK ( height_meters > 0 ),
    net_weight_kg DOUBLE PRECISION NOT NULL CHECK ( net_weight_kg > 0 ),
    gross_weight_kg DOUBLE PRECISION NOT NULL CHECK ( gross_weight_kg > 0 )
);

CREATE SEQUENCE IF NOT EXISTS product_details_seq START 1 INCREMENT 20 OWNED BY product_details.id;
--rollback DROP SEQUENCE IF EXISTS product_details_seq;
--rollback DROP TABLE IF EXISTS product_details;

--changeset SeinekVoytov:6 dbms:postgresql
CREATE TABLE IF NOT EXISTS image (
    id INT PRIMARY KEY,
    owner_id INT REFERENCES product (id),
    url VARCHAR(2048) UNIQUE NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS image_seq START 1 INCREMENT 20 OWNED BY image.id;
--rollback DROP SEQUENCE IF EXISTS image_seq;
--rollback DROP TABLE IF EXISTS image;

--changeset SeinekVoytov:7 dbms:postgresql
CREATE TABLE IF NOT EXISTS fee (
    id INT PRIMARY KEY,
    amount DECIMAL NOT NULL CHECK ( amount > 0 ),
    currency CHARACTER(3) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS fee_seq START 1 INCREMENT 20 OWNED BY fee.id;
--rollback DROP SEQUENCE IF EXISTS fee_seq;
--rollback DROP TABLE IF EXISTS fee;

--changeset SeinekVoytov:8 dbms:postgresql endDelimiter:$$
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'delivery_type') THEN
            CREATE TYPE delivery_type AS ENUM ('STANDARD', 'EXPRESS', 'SAME_DAY', 'NEXT_DAY', 'SCHEDULED', 'IN_STORE_PICKING');
        END IF;
    END $$;

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'delivery_status') THEN
            CREATE TYPE delivery_status AS ENUM ('ORDER_RECEIVED', 'ORDER_PROCESSING', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED');
        END IF;
    END $$;
--rollback DROP TYPE IF EXISTS delivery_status;
--rollback DROP TYPE IF EXISTS delivery_type;

--changeset SeinekVoytov:9 dbms:postgresql
CREATE TABLE IF NOT EXISTS delivery (
    id INT PRIMARY KEY,
    fee_id INT REFERENCES fee (id),
    status delivery_status NOT NULL,
    type delivery_type NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS delivery_seq START 1 INCREMENT 20 OWNED BY delivery.id;
--rollback DROP SEQUENCE IF EXISTS delivery_seq;
--rollback DROP TABLE IF EXISTS delivery;


--changeset SeinekVoytov:10 dbms:postgresql
CREATE TABLE IF NOT EXISTS "order" (
    id INT PRIMARY KEY,
    delivery_id INT REFERENCES delivery (id),
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS order_seq START 1 INCREMENT 20 OWNED BY "order".id;
--rollback DROP SEQUENCE IF EXISTS order_seq;
--rollback DROP TABLE IF EXISTS "order";

--changeset SeinekVoytov:12 dbms:postgresql
CREATE TABLE IF NOT EXISTS order_item (
    id INT PRIMARY KEY,
    order_id INT REFERENCES "order" (id),
    item_id INT REFERENCES product_details (id),
    quantity INT NOT NULL CHECK ( quantity > 0 )
);

CREATE SEQUENCE IF NOT EXISTS order_item_seq START 1 INCREMENT 50 OWNED BY order_item.id;
--rollback DROP SEQUENCE IF EXISTS order_item_seq;
--rollback DROP TABLE IF EXISTS order_item;

--changeset SeinekVoytov:13 dbms:postgresql
CREATE TABLE IF NOT EXISTS address (
    id INT PRIMARY KEY,
    city VARCHAR(64) NOT NULL,
    country VARCHAR(64) NOT NULL,
    street_address TEXT NOT NULL,
    apartment TEXT NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS address_seq START 1 INCREMENT 20 OWNED BY address.id;
--rollback DROP SEQUENCE IF EXISTS address_seq;
--rollback DROP TABLE IF EXISTS address;

--changeset SeinekVoytov:14 dbms:postgresql
CREATE TABLE IF NOT EXISTS order_details (
    id INT PRIMARY KEY,
    order_id INT REFERENCES "order" (id),
    address_id INT REFERENCES address (id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS order_details_seq START 1 INCREMENT 20 OWNED BY order_details.id;
--rollback DROP SEQUENCE IF EXISTS order_details;
--rollback DROP TABLE IF EXISTS order_details_seq;

--changeset SeinekVoytov:15 dbms:postgresql
CREATE TABLE IF NOT EXISTS cart (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--rollback DROP TABLE IF EXISTS cart;

--changeset SeinekVoytov:16 dbms:postgresql
CREATE TABLE IF NOT EXISTS cart_item (
    id INT PRIMARY KEY,
    product_id INT REFERENCES product (id),
    quantity INT NOT NULL,
    cart_id UUID REFERENCES cart (id)
);

CREATE SEQUENCE IF NOT EXISTS cart_item_seq START 1 INCREMENT 75 OWNED BY cart_item.id;
--rollback DROP SEQUENCE cart_item_seq;
--rollback DROP TABLE cart_item;

--changeset SeinekVoytov:17 dbms:postgresql splitStatements:false
CREATE OR REPLACE FUNCTION on_item_added_to_cart() RETURNS TRIGGER AS $$
BEGIN
    UPDATE cart SET updated_at = CURRENT_TIMESTAMP WHERE NEW.cart_id = id;
    UPDATE cart SET updated_at = CURRENT_TIMESTAMP WHERE OLD.cart_id = id;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_update_updated_at AFTER INSERT OR DELETE ON cart_item FOR EACH ROW EXECUTE PROCEDURE on_item_added_to_cart();
--rollback DROP TRIGGER IF EXISTS tr_update_updated_at ON cart_item;
--rollback DROP FUNCTION IF EXISTS on_item_added_to_cart();
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
    net_weight_kg DOUBLE PRECISION NOT NULL CHECK ( net_weight_kg > 0 ),
    description TEXT NOT NULL,
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

--changeset SeinekVoytov:18 dbms:postgresql
ALTER TABLE product ADD COLUMN brand VARCHAR(256) NOT NULL DEFAULT 'Unknown';
ALTER TABLE product_details ADD COLUMN country_manufacturer VARCHAR(64) NOT NULL DEFAULT 'Unknown';
--rollback ALTER TABLE product DROP COLUMN IF EXISTS brand;
--rollback ALTER TABLE product_details DROP COLUMN IF EXISTS country_manufacturer;

--changeset SeinekVoytov:19 dbms:postgresql
CREATE TABLE IF NOT EXISTS brand (
    id INT PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS brand_seq START 1 INCREMENT 20 OWNED BY brand.id;

ALTER TABLE product DROP COLUMN IF EXISTS brand;
ALTER TABLE product ADD COLUMN IF NOT EXISTS brand_id INT REFERENCES brand(id) ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS country_manufacturer (
    id INT PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS country_seq START 1 INCREMENT 20 OWNED BY country_manufacturer.id;

ALTER TABLE product_details DROP COLUMN IF EXISTS country_manufacturer;
ALTER TABLE product ADD COLUMN IF NOT EXISTS country_manufacturer_id INT REFERENCES country_manufacturer(id) ON DELETE SET NULL;
--rollback ALTER TABLE product DROP COLUMN IF EXISTS brand_id;
--rollback ALTER TABLE product ADD COLUMN IF NOT EXISTS VARCHAR(256) NOT NULL DEFAULT 'Unknown';
--rollback ALTER TABLE product DROP COLUMN IF EXISTS country_manufacturer_id;
--rollback ALTER TABLE product_details ADD COLUMN country_manufacturer VARCHAR(64) NOT NULL DEFAULT 'Unknown';
--rollback DROP SEQUENCE IF EXISTS brand_seq;
--rollback DROP TABLE IF EXISTS brand;
--rollback DROP TABLE IF EXISTS country_manufacturer;
--rollback DROP SEQUENCE IF EXISTS country_seq;

--changeset SeinekVoytov:20 dbms:postgresql
ALTER TABLE order_item DROP CONSTRAINT order_item_item_id_fkey;
ALTER TABLE order_item ADD COLUMN temp_item_id uuid;
UPDATE order_item SET temp_item_id = gen_random_uuid();
ALTER TABLE order_item DROP COLUMN item_id;
ALTER TABLE order_item RENAME COLUMN temp_item_id TO item_id;

ALTER TABLE delivery DROP COLUMN type;
DROP TYPE delivery_type;
ALTER TABLE delivery DROP COLUMN fee_id;
DROP TABLE fee;

CREATE TABLE pick_up_point (
    id uuid PRIMARY KEY,
    address TEXT NOT NULL
);

ALTER TABLE delivery ADD COLUMN pick_up_point_id UUID REFERENCES pick_up_point(id);
ALTER TABLE delivery ALTER COLUMN status SET DEFAULT 'ORDER_RECEIVED';
--rollback ALTER TABLE order_item ADD CONSTRAINT order_item_item_id_fkey FOREIGN KEY (item_id) REFERENCES product_details(id);
--rollback ALTER TABLE order_item ADD COLUMN item_id INT;
--rollback ALTER TABLE order_item DROP COLUMN temp_item_id;
--rollback ALTER TABLE order_item ADD CONSTRAINT order_item_item_id_fkey FOREIGN KEY (item_id) REFERENCES product_details(id);
--rollback CREATE TYPE delivery_type AS ENUM ('STANDARD', 'EXPRESS', 'SAME_DAY', 'NEXT_DAY', 'SCHEDULED', 'IN_STORE_PICKING');
--rollback ALTER TABLE delivery ADD COLUMN type delivery_type NOT NULL;
--rollback DROP TABLE pick_up_point;
--rollback CREATE TABLE fee (id INT PRIMARY KEY, amount DECIMAL NOT NULL CHECK ( amount > 0 ), currency CHARACTER(3) NOT NULL);
--rollback ALTER TABLE delivery ADD COLUMN fee_id INT REFERENCES fee(id);
--rollback ALTER TABLE delivery DROP COLUMN pick_up_point_id;

--changeset SeinekVoytov:21 dbms:postgresql
ALTER TABLE order_item DROP COLUMN item_id;
ALTER TABLE order_item ADD COLUMN item_id INT NOT NULL DEFAULT trunc(random());
--rollback ALTER TABLE order_item DROP COLUMN item_id;
--rollback ALTER TABLE order_item ADD COLUMN item_id uuid NOT NULL DEFAULT gen_random_uuid();

--changeset SeinekVoytov:22 dbms:postgresql
ALTER TABLE order_item ALTER COLUMN item_id DROP DEFAULT;
--rollback ALTER TABLE order_item ALTER COLUMN item_id SET DEFAULT trunc(random());

--changeset SeinekVoytov:23 dbms:postgresql
ALTER TABLE order_details DROP CONSTRAINT order_details_address_id_fkey;
ALTER TABLE order_details DROP COLUMN address_id;
DROP TABLE address;
--rollback CREATE TABLE IF NOT EXISTS address (
--rollback     id INT PRIMARY KEY,
--rollback     city VARCHAR(64) NOT NULL,
--rollback     country VARCHAR(64) NOT NULL,
--rollback     street_address TEXT NOT NULL,
--rollback     apartment TEXT NOT NULL
--rollback );
--rollback ALTER TABLE order_details ADD COLUMN INT address_id REFERENCES address(id);

--changeset SeinekVoytov:24 dbms:postgresql
CREATE TABLE inventory_item (
    id INT PRIMARY KEY,
    product_id INT NOT NULL UNIQUE,
    quantity INT NOT NULL CHECK ( quantity >= 0 )
);

CREATE SEQUENCE inventory_item_seq START 1 INCREMENT 50 OWNED BY inventory_item.id;
--rollback DROP SEQUENCE inventory_item_seq;
--rollback DROP TABLE inventory_item;
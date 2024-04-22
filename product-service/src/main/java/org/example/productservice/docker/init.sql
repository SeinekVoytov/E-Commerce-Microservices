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
    price_id INT REFERENCES price (id) ON DELETE CASCADE,
    category_id INT REFERENCES category (id)
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

CREATE OR REPLACE TRIGGER tr_insert AFTER INSERT ON product_short FOR EACH ROW EXECUTE PROCEDURE increment_category_count();

CREATE OR REPLACE TRIGGER tr_delete AFTER DELETE ON product_short FOR EACH ROW EXECUTE PROCEDURE decrement_category_count();

CREATE TABLE IF NOT EXISTS image (
    id SERIAL PRIMARY KEY,
    owner_id INT REFERENCES product_short (id),
    url TEXT NOT NULL
);
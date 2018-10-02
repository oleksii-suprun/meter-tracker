CREATE SEQUENCE digit_id_seq;
CREATE TABLE digit (
    id BIGINT DEFAULT nextval('digit_id_seq') PRIMARY KEY,
    image OID NOT NULL, -- TODO: consider to store images outside the DB
    value CHAR(1)
);

CREATE SEQUENCE meter_id_seq;
CREATE TABLE meter (
    id BIGINT DEFAULT nextval('meter_id_seq') PRIMARY KEY,
    name VARCHAR(20),
    capacity INTEGER NOT NULL,
    minor_digits INTEGER NOT NULL,
    CHECK (capacity > minor_digits)
);

CREATE SEQUENCE image_info_id_seq;
CREATE TABLE image_info (
    id BIGINT DEFAULT nextval('image_info_id_seq') PRIMARY KEY,
    storage_id VARCHAR(50) NOT NULL,
    hash VARCHAR(255) UNIQUE,
    uploaded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    file_name VARCHAR(50) NOT NULL,
    url VARCHAR(255) UNIQUE
);

CREATE SEQUENCE indication_id_seq;
CREATE TABLE indication (
    id BIGINT DEFAULT nextval('indication_id_seq') PRIMARY KEY,
    created_at TIMESTAMP,
    meter_id BIGINT NOT NULL,
    value DECIMAL,
    original_image_info_id BIGINT NOT NULL,
    indication_image_info_id BIGINT NOT NULL,
    consumption INTEGER,
    CHECK (consumption >= 0),
    FOREIGN KEY (meter_id) REFERENCES meter(id),
    FOREIGN KEY (original_image_info_id) REFERENCES image_info(id),
    FOREIGN KEY (indication_image_info_id) REFERENCES image_info(id)
);

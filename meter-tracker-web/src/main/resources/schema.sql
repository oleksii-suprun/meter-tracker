CREATE TABLE digit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    image BLOB NOT NULL,
    value VARCHAR(255)
);

CREATE TABLE meter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(20),
    capacity INTEGER NOT NULL,
    minor_digits INTEGER NOT NULL,
    CHECK (capacity > minor_digits)
);

CREATE TABLE indication (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    created TIMESTAMP,
    hash VARCHAR(255) UNIQUE NOT NULL,
    meter_id BIGINT NOT NULL,
    uploaded TIMESTAMP NOT NULL,
    value DOUBLE,
    consumption INTEGER,
    CHECK consumption >= 0,
    FOREIGN KEY (meter_id) REFERENCES meter (id)
);

CREATE TABLE asset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    data BLOB NOT NULL
);

CREATE TABLE indication_asset_binding (
    type INTEGER NOT NULL,
    indication_id BIGINT NOT NULL,
    asset_id BIGINT,
    PRIMARY KEY (indication_id, type),
    FOREIGN KEY (indication_id) REFERENCES indication (id),
    FOREIGN KEY (asset_id) REFERENCES asset (id)
);

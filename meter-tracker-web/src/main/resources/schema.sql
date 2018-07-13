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

CREATE TABLE image_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    storage_id VARCHAR(50) NOT NULL,
    hash VARCHAR(255) UNIQUE,
    uploaded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    file_name VARCHAR(50) NOT NULL,
    url VARCHAR(255) UNIQUE
);

CREATE TABLE indication (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    created_at TIMESTAMP,
    meter_id BIGINT NOT NULL,
    value DOUBLE,
    original_image_info_id BIGINT NOT NULL,
    indication_image_info_id BIGINT NOT NULL,
    consumption INTEGER,
    CHECK consumption >= 0,
    FOREIGN KEY (meter_id) REFERENCES meter(id),
    FOREIGN KEY (original_image_info_id) REFERENCES image_info(id),
    FOREIGN KEY (indication_image_info_id) REFERENCES image_info(id)
);

CREATE TABLE dashboard_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(255),
    type VARCHAR(255)
);

CREATE TABLE dashboard_item_entry (
    meter_id BIGINT NOT NULL,
    dashboard_item_id BIGINT NOT NULL,
    color INTEGER NOT NULL,
    PRIMARY KEY (meter_id, dashboard_item_id),
    FOREIGN KEY (meter_id) REFERENCES meter(id),
    FOREIGN KEY (dashboard_item_id) REFERENCES dashboard_item(id)
);

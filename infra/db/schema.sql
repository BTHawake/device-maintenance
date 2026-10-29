CREATE DATABASE IF NOT EXISTS device_maintenance DEFAULT CHARSET utf8mb4;
USE device_maintenance;

DROP TABLE IF EXISTS repair_order;
DROP TABLE IF EXISTS equipment;



CREATE TABLE equipment
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    code    VARCHAR(32) NOT NULL UNIQUE CHECK (code = TRIM(code)),
    name    VARCHAR(64) NOT NULL,
    model   VARCHAR(32) NOT NULL,
    site_id VARCHAR(16) NOT NULL,
    state   VARCHAR(16) NOT NULL,
    version INT         NOT NULL DEFAULT 1
);


CREATE TABLE repair_order
(
    id            VARCHAR(32) PRIMARY KEY,
    equipment_id  BIGINT       NOT NULL,
    site_id       VARCHAR(16)  NOT NULL,
    issue_summary VARCHAR(255) NOT NULL,
    priority      VARCHAR(16)  NOT NULL,
    status        VARCHAR(16)  NOT NULL,
    created_at    DATETIME     NOT NULL,
    CONSTRAINT fk_order_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);
CREATE INDEX idx_order_equipment ON repair_order (equipment_id);

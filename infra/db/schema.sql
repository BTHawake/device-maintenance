CREATE DATABASE IF NOT EXISTS device_maintenance DEFAULT CHARSET utf8mb4;
USE device_maintenance;

DROP TABLE IF EXISTS repair_order;
DROP TABLE IF EXISTS equipment;


CREATE TABLE equipment
(
    code    VARCHAR(32) primary key,
    name    VARCHAR(64) NOT NULL,
    model   VARCHAR(32) NOT NULL,
    site_id VARCHAR(16) NOT NULL,
    state   VARCHAR(16) NOT NULL,
    version INT         NOT NULL
);
ALTER TABLE equipment MODIFY version INT NOT NULL DEFAULT 1;

CREATE TABLE repair_order
(
    id             VARCHAR(32) PRIMARY KEY,
    equipment_code VARCHAR(32)  NOT NULL,
    site_id        VARCHAR(16)  NOT NULL,
    issue_summary  VARCHAR(255) NOT NULL,
    priority       VARCHAR(16)  NOT NULL,
    status         VARCHAR(16)  NOT NULL,
    created_at     DATETIME     NOT NULL,
    CONSTRAINT fk_order_equipment FOREIGN KEY (equipment_code) REFERENCES equipment (code)
);

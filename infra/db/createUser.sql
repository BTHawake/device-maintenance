CREATE USER IF NOT EXISTS 'device_app'@'localhost' IDENTIFIED BY 'DeviceApp2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON device_maintenance.* TO 'device_app'@'localhost';
FLUSH PRIVILEGES;
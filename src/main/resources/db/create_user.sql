-- Crear la base de datos para el sistema contable
CREATE DATABASE IF NOT EXISTS sistema_contable
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Crear un nuevo usuario para el sistema contable
CREATE USER IF NOT EXISTS 'contabilidad'@'localhost' IDENTIFIED BY 'walter120706';

-- Otorgar permisos necesarios
GRANT ALL PRIVILEGES ON sistema_contable.* TO 'contabilidad'@'localhost';

-- Aplicar los cambios de permisos
FLUSH PRIVILEGES;
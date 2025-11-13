-- Agregar columna rol a la tabla usuarios
ALTER TABLE usuarios ADD COLUMN rol VARCHAR(20) DEFAULT 'CLIENTE';

-- Actualizar el primer usuario registrado como ADMIN (si existe)
-- Asumiendo que el primer usuario tiene ID = 1
UPDATE usuarios SET rol = 'ADMIN' WHERE id = 1;

-- Actualizar los demás usuarios como CLIENTE
UPDATE usuarios SET rol = 'CLIENTE' WHERE id > 1;

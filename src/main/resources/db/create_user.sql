-- Crear un nuevo usuario para el sistema contable
CREATE USER contabilidad IDENTIFIED BY tupassword;

-- Otorgar permisos necesarios
GRANT CONNECT, RESOURCE TO contabilidad;
GRANT CREATE SESSION TO contabilidad;
GRANT CREATE TABLE TO contabilidad;
GRANT CREATE VIEW TO contabilidad;
GRANT CREATE SEQUENCE TO contabilidad;
GRANT UNLIMITED TABLESPACE TO contabilidad;
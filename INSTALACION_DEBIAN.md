# 🐧 Guía de Instalación en Debian 13 (VirtualBox)

Esta guía te ayudará a desplegar el Sistema Contable en Debian 13 dentro de VirtualBox.

## 📋 Requisitos

- VirtualBox instalado
- Debian 13 instalado en VirtualBox
- Conexión a Internet en la VM

## 🔧 Paso 1: Preparar Debian

### Actualizar el sistema
```bash
sudo apt update && sudo apt upgrade -y
```

### Instalar herramientas básicas
```bash
sudo apt install git curl wget -y
```

## ☕ Paso 2: Instalar Java 21

```bash
# Instalar OpenJDK 21
sudo apt install openjdk-21-jdk -y

# Verificar instalación
java -version
javac -version

# Configurar JAVA_HOME (opcional)
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

## 🔨 Paso 3: Instalar Maven

```bash
# Instalar Maven
sudo apt install maven -y

# Verificar instalación
mvn -version
```

## 🗄️ Paso 4: Instalar MySQL Server

```bash
# Instalar MySQL Server
sudo apt install mysql-server -y

# Verificar que MySQL está corriendo
sudo systemctl status mysql

# Iniciar MySQL si no está corriendo
sudo systemctl start mysql
sudo systemctl enable mysql

# Asegurar la instalación de MySQL (opcional pero recomendado)
sudo mysql_secure_installation
# Responde las preguntas:
# - Set root password? [Y/n] Y (establece una contraseña segura)
# - Remove anonymous users? [Y/n] Y
# - Disallow root login remotely? [Y/n] Y
# - Remove test database? [Y/n] Y
# - Reload privilege tables now? [Y/n] Y

# Verificar instalación
mysql --version
```

## 📥 Paso 5: Clonar el Proyecto

```bash
# Crear directorio para proyectos
mkdir -p ~/proyectos
cd ~/proyectos

# Clonar desde GitHub
git clone https://github.com/TU-USUARIO/sistema-contable.git
cd sistema-contable
```

## 🔑 Paso 6: Configurar la Base de Datos

### Conectar a MySQL como root

```bash
# Conectar a MySQL
sudo mysql -u root -p
# Ingresa la contraseña de root que estableciste
```

### Ejecutar Scripts SQL

```sql
-- 1. Crear base de datos y usuario (ejecuta línea por línea)
CREATE DATABASE IF NOT EXISTS sistema_contable
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'contabilidad'@'localhost' IDENTIFIED BY 'walter120706';

GRANT ALL PRIVILEGES ON sistema_contable.* TO 'contabilidad'@'localhost';

FLUSH PRIVILEGES;

-- 2. Verificar que se creó correctamente
SHOW DATABASES;
SELECT User, Host FROM mysql.user WHERE User='contabilidad';

-- 3. Salir
EXIT;
```

### Verificar conexión con el nuevo usuario

```bash
# Conectar con el usuario creado
mysql -u contabilidad -p sistema_contable
# Contraseña: walter120706

# Dentro de MySQL, verifica la base de datos
SHOW TABLES;
EXIT;
```

**Nota:** Las tablas se crearán automáticamente cuando ejecutes la aplicación Spring Boot por primera vez (gracias a `spring.jpa.hibernate.ddl-auto=update`).

## ⚙️ Paso 7: Configurar application.properties

```bash
# Editar el archivo
nano src/main/resources/application.properties
```

La configuración por defecto ya está lista para MySQL:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistema_contable
spring.datasource.username=contabilidad
spring.datasource.password=walter120706
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

Si cambiaste la contraseña del usuario, actualiza la línea correspondiente.

Guarda con `Ctrl+O`, Enter, y sal con `Ctrl+X`.

## 🚀 Paso 8: Compilar y Ejecutar

```bash
# Compilar el proyecto
mvn clean install -DskipTests

# Ejecutar la aplicación
mvn spring-boot:run

# O ejecutar el JAR generado
java -jar target/contabilidad-1.0.0.jar
```

## 🌐 Paso 9: Acceder desde Windows

### Configurar Red en VirtualBox

1. Apaga la VM
2. VirtualBox → Tu VM → Configuración → Red
3. Adaptador 1 → Cambiar de "NAT" a "Adaptador Puente"
4. Enciende la VM

### Obtener IP en Debian

```bash
ip addr show
# Busca la IP de tu adaptador (ej: 192.168.1.100)
```

### Acceder desde Windows

Abre el navegador en Windows:
```
http://IP-DE-DEBIAN:8080/api/contabilidad/login
```

Ejemplo:
```
http://192.168.1.100:8080/api/contabilidad/login
```

## 🔥 Configurar Firewall (si es necesario)

```bash
# Permitir el puerto 8080
sudo ufw allow 8080/tcp
sudo ufw reload
```

## 🛑 Comandos Útiles

### Detener la aplicación
```bash
# Si ejecutaste con mvn spring-boot:run
Ctrl+C

# Si hay procesos Java corriendo
pkill -f spring-boot
```

### Ver logs de MySQL
```bash
sudo tail -f /var/log/mysql/error.log
```

### Reiniciar MySQL
```bash
sudo systemctl restart mysql
```

### Ver servicios corriendo
```bash
sudo netstat -tulpn | grep :8080
sudo netstat -tulpn | grep :3306
```

## 🎯 Ejecutar como Servicio (Opcional)

Para que la aplicación inicie automáticamente:

```bash
# Crear servicio systemd
sudo nano /etc/systemd/system/sistema-contable.service
```

Contenido:
```ini
[Unit]
Description=Sistema Contable UNICAES
After=syslog.target

[Service]
User=tu_usuario
ExecStart=/usr/bin/java -jar /home/tu_usuario/proyectos/sistema-contable/target/contabilidad-1.0.0.jar
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

Activar:
```bash
sudo systemctl enable sistema-contable
sudo systemctl start sistema-contable
sudo systemctl status sistema-contable
```

## ✅ Verificación Final

1. ✅ MySQL corriendo: `sudo systemctl status mysql`
2. ✅ Aplicación corriendo: `curl http://localhost:8080/api/contabilidad/login`
3. ✅ Acceso desde Windows: Navegar a `http://IP-DEBIAN:8080/api/contabilidad/login`

## 🐛 Solución de Problemas

### MySQL no inicia
```bash
# Ver logs
sudo journalctl -u mysql.service -n 50

# Ver estado
sudo systemctl status mysql

# Reiniciar servicio
sudo systemctl restart mysql
```

### Aplicación no conecta a MySQL
```bash
# Verificar que MySQL esté escuchando
sudo netstat -tulpn | grep 3306

# Verificar conexión
mysql -u contabilidad -p sistema_contable

# Verificar permisos del usuario
sudo mysql -u root -p -e "SHOW GRANTS FOR 'contabilidad'@'localhost';"
```

### Error "Access denied"
```bash
# Recrear usuario
sudo mysql -u root -p
# En MySQL:
DROP USER IF EXISTS 'contabilidad'@'localhost';
CREATE USER 'contabilidad'@'localhost' IDENTIFIED BY 'walter120706';
GRANT ALL PRIVILEGES ON sistema_contable.* TO 'contabilidad'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Puerto 8080 ocupado
```bash
# Ver qué usa el puerto
sudo lsof -i :8080

# Matar proceso
sudo kill -9 PID
```

## 📝 Notas Importantes

- MySQL se inicia instantáneamente (a diferencia de Oracle que puede tardar minutos)
- Asegúrate de tener al menos 1GB de RAM asignados a la VM
- El usuario ADMIN se crea automáticamente al registrar el primer usuario
- Las tablas se crean automáticamente gracias a Hibernate

## 🎓 Para Presentación

Si necesitas demostrar el sistema:
1. Verifica MySQL: `sudo systemctl status mysql`
2. Si no está corriendo: `sudo systemctl start mysql`
3. Ejecuta la app: `mvn spring-boot:run`
4. Accede desde cualquier navegador en la red local

¡Listo! 🎉

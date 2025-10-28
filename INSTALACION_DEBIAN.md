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

## 🗄️ Paso 4: Instalar Oracle Database

### Opción A: Oracle XE con Docker (Recomendado)

```bash
# Instalar Docker
sudo apt install docker.io -y
sudo systemctl start docker
sudo systemctl enable docker

# Agregar tu usuario al grupo docker
sudo usermod -aG docker $USER
newgrp docker

# Descargar Oracle Database XE 21c
docker pull container-registry.oracle.com/database/express:21.3.0-xe

# Ejecutar Oracle en Docker
docker run -d \
  --name oracle-xe \
  -p 1521:1521 \
  -p 5500:5500 \
  -e ORACLE_PWD=Admin123 \
  -e ORACLE_CHARACTERSET=AL32UTF8 \
  container-registry.oracle.com/database/express:21.3.0-xe

# Esperar a que Oracle inicie (puede tomar 5-10 minutos)
docker logs -f oracle-xe
```

### Opción B: Oracle XE Nativo

```bash
# Descargar Oracle XE desde el sitio oficial
# https://www.oracle.com/database/technologies/xe-downloads.html

# Instalar dependencias
sudo apt install alien libaio1 -y

# Convertir RPM a DEB
sudo alien -i oracle-database-xe-21c*.rpm

# Configurar Oracle
sudo /etc/init.d/oracle-xe-21c configure
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

### Conectar a Oracle

```bash
# Si usas Docker
docker exec -it oracle-xe sqlplus sys/Admin123@XEPDB1 as sysdba

# Si usas instalación nativa
sqlplus sys/Admin123@localhost:1521/XEPDB1 as sysdba
```

### Ejecutar Scripts SQL

```sql
-- 1. Crear usuario (ya conectado como sys)
@src/main/resources/db/create_user.sql

-- 2. Conectar como el nuevo usuario
CONNECT contabilidad_user/password123@XEPDB1

-- 3. Crear tablas
@src/main/resources/db/create_usuarios.sql
@src/main/resources/db/add_rol_column.sql

-- Salir
EXIT;
```

## ⚙️ Paso 7: Configurar application.properties

```bash
# Editar el archivo
nano src/main/resources/application.properties
```

Ajusta estas líneas:
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=contabilidad_user
spring.datasource.password=password123
```

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

### Ver logs de Oracle Docker
```bash
docker logs -f oracle-xe
```

### Reiniciar Oracle Docker
```bash
docker restart oracle-xe
```

### Ver servicios corriendo
```bash
sudo netstat -tulpn | grep :8080
sudo netstat -tulpn | grep :1521
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

1. ✅ Oracle corriendo: `docker ps` o `lsnrctl status`
2. ✅ Aplicación corriendo: `curl http://localhost:8080/api/contabilidad/login`
3. ✅ Acceso desde Windows: Navegar a `http://IP-DEBIAN:8080/api/contabilidad/login`

## 🐛 Solución de Problemas

### Oracle no inicia
```bash
# Ver logs
docker logs oracle-xe

# Reiniciar contenedor
docker restart oracle-xe
```

### Aplicación no conecta a Oracle
```bash
# Verificar que Oracle esté escuchando
docker exec oracle-xe lsnrctl status

# Verificar conexión
docker exec -it oracle-xe sqlplus sys/Admin123@XEPDB1 as sysdba
```

### Puerto 8080 ocupado
```bash
# Ver qué usa el puerto
sudo lsof -i :8080

# Matar proceso
sudo kill -9 PID
```

## 📝 Notas Importantes

- Oracle en Docker tarda ~5-10 minutos en iniciar la primera vez
- Asegúrate de tener al menos 2GB de RAM asignados a la VM
- El usuario ADMIN se crea automáticamente al registrar el primer usuario

## 🎓 Para Presentación

Si necesitas demostrar el sistema:
1. Inicia Oracle: `docker start oracle-xe`
2. Espera 2-3 minutos
3. Ejecuta la app: `mvn spring-boot:run`
4. Accede desde cualquier navegador en la red local

¡Listo! 🎉

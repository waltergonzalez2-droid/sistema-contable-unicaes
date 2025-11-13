# 🏦 Sistema Contable UNICAES

Sistema de Contabilidad desarrollado con Spring Boot para la gestión de cuentas contables, asientos contables y reportes financieros.

## 📋 Características

- ✅ **Gestión de Cuentas Contables**: Catálogo de cuentas con jerarquía
- ✅ **Asientos Contables**: Registro, edición y anulación de asientos
- ✅ **Control de Roles**: Sistema de usuarios con roles ADMIN y CLIENTE
- ✅ **Reportes Financieros**: 
  - Balance General
  - Estado de Resultados
  - Flujo de Efectivo
  - Balance de Comprobación
- ✅ **Períodos Contables**: Gestión de períodos fiscales
- ✅ **Estados de Asientos**: BORRADOR → REGISTRADO → ANULADO

## 🛠️ Tecnologías Utilizadas

- **Backend**: Spring Boot 3.5.0
- **Java**: 21
- **Base de Datos**: MySQL 8.0+
- **ORM**: Hibernate/JPA
- **Seguridad**: Spring Security
- **Frontend**: Thymeleaf + Tailwind CSS
- **Build Tool**: Maven

## 📦 Requisitos Previos

- Java JDK 21 o superior
- Maven 3.8+
- MySQL 8.0+ o MariaDB 10.5+
- Git (para clonar el repositorio)

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU-USUARIO/sistema-contable.git
cd sistema-contable
```

### 2. Configurar la Base de Datos

Ejecuta el siguiente script SQL en MySQL como root o usuario con privilegios:

```bash
# Conectar a MySQL como root
mysql -u root -p

# Ejecutar el script de creación
source src/main/resources/db/create_user.sql
```

Este script creará:
- La base de datos `sistema_contable`
- El usuario `contabilidad` con contraseña `walter120706`
- Los permisos necesarios para el usuario

Luego, si deseas crear las tablas manualmente (opcional, Hibernate las creará automáticamente):

```bash
# Conectar con el usuario creado
mysql -u contabilidad -p sistema_contable

# Ejecutar scripts de tablas (opcional)
source src/main/resources/db/create_usuarios.sql
source src/main/resources/db/add_rol_column.sql
```

### 3. Configurar application.properties

La configuración por defecto ya está lista para MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistema_contable
spring.datasource.username=contabilidad
spring.datasource.password=walter120706
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

Si necesitas cambiar las credenciales, edita `src/main/resources/application.properties`.

### 4. Compilar y Ejecutar

```bash
# Compilar
mvn clean install -DskipTests

# Ejecutar
mvn spring-boot:run
```

### 5. Acceder a la aplicación

Abre tu navegador en: `http://localhost:8080/api/contabilidad/login`

## 👥 Usuarios por Defecto

- **Primer usuario registrado**: Automáticamente se convierte en ADMIN
- **Usuarios siguientes**: Se registran como CLIENTE

## 🔐 Roles y Permisos

### ADMIN
- Gestión completa de cuentas contables
- Gestión de períodos contables
- Crear, editar, registrar y anular asientos
- Eliminar asientos en estado BORRADOR o ANULADO
- Ver todos los reportes

### CLIENTE
- Crear y editar asientos en estado BORRADOR
- Registrar asientos
- Ver reportes financieros
- Sin acceso a gestión de cuentas y períodos

## 📊 Flujo de Trabajo de Asientos

1. **BORRADOR**: Se crea el asiento (se puede editar y eliminar)
2. **REGISTRADO**: Se registra el asiento (afecta los saldos de las cuentas)
3. **ANULADO**: Se anula el asiento (revierte los saldos, luego se puede eliminar)

## 🎨 Colores UNICAES

- Rojo Vino: `#7D3C3C`
- Dorado: `#D4AF37`
- Rojo Oscuro: `#5A2D2D`

## 📁 Estructura del Proyecto

```
Sistema Contable/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/unicaes/contabilidad/
│   │   │       ├── config/          # Configuración de seguridad
│   │   │       ├── controller/      # Controladores MVC
│   │   │       ├── model/           # Entidades JPA
│   │   │       ├── repository/      # Repositorios JPA
│   │   │       └── service/         # Lógica de negocio
│   │   └── resources/
│   │       ├── templates/           # Vistas Thymeleaf
│   │       ├── db/                  # Scripts SQL
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## 🐛 Solución de Problemas

### Error de conexión a MySQL
```bash
# Verifica que MySQL esté corriendo
sudo systemctl status mysql

# Verifica las credenciales en application.properties
# Verifica que el usuario 'contabilidad' tenga permisos
mysql -u root -p -e "SHOW GRANTS FOR 'contabilidad'@'localhost';"
```

### Error "Public Key Retrieval is not allowed"
Si encuentras este error, agrega `allowPublicKeyRetrieval=true` a la URL:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistema_contable?allowPublicKeyRetrieval=true
```

### Puerto 8080 ya en uso
```bash
# Windows
taskkill /F /IM java.exe

# Linux
kill -9 $(lsof -t -i:8080)
```

## 📝 Licencia

Este proyecto fue desarrollado para fines académicos en UNICAES.

## 👨‍💻 Autor

Desarrollado por [Tu Nombre] - UNICAES

## 📞 Contacto

Para preguntas o soporte, contacta a [tu-email@example.com]

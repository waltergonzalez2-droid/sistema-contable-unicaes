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
- **Base de Datos**: Oracle Database 21c
- **ORM**: Hibernate/JPA
- **Seguridad**: Spring Security
- **Frontend**: Thymeleaf + Tailwind CSS
- **Build Tool**: Maven

## 📦 Requisitos Previos

- Java JDK 21 o superior
- Maven 3.8+
- Oracle Database 21c (o Oracle XE)
- Git (para clonar el repositorio)

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU-USUARIO/sistema-contable.git
cd sistema-contable
```

### 2. Configurar la Base de Datos

Ejecuta los siguientes scripts SQL en Oracle:

```sql
-- 1. Crear usuario
@src/main/resources/db/create_user.sql

-- 2. Crear tabla de usuarios
@src/main/resources/db/create_usuarios.sql

-- 3. Agregar columna de rol
@src/main/resources/db/add_rol_column.sql
```

### 3. Configurar application.properties

Edita `src/main/resources/application.properties` con tus credenciales:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```

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

### Error de conexión a Oracle
```bash
# Verifica que Oracle esté corriendo
lsnrctl status

# Verifica las credenciales en application.properties
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

# Guía de Instalación - Sistema Contable

## 1. Instalación de Java JDK 21

1. **Descargar JDK 21:**
   - Visita: https://adoptium.net/ (Eclipse Temurin)
   - Descarga "JDK 21 LTS" para Windows x64 Installer
   - O visita: https://www.oracle.com/java/technologies/downloads/#java21

2. **Instalar JDK 21:**
   - Ejecuta el archivo descargado
   - Acepta los términos y condiciones
   - Usa la ruta de instalación por defecto
   - Completa la instalación

3. **Configurar Variables de Entorno:**
   - Abre el Panel de Control
   - Ve a Sistema y Seguridad > Sistema
   - Haz clic en "Configuración avanzada del sistema"
   - Click en "Variables de entorno"
   - En "Variables del sistema":
     * Crea nueva variable JAVA_HOME:
       - Nombre: `JAVA_HOME`
       - Valor: `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot` (ajusta según tu versión)
     * Edita la variable PATH:
       - Click en "PATH" y luego en "Editar"
       - Click en "Nuevo"
       - Agrega: `%JAVA_HOME%\bin`
       - Click en "Aceptar" en todas las ventanas

4. **Verificar instalación:**
   - Abre una nueva ventana de PowerShell
   - Escribe: `java -version`
   - Deberías ver la versión 21.x.x de Java

## 2. Instalación de Maven

1. **Descargar Maven:**
   - Visita: https://maven.apache.org/download.cgi
   - Descarga "Binary zip archive" (apache-maven-x.x.x-bin.zip)

2. **Instalar Maven:**
   - Crea la carpeta: `C:\Program Files\Apache\maven`
   - Descomprime el contenido del zip en esa carpeta
   - El resultado debe ser algo como: `C:\Program Files\Apache\maven\bin`, `C:\Program Files\Apache\maven\lib`, etc.

3. **Configurar Variables de Entorno:**
   - Abre nuevamente Variables de entorno
   - En "Variables del sistema":
     * Crea nueva variable MAVEN_HOME:
       - Nombre: `MAVEN_HOME`
       - Valor: `C:\Program Files\Apache\maven`
     * Edita la variable PATH:
       - Agrega: `%MAVEN_HOME%\bin`

4. **Verificar instalación:**
   - Abre una nueva ventana de PowerShell
   - Escribe: `mvn -version`
   - Deberías ver la versión de Maven y la versión de Java

## 3. Instalación de MySQL

1. **Descargar MySQL:**
   - Visita: https://dev.mysql.com/downloads/installer/
   - Descarga "MySQL Installer for Windows"
   - Elige la versión completa (mysql-installer-community)

2. **Instalar MySQL:**
   - Ejecuta el instalador descargado
   - Selecciona "Developer Default" o "Server only"
   - Click en "Execute" para descargar e instalar los componentes
   - Configura MySQL Server:
     * Puerto: 3306 (por defecto)
     * Establece una contraseña para el usuario root
     * **¡IMPORTANTE! Guarda esta contraseña**
   - Completa la instalación

3. **Verificar instalación:**
   - Abre PowerShell o CMD
   - Escribe: `mysql --version`
   - Deberías ver la versión de MySQL instalada

4. **Instalar MySQL Workbench (opcional, viene incluido):**
   - MySQL Workbench se instala automáticamente con el instalador
   - Es una herramienta gráfica para administrar bases de datos
   - Puedes usarlo para ejecutar scripts SQL visualmente

## 4. Configuración del Proyecto

1. **Configurar la base de datos:**
   
   **Opción A - Usando línea de comandos:**
   - Abre PowerShell o CMD
   - Conéctate a MySQL como root:
     ```cmd
     mysql -u root -p
     ```
   - Ingresa la contraseña de root
   - Ejecuta el script:
     ```sql
     source C:\ruta\al\proyecto\src\main\resources\db\create_user.sql
     ```
   
   **Opción B - Usando MySQL Workbench:**
   - Abre MySQL Workbench
   - Conéctate como root usando la contraseña establecida
   - Ve a File > Open SQL Script
   - Selecciona el archivo: `src/main/resources/db/create_user.sql`
   - Ejecuta el script (click en el icono del rayo)
   
   Este script creará:
   - La base de datos `sistema_contable`
   - El usuario `contabilidad` con contraseña `walter120706`
   - Los permisos necesarios

2. **Configurar la conexión:**
   - Abre el archivo: `src/main/resources/application.properties`
   - Verifica que la configuración sea correcta:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/sistema_contable
     spring.datasource.username=contabilidad
     spring.datasource.password=walter120706
     ```
   - Si cambiaste la contraseña en el script SQL, actualízala aquí también

## 5. Compilar y Ejecutar el Proyecto

1. **Compilar el proyecto:**
   - Abre PowerShell
   - Navega al directorio del proyecto:
     ```powershell
     cd "C:\xampp\htdocs\primera\Sistema Contable"
     ```
   - Ejecuta:
     ```powershell
     mvn clean install
     ```

2. **Ejecutar el proyecto:**
   ```powershell
   mvn spring-boot:run
   ```

3. **Verificar que está funcionando:**
   - Abre un navegador
   - Visita: http://localhost:8080/api/contabilidad/login
   - Deberías ver la página de inicio de sesión

## Solución de Problemas Comunes

1. **Error "java no se reconoce como un comando interno":**
   - Revisa la configuración de JAVA_HOME
   - Asegúrate de haber reiniciado PowerShell

2. **Error "mvn no se reconoce como un comando interno":**
   - Revisa la configuración de MAVEN_HOME
   - Asegúrate de haber reiniciado PowerShell

3. **Error de conexión a MySQL:**
   - Verifica que el servicio MySQL está corriendo:
     ```cmd
     net start | findstr MySQL
     ```
   - Si no está corriendo, inícialo:
     ```cmd
     net start MySQL80
     ```
   - Verifica el usuario y contraseña en application.properties
   - Verifica el puerto (3306 por defecto)
   - Verifica que la base de datos existe:
     ```cmd
     mysql -u root -p -e "SHOW DATABASES;"
     ```

4. **Error "Access denied for user 'contabilidad'@'localhost'":**
   - Verifica que ejecutaste el script create_user.sql
   - Verifica los permisos del usuario:
     ```cmd
     mysql -u root -p -e "SHOW GRANTS FOR 'contabilidad'@'localhost';"
     ```

5. **Error "Unknown database 'sistema_contable'":**
   - Ejecuta nuevamente el script create_user.sql
   - O crea la base de datos manualmente:
     ```cmd
     mysql -u root -p -e "CREATE DATABASE sistema_contable CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
     ```

4. **Error al compilar el proyecto:**
   - Asegúrate de tener conexión a internet (para descargar dependencias)
   - Verifica que estás en el directorio correcto
   - Verifica que tienes permisos de escritura en el directorio
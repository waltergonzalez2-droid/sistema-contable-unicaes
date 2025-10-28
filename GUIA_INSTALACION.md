# Guía de Instalación - Sistema Contable

## 1. Instalación de Java JDK 11

1. **Descargar JDK 11:**
   - Visita: https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
   - Descarga "Windows x64 Installer" (jdk-11.0.x_windows-x64_bin.exe)
   - Necesitarás crear una cuenta gratuita de Oracle para la descarga

2. **Instalar JDK 11:**
   - Ejecuta el archivo descargado (jdk-11.0.x_windows-x64_bin.exe)
   - Acepta los términos y condiciones
   - Usa la ruta de instalación por defecto: `C:\Program Files\Java\jdk-11`
   - Completa la instalación

3. **Configurar Variables de Entorno:**
   - Abre el Panel de Control
   - Ve a Sistema y Seguridad > Sistema
   - Haz clic en "Configuración avanzada del sistema"
   - Click en "Variables de entorno"
   - En "Variables del sistema":
     * Crea nueva variable JAVA_HOME:
       - Nombre: `JAVA_HOME`
       - Valor: `C:\Program Files\Java\jdk-11`
     * Edita la variable PATH:
       - Click en "PATH" y luego en "Editar"
       - Click en "Nuevo"
       - Agrega: `%JAVA_HOME%\bin`
       - Click en "Aceptar" en todas las ventanas

4. **Verificar instalación:**
   - Abre una nueva ventana de PowerShell
   - Escribe: `java -version`
   - Deberías ver la versión 11.x.x de Java

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

## 3. Instalación de Oracle Database XE

1. **Descargar Oracle Database XE:**
   - Visita: https://www.oracle.com/database/technologies/xe-downloads.html
   - Descarga "Oracle Database XE"
   - Necesitarás una cuenta de Oracle

2. **Instalar Oracle Database XE:**
   - Ejecuta el archivo descargado
   - Selecciona instalación típica
   - Establece una contraseña para el usuario SYS y SYSTEM
   - **¡IMPORTANTE! Guarda esta contraseña**
   - Puerto por defecto: 1521
   - Completa la instalación

3. **Instalar SQL Developer:**
   - Visita: https://www.oracle.com/tools/downloads/sqldev-downloads.html
   - Descarga SQL Developer (asegúrate de que sea compatible con JDK 11)
   - Descomprime el archivo en una ubicación de tu elección

## 4. Configuración del Proyecto

1. **Configurar la base de datos:**
   - Abre SQL Developer
   - Conéctate como SYSTEM usando la contraseña que estableciste
   - Ejecuta el script ubicado en: `src/main/resources/db/create_user.sql`
   - Modifica la contraseña en el script según tus necesidades

2. **Configurar la conexión:**
   - Abre el archivo: `src/main/resources/application.properties`
   - Modifica:
     ```properties
     spring.datasource.username=contabilidad
     spring.datasource.password=tupassword
     ```
   - Reemplaza "tupassword" con la contraseña que estableciste

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
   - Visita: http://localhost:8080/api/contabilidad/cuentas
   - Deberías ver una respuesta JSON (probablemente una lista vacía al inicio)

## Solución de Problemas Comunes

1. **Error "java no se reconoce como un comando interno":**
   - Revisa la configuración de JAVA_HOME
   - Asegúrate de haber reiniciado PowerShell

2. **Error "mvn no se reconoce como un comando interno":**
   - Revisa la configuración de MAVEN_HOME
   - Asegúrate de haber reiniciado PowerShell

3. **Error de conexión a Oracle:**
   - Verifica que el servicio de Oracle está corriendo
   - Verifica el usuario y contraseña
   - Verifica el puerto (1521 por defecto)

4. **Error al compilar el proyecto:**
   - Asegúrate de tener conexión a internet (para descargar dependencias)
   - Verifica que estás en el directorio correcto
   - Verifica que tienes permisos de escritura en el directorio
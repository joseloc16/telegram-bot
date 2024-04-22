# telegram-bot

# Configuración de Entorno para Telegram Bot

Este documento proporciona una guía detallada para configurar el entorno necesario para ejecutar el bot de Telegram, incluyendo la base de datos y la conexión desde una aplicación Spring Boot.

## Configuración de Docker con PostgreSQL

1. **Descargar la Imagen de PostgreSQL**:
   ```bash
    docker pull postgres
   
2. **Iniciar Contenedor de PostgreSQL**:
   ```bash
    docker run --name telegram-bot-container -e POSTGRES_USER=userbot -e POSTGRES_PASSWORD=passbot -e POSTGRES_DB=telegrambot-db -p 5431:5432 -d postgres
Este comando inicia un nuevo contenedor llamado telegram-bot-container corriendo PostgreSQL y establece las variables de entorno necesarias para la configuración inicial.

3. **Acceder al Contenedor de PostgreSQL**
    ```bash
    docker exec -it telegram-bot-container psql -U userbot -d telegrambot-db
Utilice este comando para acceder a la base de datos desde la línea de comando dentro del contenedor.

## Creación de la Estructura de la Base de Datos

Dentro de la sesión de PostgreSQL, ejecute los siguientes comandos SQL para configurar su base de datos:

1. **Crear Secuencia**
    ```bash
    CREATE SEQUENCE seq_words_detect INCREMENT 1 MINVALUE 1 START 1 CACHE 20;

2. **Crear Tabla**
    ```bash
    CREATE TABLE words_detect (
      id_word bigint NOT NULL DEFAULT nextval('seq_words_detect'),
      word VARCHAR(255) NOT NULL,
      PRIMARY KEY (id_word)
    );
    
Crea la tabla words_detect con una columna para ID y otra para la palabra detectada.

3. **Insertar Datos**
    ```bash
    INSERT INTO words_detect (word) VALUES ('cliente'), ('detectado'), ('actividad'), ('cuenta bancaria'), ('sospechosa'), ('premio'), ('ganado'), ('reclamar'), ('responda'), ('nombre'), ('direccion');

4. **Verificar los Datos**
    ```bash
    SELECT * FROM words_detect;

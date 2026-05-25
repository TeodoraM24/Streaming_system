# Streaming System – Local Setup Guide

## 1. Clone the Repository

Clone the project repository and open it in your IDE.

## 2. Start the Required Docker Containers

Before running the application, make sure the following containers are running:

* PostgreSQL
* MongoDB
* Neo4j

## 3. Create the PostgreSQL Database

Create a PostgreSQL database named:

```text
streaming_system
```

## 4. Run the SQL Scripts

Navigate to the `SQL` folder and run the SQL files in the following order:

1. `streaming-system-create-db-v2.sql`
   Creates the database tables and structure.

2. `streaming-system-v2-postgres-seed.sql`
   Inserts the required data into the database.

4. `audit_script.sql`
   Creates the stored objects, such as functions, procedures and triggers.

4. `indexes.sql`
   Creates the stored objects, such as functions, procedures and triggers.

5. `business_logic.sql`
   Creates the stored objects, such as functions, procedures and triggers.

After running these files, the PostgreSQL database is ready.

## 5. Configure `application.properties`

Place the `application.properties` file in:

```text
src/main/resources/application.properties
```

Make sure it contains the correct connection settings for:

* PostgreSQL
* MongoDB
* Neo4j

## 6. Run the Application

When the containers are running, the database is created, the SQL scripts are executed, and `application.properties` is configured correctly, run the project’s `main` class to start the application.

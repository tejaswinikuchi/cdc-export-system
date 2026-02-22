# CDC Export System – Production-Ready Incremental Data Export

## Overview

This project implements a production-ready, containerized data export system using Change Data Capture (CDC) principles. The system efficiently synchronizes user data by supporting full, incremental, and delta export strategies using timestamp-based watermarking.

Instead of exporting the entire dataset repeatedly, the system tracks the latest exported record for each consumer and processes only newly updated records. This ensures performance, scalability, and reliability.

---

## Architecture

The system consists of:

- Spring Boot backend application
- PostgreSQL database
- Docker-based containerization
- Watermark-based CDC logic
- Asynchronous export job handling
- CSV-based export output

All services are orchestrated using Docker Compose.

---

## Key Features

- Fully containerized using Docker and Docker Compose
- Automatic database initialization and seeding (100,000+ records)
- Timestamp-based CDC implementation
- Watermark tracking per consumer
- Asynchronous export processing
- Full, incremental, and delta export support
- Structured logging for export lifecycle events
- Automated test suite with 77% code coverage

---

## Database Schema

### users Table

| Column       | Type                         | Description |
|-------------|------------------------------|------------|
| id          | BIGSERIAL (Primary Key)      | Unique user identifier |
| name        | VARCHAR(255)                 | User's full name |
| email       | VARCHAR(255), UNIQUE         | User email address |
| created_at  | TIMESTAMP WITH TIME ZONE     | Record creation time |
| updated_at  | TIMESTAMP WITH TIME ZONE     | Last update time |
| is_deleted  | BOOLEAN (default false)      | Soft delete flag |

An index is created on the `updated_at` column to optimize incremental queries.



## Database Schema

### users Table

| Column       | Type                         | Description |
|-------------|------------------------------|------------|
| id          | BIGSERIAL (Primary Key)      | Unique user identifier |
| name        | VARCHAR(255)                 | User's full name |
| email       | VARCHAR(255), UNIQUE         | User email address |
| created_at  | TIMESTAMP WITH TIME ZONE     | Record creation time |
| updated_at  | TIMESTAMP WITH TIME ZONE     | Last update time |
| is_deleted  | BOOLEAN (default false)      | Soft delete flag |

An index is created on the `updated_at` column to optimize incremental queries.
---

### watermarks Table

| Column            | Type                         | Description |
|------------------|------------------------------|------------|
| id               | SERIAL (Primary Key)         | Unique identifier |
| consumer_id      | VARCHAR(255), UNIQUE         | Data consumer identifier |
| last_exported_at | TIMESTAMP WITH TIME ZONE     | Timestamp of last exported record |
| updated_at       | TIMESTAMP WITH TIME ZONE     | Watermark update timestamp |

Each consumer maintains an independent watermark.

---


## How to Run the Project

### Build and Start Services
docker compose up --build

This will:

- Start PostgreSQL
- Automatically seed the database with 100,000+ users
- Start the Spring Boot application
- Mount the `output/` directory for export files

---

## Health Check

To verify that the application is running:


GET http://localhost:8080/health

### 
Expected response:

{
"status": "ok",
"timestamp": "ISO-8601 timestamp"
}


## API Endpoints

### 1. Full Export


POST /exports/full
Header: X-Consumer-ID: consumer-1

Exports all non-deleted users and updates the watermark for the consumer.

---

### 2. Incremental Export


POST /exports/incremental
Header: X-Consumer-ID: consumer-1

Exports only records where `updated_at` is greater than the last stored watermark.

Soft-deleted records are excluded.

---

### 3. Delta Export


POST /exports/delta
Header: X-Consumer-ID: consumer-1

Exports changed records including an additional `operation` column:

- INSERT
- UPDATE
- DELETE

---

### 4. Get Watermark


GET /exports/watermark
Header: X-Consumer-ID: consumer-1

Returns the current watermark for the specified consumer.

---

## Export Output

All export files are written to:


./output

Generated file examples:

- full_consumer_timestamp.csv
- incremental_consumer_timestamp.csv
- delta_consumer_timestamp.csv

Each file is uniquely named and stored via Docker volume mapping.

---

## Running Tests

To execute tests:

docker run --rm -v "${PWD}:/app" -w /app maven:3.9.6-eclipse-temurin-17 mvn clean test


To view the coverage report:


target/site/jacoco/index.html


Current test coverage: **77%**

---

## Environment Variables

All configurable environment variables are documented in:


.env.example


This includes database configuration, server port, logging settings, and export directory configuration.

---

## Logging

The application logs structured export lifecycle events:

- Export job started
- Export job completed
- Export job failed

View logs using:


docker logs cdc-app


---

## Design Considerations

- Watermark updates occur only after successful export completion
- Each consumer is isolated using `consumer_id`
- Soft deletes are handled correctly in delta exports
- Indexing ensures scalable performance
- Architecture allows future extension to worker-based processing systems

---

## Submission Checklist

- docker-compose.yml
- Dockerfile
- .env.example
- README.md
- Source code
- seeds/ directory
- output/ directory (gitignored)
- Tests with ≥70% coverage

---

## Conclusion

This project demonstrates a scalable, production-ready CDC-based export architecture suitable for real-world data synchronization between services or data warehouses.

It emphasizes correctness, performance, maintainability, and clean system design.

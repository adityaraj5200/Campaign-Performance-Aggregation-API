# Campaign Performance Aggregation API

Java 21 • Spring Boot 3.2.5 • JDBC • H2

This project implements the **Growth Fusion Backend Developer Assignment**, producing a unified daily performance view by merging:

* **Meta cost**
* **Snapchat cost**
* **Revenue table**

with correct **LA → UTC conversion**, **active-campaign filtering**, and **latest snapshot extraction**.

---

# Tech Stack

* **Java 21**
* **Spring Boot 3.2.5**
* **JdbcTemplate (no ORM)**
* **H2 in-memory database**
* **JUnit 5**
* **Lombok**

---

# Requirements

Before running:

* **Java 21**
* **Maven 3.9+**
* No additional DB setup required — H2 initializes automatically from `schema.sql` and `data.sql`.

---

# Features Implemented

### Core Business Logic

* GET `/api/v1/campaigns/active?date=YYYY-MM-DD`
* LA day → UTC window conversion
* Active campaign detection
* Latest cost snapshot per campaign
* Revenue aggregation
* Campaign-name normalization
* Full metric calculation
* Sorting by profit DESC → spend DESC

### Technical Features

* JdbcTemplate-based repositories
* Global exception handling
* Debug logging:
  * LA → UTC conversion
  * Query window
  * Rows fetched per table
  * Active snapshots count
  * Execution time

* Seed data auto-loaded from `data.sql`

---

# Project Structure

```
src/main/java/com/growthfusion/
│── controller/
│── service/
│   └── impl/
│── repository/
│── dto/
│── model/
│── util/
│── config/
│
src/main/resources/
│── application.properties
│── schema.sql
│── data.sql
│
src/test/java/com/growthfusion/
```

---

# Setup & Run

## Clone the project

```bash
git clone https://github.com/adityaraj5200/Campaign-Performance-Aggregation-API
cd "Campaign-Performance-Aggregation-API"
```

## Run with Maven

```bash
mvn spring-boot:run
```

## Or build & run

```bash
mvn clean package
java -jar target/campaign-performance-api-1.0.0.jar
```

---

# Example API Usage

### Please use this Postman Collection: https://aditya-team-7143.postman.co/workspace/blog-application~bf749697-64e8-4f24-94d0-4010f24c3367/collection/15813603-688c58c4-2af6-4740-80bf-6c45b531bab6?action=share&creator=15813603

### Get Active Campaigns

```
GET http://localhost:8080/api/v1/campaigns/active?date=2025-11-13
```

### Example SUCCESS Response

```json
[
  {
    "platform": "meta",
    "campaignName": "Organic Coffee Launch",
    "status": "ACTIVE",
    "lastCostEventTimeUtc": "2025-11-14T05:10:00",
    "spend": 240.0,
    "impressions": 17000,
    "revenue": 1830.0,
    "profit": 1590.0,
    "roas": 7.625,
    "roi": 6.625
  }
]
```

### Invalid Date Format

```
GET /api/v1/campaigns/active?date=13-11-2025
```

**Response:**

```json
{
  "error": "Invalid date format",
  "message": "Expected YYYY-MM-DD"
}
```

---

#️ H2 Database Console

URL:

```
http://localhost:8080/h2-console
```

Use:

```
JDBC URL: jdbc:h2:mem:growthfusiondb
Username: adityarajgrowthfusion
Password: password
```

Automatically initialized with `schema.sql` + `data.sql`.

---

# Design Decisions

## **Campaign-name normalization**

Real marketing data has spelling variations.
This project normalizes by:

* lowercasing
* trimming leading and trailing whitespaces
* collapsing spaces

Example:

```
"Organic Coffee Launch"
"organic coffee launch"
 → "organic coffee launch"
```

## **LA → UTC Day Window**

A date is interpreted in **Los Angeles timezone**:

```
2025-11-13 00:00:00 LA
→ 2025-11-13 08:00:00 UTC

2025-11-13 23:59:59 LA
→ 2025-11-14 07:59:59 UTC
```

## **Latest cost snapshot per campaign**

Filtering logic:

```
status = ACTIVE
OR (status IS NULL AND spend > 0)
```

Then pick the **latest event_time_la** in the UTC window.
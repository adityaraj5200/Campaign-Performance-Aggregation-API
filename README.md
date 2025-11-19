
# 📊 Campaign Performance Aggregation API  
Java 21 • Spring Boot 3.x • JDBC • H2

This project implements the **Growth Fusion Backend Developer Assignment**, producing a unified daily view of campaign performance by merging **Meta cost**, **Snapchat cost**, and **revenue** data with correct **LA → UTC** time handling.

---

# 🚀 Features Implemented
- GET `/api/v1/campaigns/active?date=YYYY-MM-DD`
- LA day → UTC window conversion (00:00–23:59:59 LA → 08:00–07:59:59 UTC)
- Active campaign detection  
  - `status = ACTIVE`  
  - OR `status IS NULL AND spend > 0`  
- Latest cost snapshot per campaign  
- Revenue aggregation from UTC window  
- Normalized joins (trim + lowercase)  
- All metrics computed:
  - profit, roas, roi
  - lpctr, epc, lpcpc
- Sorted by:
  - profit DESC
  - spend DESC
- JdbcTemplate-based repositories
- Fully modular code structure
- H2 in-memory DB with schema + data
- Postman collection included
- Java 21–compatible

---

# 📂 Project Structure
```

src/main/java/com/growthfusion/
controller/
service/
service/impl/
repository/
dto/
model/
util/
src/main/resources/
application.properties
schema.sql
data.sql
postman/
CampaignAPI.postman_collection.json
src/test/java/com/growthfusion/

```

---

# 🛠️ Setup Instructions

### 1. Clone the repository  
```

git clone <your-repo-url>
cd campaign-performance-api

```

### 2. Run with Maven  
```

mvn spring-boot:run

```

### 3. Or build & run  
```

mvn clean package
java -jar target/campaign-performance-api-1.0.0.jar

```

---

# 🧪 Testing the API

### Example Request  
```

GET [http://localhost:8080/api/v1/campaigns/active?date=2025-11-13](http://localhost:8080/api/v1/campaigns/active?date=2025-11-13)

```

### Example Error  
```

GET [http://localhost:8080/api/v1/campaigns/active?date=13-11-2025](http://localhost:8080/api/v1/campaigns/active?date=13-11-2025)

````

Response:
```json
{
  "error": "Invalid date format",
  "message": "Expected YYYY-MM-DD"
}
````

---

# 🧰 Postman Collection

Import:

```
postman/CampaignAPI.postman_collection.json
```

---

# 🗄️ H2 Database

Console available at:

```
http://localhost:8080/h2-console
```

Use:

```
JDBC URL: jdbc:h2:mem:campaign_db
User: sa
Password: <blank>
```

---

# 🧠 Design Decisions

### **1. JdbcTemplate over ORM**

The assignment emphasizes SQL + data modeling → JdbcTemplate provides full control and transparency.

### **2. Normalized joining**

Campaign names vary wildly in real marketing data → normalization guarantees reliable joins.

### **3. LA → UTC conversion**

Implemented using:

```
ZoneId.of("America/Los_Angeles")
ZoneId.of("UTC")
```

Ensures accuracy across DST.

### **4. Modular architecture**

* Services handle business logic
* Repositories handle data access
* Utilities isolate transformation logic
* DTOs define stable, API-safe formats

### **5. Robust metric safety**

All metrics gracefully handle nulls and divide-by-zero scenarios.

---

# 🧪 Tests Included

### **1. TimezoneConversionTest**

Validates LA→UTC window conversion for multiple dates.

### **2. AggregationJoinTest**

Loads H2 schema + data and asserts:

* correct snapshot extraction
* correct active filtering
* correct revenue aggregation
* correct metric values

---

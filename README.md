# 📦 Order Management System — Microservices with Kafka

A **microservices-based Order Management System** built with **Spring Boot 3**, **Apache Kafka**, and a **React + Ant Design** frontend. This project demonstrates event-driven architecture where services communicate asynchronously through Kafka topics.

> **📌 Project Scope & Focus Note (For Review):**  
> Due to strict timeline constraints, this phase deliberately focuses on the core **Apache Kafka Event-Driven Messaging Architecture (Producer-Consumer pipeline)**. Database persistence (RDBMS), live SMTP email servers, and user authentication/login screens were intentionally simplified into in-memory and simulated services to provide a self-contained, dependable Kafka demo. All secondary layers can be plugged in during Phase 2.

---

## 🏗️ Architecture Overview

```
┌──────────────┐       ┌──────────────┐       ┌──────────────────┐
│   React UI   │──────▶│ order-service │──────▶│   Apache Kafka   │
│  (Ant Design)│ REST  │   (port 8081) │ Pub   │  (Docker :9092)  │
└──────────────┘       └──────────────┘       └───────┬──────────┘
                                                      │
                                        ┌─────────────┼─────────────┐
                                        │ Subscribe   │ Subscribe   │
                                        ▼             ▼             │
                              ┌─────────────┐ ┌──────────────────┐ │
                              │  payment-   │ │   inventory-     │ │
                              │  service    │ │   service        │ │
                              │ (port 8082) │ │  (port 8083)     │ │
                              └──────┬──────┘ └───────┬──────────┘ │
                                     │ Pub            │ Pub        │
                                     ▼                ▼            │
                              payment-events    inventory-events   │
                              (Kafka topic)     (Kafka topic)      │
```

### Event Flow

1. **User** creates an order via the React UI
2. **order-service** receives the REST request, generates a UUID, and publishes an `OrderCreatedEvent` to the `order-events` Kafka topic
3. **payment-service** consumes the event, processes (simulated) payment, sends a confirmation email (with retry), and publishes `PaymentCompletedEvent` to `payment-events`
4. **inventory-service** consumes the same event, checks in-memory stock, decrements quantity if available, and publishes `InventoryCheckedEvent` to `inventory-events`

---

## 📁 Project Structure

```
OrderManagementSystem/
├── pom.xml                          # Parent POM (Maven multi-module)
├── src/main/java/.../oms/           # OMS Core — User management & authentication
│   ├── controller/UserController.java   # POST /api/auth/signup, GET /api/auth/test
│   ├── dto/SignupRequest.java           # {name, email, password, phoneNumber, address}
│   ├── entity/User.java                # JPA @Entity → "users" table
│   ├── repository/UserRepository.java  # Spring Data JPA repository
│   ├── service/UserService.java        # Interface
│   └── service/impl/UserServiceImpl.java  # Signup logic with duplicate-email check
│
├── common/                          # Shared event POJOs
│   └── src/main/java/.../events/
│       ├── OrderCreatedEvent.java   # {orderId, itemId, quantity}
│       ├── PaymentCompletedEvent.java  # {orderId, success}
│       └── InventoryCheckedEvent.java  # {orderId, inStock}
│
├── order-service/                   # Port 8081 — REST API + Kafka producer
│   └── src/main/java/.../
│       ├── controller/OrderController.java   # POST /orders, GET /orders
│       └── kafka/OrderKafkaProducer.java     # Publishes to "order-events"
│
├── payment-service/                 # Port 8082 — Kafka consumer + email
│   └── src/main/java/.../
│       ├── kafka/PaymentListener.java        # Listens "order-events", publishes "payment-events"
│       └── email/EmailService.java           # Sends email with @Retryable (3 attempts)
│
├── inventory-service/               # Port 8083 — Kafka consumer + stock check
│   └── src/main/java/.../
│       └── kafka/InventoryListener.java      # Listens "order-events", publishes "inventory-events"
│
└── frontend/                        # React UI (Vite + Ant Design)
    ├── package.json
    ├── vite.config.js               # Dev proxy /api → localhost:8081
    └── src/
        ├── App.jsx                  # Layout, routing, navigation
        ├── components/
        │   ├── OrderList.jsx        # AntD Table — lists all orders
        │   └── CreateOrder.jsx      # AntD Form — creates a new order
        └── utils/
            └── kafkaHealth.ts       # Health check toast on page load
```

---

## 🛠️ Tech Stack

| Layer       | Technology                               |
|-------------|------------------------------------------|
| **Backend** | Java 17, Spring Boot 3.2.5, Spring Kafka |
| **Broker**  | Apache Kafka (Docker — KRaft mode)       |
| **Frontend**| React 18, Vite, Ant Design, Axios        |
| **Build**   | Maven (multi-module), npm               |
| **Other**   | Lombok, Spring Retry, Jackson JSON       |

---

## 🚀 Prerequisites

- **Java 17** (JDK) — `java -version`
- **Maven** (or use the included `mvnw` wrapper)
- **Docker Desktop** — for running Kafka
- **Node.js 18+** and **npm** — for the React frontend

---

## 🏃 How to Run

### 1. Start Kafka (Docker)

```bash
docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  apache/kafka:latest
```

Verify: `docker ps` should show the kafka container running.

### 2. Build the Backend

```bash
cd OrderManagementSystem
./mvnw clean install
```

### 3. Start the Microservices

Open **three separate terminals**:

```bash
# Terminal 1 — Order Service (port 8081)
./mvnw spring-boot:run -pl order-service

# Terminal 2 — Payment Service (port 8082)
./mvnw spring-boot:run -pl payment-service

# Terminal 3 — Inventory Service (port 8083)
./mvnw spring-boot:run -pl inventory-service
```

### 4. Start the React Frontend

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:3000** in your browser.

---

## 🧪 Demo Walkthrough

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Open `http://localhost:3000` | UI loads, Kafka health toast appears (green = broker reachable) |
| 2 | Click **Create Order** | Order form opens |
| 3 | Enter Item ID: `A1`, Quantity: `2`, click **Create Order** | Success toast, auto-redirects to Orders page |
| 4 | Check **Orders** page | New order with UUID, itemId=A1, quantity=2 is listed |
| 5 | Check **order-service** terminal | Log: `Published OrderCreatedEvent to order-events` |
| 6 | Check **payment-service** terminal | Log: `Received OrderCreatedEvent`, sends email, publishes `PaymentCompletedEvent` |
| 7 | Check **inventory-service** terminal | Log: `Received OrderCreatedEvent`, item A1 stock decremented, publishes `InventoryCheckedEvent` |

### Pre-loaded Inventory (for demo)

| Item ID | Available Qty |
|---------|---------------|
| A1      | 10            |
| B2      | 5             |
| C3      | 0 (out of stock) |

---

## 📡 Kafka Topics

| Topic              | Producer          | Consumer(s)                           |
|--------------------|-------------------|---------------------------------------|
| `order-events`     | order-service     | payment-service, inventory-service    |
| `payment-events`   | payment-service   | _(available for future consumers)_    |
| `inventory-events` | inventory-service | _(available for future consumers)_    |

---

## 🔑 Key Design Decisions

1. **Event-Driven Architecture** — Services are decoupled via Kafka; adding a new consumer (e.g., notification-service) requires zero changes to existing services.
2. **Shared Event Module (`common`)** — All Kafka event POJOs live in one module, ensuring type safety across services.
3. **In-Memory Storage** — Orders and inventory are stored in-memory for simplicity (no database required for demo).
4. **Retry Pattern** — Email sending uses Spring `@Retryable` with exponential backoff (3 attempts, 2s delay).
5. **KRaft Mode** — Kafka runs without ZooKeeper, using the modern KRaft consensus protocol.

---

## 🔮 Future Enhancements

- [ ] Persist orders to a database (PostgreSQL / MySQL)
- [ ] Add an order-status update flow (PENDING → PAID → SHIPPED)
- [ ] WebSocket push notifications to the UI for real-time event updates
- [ ] API Gateway (Spring Cloud Gateway) for unified routing
- [ ] Containerize all services with Docker Compose
- [ ] Add Swagger/OpenAPI documentation
- [ ] Integration tests with Testcontainers

---

## 🌐 Service Ports & Configuration Files

| Service | Port | Configuration File | Notes |
|---------|------|-------------------|-------|
| **order-service** | `8081` | [`order-service/src/main/resources/application.yml`](file:///c:/Users/MadhuKumari/Desktop/OrderManagementSystem/order-service/src/main/resources/application.yml) | Exposes order REST endpoints and publishes to Kafka |
| **payment-service** | `8082` | [`payment-service/src/main/resources/application.yml`](file:///c:/Users/MadhuKumari/Desktop/OrderManagementSystem/payment-service/src/main/resources/application.yml) | Listens on Kafka `order-events`, simulates payment |
| **inventory-service** | `8083` | [`inventory-service/src/main/resources/application.yml`](file:///c:/Users/MadhuKumari/Desktop/OrderManagementSystem/inventory-service/src/main/resources/application.yml) | Listens on Kafka `order-events`, checks stock |
| **frontend (UI)** | `3000` | [`frontend/vite.config.js`](file:///c:/Users/MadhuKumari/Desktop/OrderManagementSystem/frontend/vite.config.js) | React dev server, proxies `/api/*` to `localhost:8081` |
| **Kafka Broker** | `9092` | Docker container port mapping | `PLAINTEXT://localhost:9092` |
| **OMS Core (Monolith)** | `8081` | [`src/main/resources/application.properties`](file:///c:/Users/MadhuKumari/Desktop/OrderManagementSystem/src/main/resources/application.properties) | Root module with MySQL and JWT Auth |

---

## 📄 API Endpoints

### 1. order-service (port 8081)

| Method | Endpoint   | Description              | Request Body                     |
|--------|-----------|--------------------------|----------------------------------|
| `GET`  | `/orders` | List all created orders  | —                                |
| `POST` | `/orders` | Create a new order       | `{ "itemId": "A1", "quantity": 2 }` |

### 2. OMS User Management / Auth (`com.example.oms`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/api/auth/signup` | Register a new user | `{ "name": "...", "email": "...", "password": "...", "phoneNumber": "...", "address": "..." }` |
| `GET`  | `/api/auth/test` | Auth health test endpoint | — |

---

## 👨‍💻 Author

Madhu Kumari

## 📝 License

This project is for demonstration purposes.

# Choreography Saga Pattern — Demo Project

A demo project for learning the **Saga pattern** using the **Choreography** approach in a microservices architecture.

## What is Choreography Saga?

In distributed systems, a saga is a sequence of local transactions. In the choreography approach, there is no central coordinator — each service listens for events and reacts by publishing its own events. Services communicate through an event bus (e.g., Kafka).

## Services

| Service | Responsibility |
|---|---|
| **order-service** | Creates orders, initiates the saga |
| **stock-service** | Reserves or releases stock |
| **payment-service** | Processes or cancels payments |
| **bank-service** | Handles bank transactions |

## Tech Stack

- Java 21
- Spring Boot 4.0.3
- Spring Data JPA
- Spring Web MVC
- PostgreSQL

## Project Structure

```
choreography-saga/          ← parent module (pom)
├── order-service/
├── stock-service/
├── payment-service/
└── bank-service/
```

## Getting Started

```bash
./mvnw clean install
```

# E-Commerce Site Application

This is a Java Spring Boot project designed for e-commerce operations. This application demonstrates transactional order processing, global exception handling, and RESTful API design, utilizing technologies such as Spring Boot, SQLite, and MyBatis.

## Project Overview

This project provides a backend for e-commerce operations, emphasizing transaction integrity, data consistency, and error handling:

### Transactional Order Processing

The application employs Spring Boot's `@Transactional` annotation with isolation level `SERIALIZABLE` to ensure each order is processed consistently and isolated, safeguarding against concurrent transaction anomalies.

```java
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 6)
    public ResponseMessage createOrder(CreateOrder createOrder) throws InsufficientItemStockException, InsufficientUserBalanceException {
        // Transactional logic
    }
```

### Global Exception Handling

`@RestControllerAdvice` is used to catch and handle exceptions globally, ensuring any errors are processed uniformly, providing clear feedback to the client.

```java
    @RestControllerAdvice
    public class ApplicationExceptionHandler {
        // Exception handling methods
    }
```

## Highlights

- **Create Order Concurrency Integration Test**: Implemented comprehensive integration tests at both the controller and service levels to ensure that only one 1 is processed when the Item Stock is 1, even if multiple order requests( Used 9 concurrent requests) are received concurrently. This guarantees the integrity of order processing under high load and prevents stock inconsistencies.
  - [EcsiteControllerConcurrencyIntegrationTest](./src/test/java/io/hireroo/ecsite/controller/EcsiteControllerConcurrencyIntegrationTest.java)
  - [EcsiteServiceConcurrencyIntegrationTest](./src/test/java/io/hireroo/ecsite/service/EcsiteServiceConcurrencyIntegrationTest.java)

## Technology Stack

- **Spring Boot 3.1.4**: Framework
- **SQLite**: SQL Database
- **MyBatis**: Database ORM
- **Java 21**: Programming Language
- **Mockito**: Testing Framework

## Getting Started

To get the application running locally:

1. Ensure JDK 21 is installed.
2. Start the application:
    ```bash
    mvn spring-boot:run
    ```

## API Endpoints

Manage items, users, and orders through these endpoints:

- **Items**:
    - `GET /api/item/{id}`: Retrieves an item's details.
    - `POST /api/item`: Adds a new item.
    - `DELETE /api/item/{id}`: Removes an item.

- **Users**:
    - `GET /api/user/{id}`: Fetches user details.
    - `POST /api/user`: Creates a user account.
    - `DELETE /api/user/{id}`: Deletes a user account.

- **Orders**:
    - `POST /api/item/{item_id}/buy`: Processes an item purchase.

## Purchase Process Sequence Diagram

The sequence diagram below illustrates the steps involved in the purchase process:

### Purchase Product `POST: /api/item/{item_id}/buy`

```mermaid
sequenceDiagram
    actor client as User
    participant controller as Ecsite<br/><br/>Controller
    participant service as Ecsite<br/><br/>Service
    participant repository as Ecsite<br/><br/>Mapper
    participant db as Sqlite<br/><br/>Database
    client ->> controller: API Call: Purchase Item <br/><br/>POST: /api/item/ITEM1234/buy
    activate controller 
        alt if User Exist
            controller ->> service: Check User Existence
        else 
            controller ->> client: HTTP Status: 404<br/><br/>{"error": "User not found"}
        end
        alt if Item Exist 
            controller ->> service: Check Item Existence
        else
            controller ->> client: HTTP Status: 404<br/><br/>{"error": "Item not found"}
        end
        controller ->> service: Service call
        activate service
            note over service,db: Transaction Starts
            alt if item stock sufficient
                service ->> repository: Check Item Stock >= Quantity requested
            else
                service ->> client: HTTP Status: 400<br/><br/>{"error": "Item stock insufficient"}
            else if user balance sufficient
                service ->> repository: Check User Balance >= Purchase Amount
            else
                service ->> client: HTTP Status: 400<br/><br/>{"error": "User Balance insufficient"} 
            end
                service ->> repository: Update Users Savings Balance
                service ->> repository: Update Item Stock
                service ->> repository: Create Purchase/ Order record
            note over service,db: Transaction Ends
            service ->> controller: Response {"message": "success"}
        deactivate service
        controller ->> client: HTTP Status: 200<br/><br/>{"message": "success"}
    deactivate controller
```

## Testing

Run unit tests with:

```bash
  mvn test
```
Generate Test Coverage Report with:

```bash
  mvn clean test jacoco:report
```

## JaCoCo Test Coverage Report
image
![Report](./docs/jacoco-test-coverage-report.png)

## ToDo's and Future Improvements

- **Swagger API Schema**: To enhance API documentation and user interaction.
- **Linter in Pre-commit**: To maintain code quality and ensure style consistency.
- **Refactor Validation Logic**: For improved data integrity at the service level.
- **Circuit Breaker Mechanism**: Implement using `Resilience4j` for better fault tolerance.
- **Soft Delete for Items**: Modify deletion logic for items to allow data recovery and maintain History records.


# Purchase Products on EC Sites (Java Spring Boot)

## Overview

A company has decided to build a book e-commerce site as a new project using Spring Boot. The application already has endpoints for registering and retrieving products, registering and retrieving users, etc., but no endpoint for purchasing products has been implemented. Please implement an endpoint for purchasing products at `/api/item/{item_id}/buy` according to the following specification.

## Specification

### Purchase of Products

This endpoint is for purchasing products and includes the product ID in the Path Parameter, the user ID in the request, and the number of items to be purchased. When the purchase is completed, return a 200 status code indicating success and an optional return value.

**Example Request:**

- **Path:** `/api/item/{item_id}/buy`
- **Method:** POST
- **Parameter:**
    ```json
    {
      "user_id": "fa6b7411",
      "quantity": 1
    }
    ```
- **Response:**
    ```json
    {
      "message": "success"
    }
    ```

After the purchase is completed, please update stock data for the item, and the user's savings, respectively.

### Product and User Information

Product and user information is stored in the database with the following definitions, respectively:

#### Item Table

| Column Name  | Type   | Summary                       |
|--------------|--------|-------------------------------|
| id           | STRING | ID to uniquely identify an item |
| name         | STRING | Item name                    |
| description  | STRING | Item description             |
| price        | INT    | The amount of money required to purchase the product |
| stock        | INT    | Number of items in stock     |

#### User Table

| Column Name | Type   | Description                  |
|-------------|--------|------------------------------|
| id          | STRING | ID to uniquely identify a user |
| name        | STRING | User name                    |
| savings     | INT    | Amount of money a user can spend |

In rare cases, the number of items to be purchased may be greater than the number of items registered, or the purchase amount may be higher than the savings balance registered in the user's account. In that case, please return a 400 status code indicating failure and an optional return value.

## Environment

- **OS:** Debian GNU/Linux 11 (bullseye)
- **Java version:** 21
- **Spring Boot version:** 3.1.4
- **Database:** SQLite

## Notes

The application already implements endpoints for registering, retrieving, and deleting products and registering, retrieving, and deleting users. A created application will run on a production Docker Container with an expected maximum RPS (Requests Per Second) of 10.

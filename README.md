# MahashriMart

MahashriMart is a Java 17 multi-seller marketplace built with Servlets on Tomcat 9, JSP/JSTL, JDBC, H2, and HikariCP.

**Live demo:** https://mahashri-mart.onrender.com

## Features

- **Authentication** — registration and login for buyers, sellers, and an admin seed account, with bcrypt password hashing and session-based auth
- **Product browsing** — search by keyword and filter by category (Toys, Home, Wellness, Electronics, Jewels, Groceries, Stationery, School / Office Supplies, Art Supplies)
- **Cart & checkout** — add/update/remove items, running total, mock payment confirmation
- **Order history** — buyers can view their past orders; sellers can view incoming orders containing their own products
- **Seller dashboard** — sellers can create, edit, and delete their own product listings
- **Admin panel** — view all users, view all orders, and moderate/remove any product listing
- **Reviews & ratings** — buyers can leave a 1–5 star rating with an optional comment on any product

## Tech stack

- Java 17, Maven, Apache Tomcat 9.0.x
- Java Servlets (`javax.servlet.*`), JSP + JSTL
- JDBC with H2 (embedded, in-memory)
- HikariCP connection pooling
- jBCrypt for password hashing
- JUnit 5 + Mockito for unit tests
- Docker (for deployment)

## Architecture

Layered MVC over Servlets (Front Controller pattern):

```
Browser (HTML/CSS/JSP)
    |
Filter layer -> AuthFilter (session check), EncodingFilter
    |
Controller layer -> per-resource Servlets (ProductServlet, CheckoutServlet, OrderHistoryServlet,
                     SellerOrdersServlet, AdminServlet, ReviewServlet, ...)
    |
Service layer -> business logic, validation (OrderService, ProductService, UserService, ...)
    |
DAO layer -> ProductDao, OrderDao, CartDao, UserDao, ReviewDao — all SQL, PreparedStatement only
    |
Connection Pool -> HikariCP (via AppContextListener at startup)
    |
H2 Database
```

## Diagrams

- **ER Diagram** — see `docs/D1-ER-diagram.mermaid`
- **Use Case Diagram** — see `docs/D2-UseCase-diagram.mermaid`
- **Sequence Diagram (place-order flow)** — see `docs/D3-Sequence-diagram.mermaid`

## Screenshots

_(Add screenshots of the marketplace, cart, checkout, seller dashboard, and admin panel here before final submission.)_

## Run locally with Docker (recommended)

Requires Docker Desktop installed.

```bash
docker build -t mahashrimart .
docker run -p 8080:8080 mahashrimart
```

The app will be available at `http://localhost:8080/`.

## Run locally without Docker

Requires JDK 17, Maven, and Apache Tomcat 9.0.x installed separately.

```bash
mvn clean package
```

Copy the generated `target/mahashrimart.war` into your Tomcat `webapps/` folder, then start Tomcat. The app will be available at `http://localhost:8080/mahashrimart`.

## Seed accounts

| Role | Email | Notes |
|---|---|---|
| Admin | admin@mahashri.com | Full store overview: users, orders, product moderation |
| Buyer | buyer2@mahashri.com | Sample buyer with an order history |
| Seller | seller7@mahashri.com | Campus Essentials — School / Office Supplies |

## Deployment

This project is deployed on [Render](https://render.com) using the included `Dockerfile`. Render auto-builds and redeploys on every push to the `main` branch.

## Known Limitations

- **Data persistence:** The app currently uses H2 in in-memory mode (`jdbc:h2:mem`) rather than a persistent database. This is a deliberate trade-off for this deployment checkpoint: the free hosting tier used does not provide persistent disk storage, and available free-tier alternatives with persistent storage (e.g., Fly.io, Oracle Cloud) require a payment card on file for identity verification even when no charge applies. As a student project, this was avoided in favor of documenting the limitation. As a result, data resets to the seeded demo dataset whenever the app restarts or the free-tier instance spins down after inactivity. A production-grade fix would involve a managed external database with persistent storage, or upgrading to a hosting plan with persistent disk support.
# MahashriMart

MahashriMart is a Java 17 multi-seller marketplace built with Servlets on Tomcat 9, JSP/JSTL, JDBC, H2, and HikariCP.

**Live demo:** https://mahashri-mart.onrender.com

## Features

- **Authentication** — registration and login for buyers and sellers, with bcrypt password hashing and session-based auth
- **Product browsing** — search by keyword and filter by category
- **Cart & checkout** — add/update/remove items, mock payment confirmation, order history
- **Seller dashboard** — sellers can create, edit, and delete their own product listings
- **Admin panel** — view all users and orders, and remove any product listing
- **Reviews & ratings** — buyers can leave a 1–5 star rating with an optional comment on any product

## Tech stack

- Java 17, Maven, Apache Tomcat 9.0.x
- Java Servlets (`javax.servlet.*`), JSP + JSTL
- JDBC with H2 (embedded, in-memory)
- HikariCP connection pooling
- jBCrypt for password hashing
- JUnit 5 + Mockito for unit tests
- Docker (for deployment)

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

## Deployment

This project is deployed on [Render](https://render.com) using the included `Dockerfile`. Render auto-builds and redeploys on every push to the `main` branch.

## Known Limitations

- **Data persistence:** The app currently uses H2 in in-memory mode (`jdbc:h2:mem`) rather than a persistent database. This was a deliberate trade-off for this deployment checkpoint — the free hosting tier used does not provide persistent disk storage, so even a file-based H2 setup would not survive container restarts. As a result, data resets to the seeded demo dataset whenever the app restarts, or when the free-tier instance spins down after inactivity and wakes back up. A production-grade fix would involve a managed external database (e.g., a free-tier PostgreSQL or MySQL instance hosted in the cloud) or upgrading to a hosting plan with persistent disk support.
- **Seller order visibility:** Sellers can currently manage their own listings, but there is no dedicated view yet for sellers to see incoming orders containing their products. This is planned as a follow-up enhancement.
# MahashriMart

MahashriMart is a Java 17 multi-seller marketplace built with Servlets on Tomcat 9, JSP/JSTL, JDBC, H2, and HikariCP.

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

## Run locally

Requires JDK 17, Maven, and Apache Tomcat 9.0.x installed separately.

```bash
mvn clean package
```

Copy the generated `target/mahashrimart.war` into your Tomcat `webapps/` folder, then start Tomcat. The app will be available at:
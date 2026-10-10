# Contributing to MahashriMart

This file shows the exact steps from `git clone` to a running local site, and the rules for making changes.

## 1. What you need

- JDK 17
- Maven 3.8 or newer
- Git
- Docker is NOT needed (it is only used by the deployed site on Render)

Check your tools:

```
java -version
mvn -version
git --version
```

## 2. Get the code and run it

```
git clone https://github.com/MahashriTA-sudo/Mahashri-mart.git
cd Mahashri-mart
mvn clean verify
mvn compile exec:java "-Dexec.mainClass=com.mahashri.mahashrimart.EmbeddedServer"
```

Then open http://localhost:8080/ in your browser. Stop the server with `Ctrl+C`.

Notes:
- `mvn clean verify` builds the project and runs all automated tests. It must say `BUILD SUCCESS`.
- The local database is H2 in memory, so the data resets every time the server restarts. Demo data is loaded automatically.
- After you change any code, JSP or CSS, stop the server, run `mvn clean verify` again, then start the server again. The server runs from the copy in `target/`.
- `mvn clean verify` does not compile JSP files. A JSP error only shows when you open the page.
- If `mvn clean` fails with "Failed to delete", an old server is still running. Stop it, then run the build again.
- Optional settings (for example the chatbot provider) are listed in `.env.example`.

## 3. Demo accounts

The password for all accounts is `password`.

| Role | Email |
|------|-------|
| Buyer | buyer1@mahashri.com (up to buyer5) |
| Seller | seller1@mahashri.com (up to seller8) |
| Admin | admin@mahashri.com |

## 4. Project layout

- `src/main/java/com/mahashri/mahashrimart/` : controller, service, dao, model, filter, listener, util, exception
- `src/main/webapp/WEB-INF/views/` : JSP pages
- `src/main/webapp/static/css/app.css` : the stylesheet
- `src/main/resources/db/migrations/` : numbered database migration files
- `src/test/java/` : unit and DAO tests
- `docs/` : diagrams and screenshots

## 5. How to make a change

1. Open a GitHub Issue first (feature request or bug report) with a short impact note: does it need a database migration, does it change an API response, is it additive?
2. Create a branch from `main`: `feature/<name>` or `fix/<name>`.
3. Write the code and the tests. Use PreparedStatement for all SQL. Keep servlets thin and put business rules in the service layer.
4. Run `mvn clean verify`. It must be `BUILD SUCCESS` before you commit.
5. Commit with conventional commits: `feat:`, `fix:`, `test:` or `docs:`.
6. Push and open a pull request. In the description write what changed, why, and how it was tested. Review your own pull request.
7. Merge with a merge commit only when CI is green.
8. Test the change on the deployed site: https://mahashri-mart.onrender.com
9. For a release: update `CHANGELOG.md`, add a row to `TESTING.md`, and push a semver tag (for example `v1.5.0`).

## 6. Database changes

Every schema change is a new numbered file in `src/main/resources/db/migrations/` (for example `V4__add_wishlist.sql`). Do not edit an old migration file.

## 7. Definition of Done

- Code compiles and tests pass
- Code is self-reviewed
- A migration file is included if the schema changed
- Verified on the deployed URL, not only on localhost
- README and docs updated if behavior changed
- Merged to `main` only when CI is green
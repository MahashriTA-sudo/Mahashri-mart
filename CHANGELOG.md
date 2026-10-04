# Changelog

All notable changes to MahashriMart are documented in this file.

## [v1.2.0] - 2026-10-04
Order status management release.
- **Buyer cancellation** — buyers can cancel PENDING or CONFIRMED orders via `POST /orders/cancel`; stock is restored atomically in a transaction
- **Seller status advancement** — sellers can mark orders SHIPPED (from CONFIRMED) or DELIVERED (from SHIPPED) via `POST /seller/orders/update`; invalid transitions are rejected with a clear error message
- **Admin full control** — admins can force-set any order to any status via `POST /admin/orders/update`; force-cancellation also restores stock
- **Admin status filter** — `GET /admin/orders?status=SHIPPED` (or any status) filters the orders table; "All" resets to unfiltered view
- **Coloured status badges** — PENDING (amber), CONFIRMED (blue), SHIPPED (indigo), DELIVERED (green), CANCELLED (red) across all order views
- **Flash messages** — success and error banners on all order pages using redirect-after-POST pattern
- **Detailed order items** — buyer order history now shows qty × unit price per item, not just product name
- `updated_at` column added to `orders` table (tracked on every status change)
- `incrementStock` added to `ProductDao` / `JdbcProductDao` for stock restoration


## [v1.1.0] - 2026-09-20
AI chatbot release.
- Shopping assistant chat widget on every page, served by POST /api/v1/chat
- Pluggable provider: mock (rule-based FAQ) and Gemini (API key kept server-side)
- Guardrails: 10 messages per minute rate limit, 200-character input cap, 20-second timeout, per-session cache, and a static fallback reply on any failure

## [v1.0.0] - 2026-09-13
Full Build + Deploy checkpoint release.
- All mandatory features (F1-F8) implemented and verified live: authentication, product browsing/search, cart, checkout, buyer + seller order history, admin panel, reviews & ratings
- Security checklist completed: parameterized queries, bcrypt hashing, AuthFilter route protection (privilege-escalation gap fixed), output escaping, custom error pages, credentials via environment variables only
- CI pipeline added (GitHub Actions - build and test on every push)
- `GET /api/v1/health` endpoint added for uptime monitoring
- ER, Use Case, and Sequence diagrams added
- README expanded with architecture, diagrams, screenshots, and seed account documentation
- Deployed live on Render via Docker

## [v0.2.0] - 2026-09-08
- Seller order visibility (F6 completion) - sellers can view incoming orders for their products
- Product catalog expanded across new categories: Toys, Jewels, Stationery, School / Office Supplies, Art Supplies
- Fixed order total display bug (`orders.jsp`)
- Fixed multiple broken product image URLs

## [v0.1.0] - 2026-09-05
MVP release.
- Initial repository setup, project skeleton
- Authentication (register/login/session)
- Product search and category filter
- Seller product CRUD
- Admin panel (view users, orders, manage listings)
- Reviews and star ratings
- Unit tests added (ProductService, UserService)
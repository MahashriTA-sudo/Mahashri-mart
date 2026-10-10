# Changelog

All notable changes to MahashriMart are documented in this file.

## [v1.5.0] - 2026-10-10
Home page quick actions and login page polish.
- **Home page quick actions**: each product card now shows stock ("X available" or "Out of stock"), and logged-in buyers get **Add to cart** and **Buy** buttons without opening the product page
- **Wishlist heart on cards**: a small heart beside the product name adds or removes the product from the wishlist (filled heart = saved). The page returns to the products area
- **Logged-out view**: product cards show a "Log in to purchase" button and no heart
- **Buy now** button added to the product page (opens checkout)
- **Cart notice**: a green "Added to your cart" message appears on the home page after Add to cart
- **Reviews section redesigned**: "Customer reviews" heading with average score, review cards in a 2-column grid, grey stars for empty stars, and the "Write a review" form inside a card
- **Login page**: show/hide password button and a demo accounts box (buyer, seller, admin)
- `CartAddServlet` accepts an optional `next` field (`checkout` or `home`); `WishlistServlet` accepts `from=home`; `HomeServlet` sends `wishedIds` to the page
- No database changes. 102 tests in total, all passing

## [v1.4.1] - 2026-10-08
Bug fix release.
- **Fix**: an admin could cancel a DELIVERED order. This is now blocked with the message "A DELIVERED order cannot be cancelled."
- Unit test `adminCannotCancelADeliveredOrder` added (102 tests in total)
- Live-site test T17 added to `TESTING.md`

## [v1.4.0] - 2026-10-06
Wishlist release.
- **Wishlist / save-for-later (O1)**: logged-in buyers can add a product to their wishlist from the product page, view the Wishlist page from the header, and remove items
- Wishlist is private: logged-out visitors are redirected to the Login page
- Same product cannot be saved twice by the same user
- `wishlist_items` table added with migration `V4__add_wishlist.sql` (also added to `schema.sql`)
- `WishlistDao`, `WishlistService`, `WishlistServlet` and `WishlistItem` added, with 12 new tests (101 tests in total)

## [v1.3.0] - 2026-10-04
Seller sales dashboard and order page polish.
- **Seller sales dashboard (O3)**: the seller's incoming orders page shows total, active, delivered and cancelled order counts, and total revenue (cancelled orders are not counted)
- **Order status timeline**: the buyer's order history shows Confirmed, Shipped and Delivered progress, or a red notice for cancelled orders
- **Readable order dates**: dates now show like `4 Oct 2026, 8:17 PM` in Indian time, on both buyer and seller pages
- `SellerSalesSummary` model class added, with 6 new unit tests (89 tests in total)

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
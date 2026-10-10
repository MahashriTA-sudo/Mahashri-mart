# MahashriMart - Manual Testing Guide

Live site: https://mahashri-mart.onrender.com
Test accounts (password: `password`): buyer1@mahashri.com, seller1@mahashri.com, admin@mahashri.com

## 1. Automated tests
1. Run `mvn clean verify`.
2. Expected result: `Tests run: 102, Failures: 0, Errors: 0` and `BUILD SUCCESS`.
3. The same command also runs Checkstyle and SpotBugs. Expected: 0 Checkstyle violations and "BugInstance size is 0". The build fails if a new problem appears.

## 2. Buyer flow
1. Log in as `buyer1@mahashri.com`.
2. Open the Marketplace and add a product to the cart.
3. Try to add more than the available stock. Expected: an error message.
4. Open the Cart, change the quantity, then remove one item.
5. Click Checkout. Expected: "Order #N confirmed" and a CONFIRMED badge on the Orders page.
6. Click **Cancel order**. Expected: status becomes CANCELLED and the product stock goes back up.
7. Write a review (rating 1 to 5). Expected: ratings outside 1 to 5 are rejected.

## 3. Seller flow
1. Log in as `seller1@mahashri.com`.
2. Add or edit a product.
3. Open Seller Orders and find a CONFIRMED order.
4. Click the button to move it to SHIPPED, then to DELIVERED.
5. Expected: a seller cannot skip a step, and cannot update another seller's orders.

## 4. Admin flow
1. Log in as `admin@mahashri.com`.
2. Open the admin dashboard and the orders list.
3. Filter orders by status (for example SHIPPED).
4. Change an order's status. Expected: cancelling an order restores the stock.
5. Try to cancel a DELIVERED order. Expected: refused with "A DELIVERED order cannot be cancelled."

## 5. Chatbot
1. Click "Chat with us".
2. Ask about a product. Expected: a reply based on the catalog.

## 6. Security checks
1. Open /orders while logged out. Expected: redirect to login.
2. Log in as a buyer and open /admin/orders. Expected: access denied.

## 7. Wishlist
1. Log in as a buyer, open a product and click **Add to wishlist**.
2. Open the Wishlist page. Expected: the product is listed.
3. Click **Remove**. Expected: the product disappears.
4. Log out and open /wishlist. Expected: redirect to login.

## 8. Home page quick actions and login page
1. Open the Login page. Expected: a **Show** button in the password box and a **Demo accounts** box.
2. Log in as `buyer1@mahashri.com` and open the home page. Expected: each product card shows stock text, **Add to cart**, **Buy**, and a small heart beside the product name.
3. Click the heart. Expected: it changes between empty and filled, and the page stays at the products area.
4. Click **Buy** on a card. Expected: the checkout page opens.
5. Log out and open the home page. Expected: cards show **Log in to purchase** and no heart.

## 9. Load test
Tool: a small Java program, `loadtest/LoadTest.java` (built-in `java.net.http.HttpClient`, no extra install).
1. Start the site locally (see README), then open a second PowerShell window.
2. Run `java loadtest\LoadTest.java http://localhost:8080`.
3. The program runs 10 concurrent users for 60 seconds. Each user sends requests one after another to `/`, `/api/v1/health` and `/login`.
4. It prints the total requests, errors, requests per second, average time, 95th percentile and slowest request.

Result (local run, 10 Oct 2026, version v1.5.1):

| Measure | Value |
|---------|-------|
| Concurrent users | 10 |
| Duration | 60 seconds |
| Total requests | 56,462 |
| Errors | 0 |
| Requests per second | 940.8 |
| Average response time | 10 ms |
| 95th percentile | 43 ms |
| Slowest request | 736 ms |

Note: this was run on my laptop against the local site (in-memory database). The free Render server is slower, so it was not used for the load test.

## Test Results Table - Local run

Environment: local run at http://localhost:8080 (in-memory database), version v1.3.0 (T19 run on v1.5.1).

| ID | What I test | Expected result | Actual result | Pass/Fail |
|----|-------------|-----------------|---------------|-----------|
| T1 | Admin changes an order status to SHIPPED | Status changes, flash message shows | Order #1 changed to SHIPPED, message "Order #1 status updated." shown | Pass |
| T2 | Admin filters orders (`/admin/orders?status=SHIPPED`) | Only SHIPPED orders are listed | SHIPPED filter showed Order #1 while SHIPPED, then an empty list after it became DELIVERED. DELIVERED filter showed Order #1 | Pass |
| T3 | Admin cancels an order | Status becomes CANCELLED and stock goes back up | Order #2 cancelled by admin, message shown, Color Pencils Set stock went 349 to 350 | Pass |
| T4 | Seller moves order CONFIRMED to SHIPPED | Status becomes SHIPPED | Seller8 clicked "Mark as Shipped" on Order #3, status became SHIPPED | Pass |
| T5 | Seller moves order SHIPPED to DELIVERED | Status becomes DELIVERED, timeline all green | Seller8 marked Order #3 as Delivered, badge showed DELIVERED | Pass |
| T6 | Buyer cancels a CONFIRMED order | Status becomes CANCELLED, stock goes back up | Buyer1 cancelled Order #5, message "cancelled and stock restored" shown, Acrylic Paints Set stock went 249 to 250 | Pass |
| T7 | Buyer tries to cancel a DELIVERED order | Cancel button is not shown | Order #1 (DELIVERED) showed all three green dots and no Cancel button | Pass |
| T8 | Seller sales dashboard after the flow | Boxes show correct counts and revenue | Total 4, Active 0, Delivered 2, Cancelled 2, Revenue Rs 348.00 (cancelled orders not counted) | Pass |
| T9 | Register, browse, order, review (end to end) | Every step works without errors | Registered a new buyer, logged in, browsed Pencil Pouch (805 available), ordered it (Order #6), submitted a review (3 stars, "good") | Pass |
| T10 | Open /orders while logged out | Redirect to login | Logged out, opened /orders, redirected to the Login page | Pass |
| T19 | Load test: 10 concurrent users for 60 seconds | No errors, fast responses | 56,462 requests, 0 errors, 940.8 requests per second, average 10 ms, 95th percentile 43 ms | Pass |

## Test Results Table - Live site

Environment: deployed site https://mahashri-mart.onrender.com, version v1.4.0 (T17 tested after the PR #5 bug fix, T18 tested after the v1.5.0 release). (Render restarts reset the in-memory data, so order numbers start again from #1.)

| ID | What I test | Expected result | Actual result | Pass/Fail |
|----|-------------|-----------------|---------------|-----------|
| T11 | Admin changes an order status to SHIPPED | Status changes, flash message shows | Admin changed Order #1 to SHIPPED, badge and message updated | Pass |
| T12 | Buyer cancels a CONFIRMED order | Status becomes CANCELLED, stock goes back up | Buyer1 ordered 2 Paint Brush Set (Order #1), stock went 280 to 278. After cancel, message shown and stock went back to 280 | Pass |
| T13 | Admin filters orders by status | Only matching orders are listed | SHIPPED filter showed Order #1, then an empty list after it became DELIVERED. DELIVERED filter showed Order #1 | Pass |
| T14 | Wishlist: add and remove a product | Product appears on the Wishlist page, then disappears after Remove | Wax Crayons Pack appeared on the Wishlist page, and disappeared after Remove | Pass |
| T15 | Open /wishlist while logged out | Redirect to login | Logged out, opened /wishlist, redirected to the Login page | Pass |
| T16 | Register, browse, order, review (end to end) | Every step works without errors | Registered a new buyer, logged in, browsed Magazine File Holder (500 available), ordered it (Order #2), submitted a review (4 stars, "good") | Pass |
| T17 | Admin tries to cancel a DELIVERED order (bug fix, PR #5) | Cancel is refused and the order stays DELIVERED | Admin set Order #1 (DELIVERED) to CANCELLED, message "A DELIVERED order cannot be cancelled." shown and status stayed DELIVERED | Pass |
| T18 | Home page quick actions and login page (v1.5.0) | Cards show stock, Add to cart, Buy and a heart beside the name. Buy opens checkout. Login page has Show button and demo box. Logged-out cards show "Log in to purchase" and no heart | Health check showed UP/UP. Login page had the Show button and Demo accounts box. As buyer1, cards showed stock text, Add to cart, Buy and a heart beside the product name (filled for saved items). Buy opened the checkout page. After Log out, cards showed "Log in to purchase" and no heart | Pass |

## Result
Local run: 11 of 11 passed (T1 to T10 on 6 Oct 2026, T19 load test on 10 Oct 2026). Live site: 8 of 8 passed (T11 to T16 on 7 Oct 2026, T17 on 8 Oct 2026, T18 on 10 Oct 2026).
Tested by: Mahashri T A   All checks passed: Yes
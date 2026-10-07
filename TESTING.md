# MahashriMart - Manual Testing Guide

Live site: https://mahashri-mart.onrender.com
Test accounts (password: `password`): buyer1@mahashri.com, seller1@mahashri.com, admin@mahashri.com

## 1. Automated tests
1. Run `mvn clean verify`.
2. Expected result: `Tests run: 101, Failures: 0, Errors: 0` and `BUILD SUCCESS`.

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

## Test Results Table - Local run

Environment: local run at http://localhost:8080 (in-memory database), version v1.3.0.

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

## Test Results Table - Live site

Environment: deployed site https://mahashri-mart.onrender.com, version v1.4.0. (Render restarts reset the in-memory data, so order numbers start again from #1.)

| ID | What I test | Expected result | Actual result | Pass/Fail |
|----|-------------|-----------------|---------------|-----------|
| T11 | Admin changes an order status to SHIPPED | Status changes, flash message shows | Admin changed Order #1 to SHIPPED, badge and message updated | Pass |
| T12 | Buyer cancels a CONFIRMED order | Status becomes CANCELLED, stock goes back up | Buyer1 ordered 2 Paint Brush Set (Order #1), stock went 280 to 278. After cancel, message shown and stock went back to 280 | Pass |
| T13 | Admin filters orders by status | Only matching orders are listed | SHIPPED filter showed Order #1, then an empty list after it became DELIVERED. DELIVERED filter showed Order #1 | Pass |
| T14 | Wishlist: add and remove a product | Product appears on the Wishlist page, then disappears after Remove | Wax Crayons Pack appeared on the Wishlist page, and disappeared after Remove | Pass |
| T15 | Open /wishlist while logged out | Redirect to login | Logged out, opened /wishlist, redirected to the Login page | Pass |
| T16 | Register, browse, order, review (end to end) | Every step works without errors | Registered a new buyer, logged in, browsed Magazine File Holder (500 available), ordered it (Order #2), submitted a review (4 stars, "good") | Pass |

## Result
Local run: 10 of 10 passed (6 Oct 2026). Live site: 6 of 6 passed (7 Oct 2026).
Tested by: Mahashri T A   All checks passed: Yes
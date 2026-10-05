# MahashriMart - Manual Testing Guide

Live site: https://mahashri-mart.onrender.com
Test accounts (password: `password`): buyer1@mahashri.com, seller1@mahashri.com, admin@mahashri.com

## 1. Automated tests
1. Run `mvn clean verify`.
2. Expected result: `Tests run: 83, Failures: 0, Errors: 0` and `BUILD SUCCESS`.

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

## Result
Date tested: ____   Tested by: ____   All checks passed: Yes / No
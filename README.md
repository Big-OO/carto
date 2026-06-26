# Carto

A native Android Shopify e-commerce application powered by Shopify.

Carto allows users to browse products as guests, authenticate, manage wishlist and cart, search and filter products, apply discounts, manage addresses, and complete checkout using Cash on Delivery or online payment.

## 📌 Project Overview

This project is a mobile Shopify-based e-commerce app built for the JETS Mobile Lab project.

The application integrates with Shopify Admin REST API to fetch and manage store data such as products, vendors, variants, inventory, discounts, customers, and orders.

## ✨ Main Features

### Authentication
- Guest browsing
- Email/password registration and login
- Social authentication support
- Email verification
- Protected wishlist and cart access

### Product Catalog
- Fetch products dynamically from Shopify
- Browse products by vendors/brands
- Product details screen
- Product images, sizes, price, rating, description, and reviews

### Search, Filtering, and Sorting
- Global product search
- Main categories and sub-categories
- Filter by category, sub-category, and brand
- Sort by price, best sellers, and sub-category grouping

### Wishlist
- Add products to wishlist
- Remove products from wishlist
- Wishlist available only for authenticated users

### Shopping Cart
- Add products to cart
- Remove products from cart
- Update item quantity
- Validate stock before increasing quantity
- Dynamically calculate total price

### Account & Settings
- User profile summary
- Recent orders preview
- Wishlist preview
- Address management
- Currency exchange rate support
- Country list integration

### Checkout & Payment
- Apply discount coupons
- Cash on Delivery support
- COD upper limit validation
- Online payment support
- Order placement
- Confirmation email after successful order

### Safety & UX
- Confirmation dialogs before destructive actions
- Consistent design system
- Custom launcher icon
- Clean mobile-first UI

## 🛠 Tech Stack

- Kotlin
- Native Android
- XML or Jetpack Compose
- MVVM Architecture
- Retrofit / OkHttp
- Coroutines / Flow
- Firebase Authentication
- Shopify Admin REST API
- Optional: Shopify GraphQL Admin API
- Optional: Google Places API or HERE Maps API

## 🔐 API Integration

Shopify REST endpoints follow this structure:

```text
https://{hostname}/admin/api/{version}/{resource}.json
````

Credentials must never be hardcoded.

Use secure config files or local properties for:

```properties
SHOPIFY_API_KEY=
SHOPIFY_PASSWORD=
SHOPIFY_HOSTNAME=
SHOPIFY_API_VERSION=
```

## 📁 Suggested Project Structure

```text
app/
 └── src/
     └── main/
         ├── java/com/example/shopifyapp/
         │   ├── data/
         │   │   ├── remote/
         │   │   ├── model/
         │   │   └── repository/
         │   ├── domain/
         │   │   ├── model/
         │   │   └── usecase/
         │   ├── presentation/
         │   │   ├── auth/
         │   │   ├── home/
         │   │   ├── product/
         │   │   ├── cart/
         │   │   ├── wishlist/
         │   │   ├── checkout/
         │   │   └── profile/
         │   └── utils/
         └── res/
```

## 🧪 Project Requirements

* All team members must contribute through GitHub commits.
* Tasks should be tracked using Jira or Trello.
* Mentors should be added to the GitHub repository and project board.
* Destructive actions must require confirmation before execution.

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

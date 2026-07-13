# 🛍️ Carto

**Carto** is a modern native Android e-commerce application built for fashion shopping, product discovery, wishlist management, cart flow, AI assistance, map-based address picking, and a polished light/dark user experience.

The goal of this project is not just to show shopping screens. It demonstrates a real modular Android setup with clean feature separation, Shopify integration, Firebase integration, local persistence, reactive state, AI-powered shopping support, and scalable architecture.

---

## 📸 Preview

<p align="center">
  <img src="promo/11-carto-intro-poster.png" width="850" alt="Carto app intro poster" />
</p>

<p align="center">
  <img src="mockups/02-home-light-mockup.png" width="300" alt="Carto home light mode mockup" />
  <img src="mockups/07-home-dark-mockup.png" width="300" alt="Carto home dark mode mockup" />
</p>

<table>
  <tr>
    <td align="center">
      <strong>Onboarding</strong><br/>
      <img src="mockups/01-onboarding-mockup.png" width="300" alt="Onboarding screen mockup"/>
    </td>
    <td align="center">
      <strong>Saved / Wishlist</strong><br/>
      <img src="mockups/03-saved-mockup.png" width="300" alt="Saved wishlist screen mockup"/>
    </td>
    <td align="center">
      <strong>Product Details</strong><br/>
      <img src="mockups/04-product-details-mockup.png" width="300" alt="Product details screen mockup"/>
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Carto AI</strong><br/>
      <img src="mockups/05-carto-ai-mockup.png" width="300" alt="Carto AI screen mockup"/>
    </td>
    <td align="center">
      <strong>Brand Page</strong><br/>
      <img src="mockups/06-brand-page-mockup.png" width="300" alt="Brand page screen mockup"/>
    </td>
    <td align="center">
      <strong>Cart</strong><br/>
      <img src="mockups/08-cart-mockup.png" width="300" alt="Cart screen mockup"/>
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Map / Address</strong><br/>
      <img src="mockups/09-map-address-mockup.png" width="300" alt="Map address screen mockup"/>
    </td>
    <td align="center">
      <strong>Profile</strong><br/>
      <img src="mockups/10-profile-mockup.png" width="300" alt="Profile screen mockup"/>
    </td>
    <td align="center">
      <strong>Home - Dark Mode</strong><br/>
      <img src="mockups/07-home-dark-mockup.png" width="300" alt="Home dark mode mockup"/>
    </td>
  </tr>
</table>

---

## ✨ Features

| Area | Implementation |
|---|---|
| 🏠 Home | Displays banners, categories, brands, discounts, and product sections in light/dark mode. |
| 🛍️ Product browsing | Supports product cards, sale badges, images, prices, categories, brands, and variants. |
| 📦 Product details | Shows product image, description, rating, colors, sizes, price, and add-to-cart flow. |
| ❤️ Wishlist | Saves favorite products and keeps the saved-products experience clean and accessible. |
| 🛒 Cart | Manages selected products, quantities, totals, and checkout preparation. |
| 🤖 Carto AI | Provides a smart shopping assistant experience inside the app. |
| 🗺️ Address map | Lets users select delivery location through a map-based flow. |
| 👤 Profile | Displays customer profile, order stats, settings, and logout flow. |
| 🌓 Light & dark mode | Provides a polished theme experience across the app. |
| 🧱 Modular architecture | Features are isolated into dedicated modules for better maintainability. |

---

## 🧰 Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Main programming language. |
| Jetpack Compose | Declarative native Android UI. |
| Clean Architecture | Separates UI, domain logic, and data implementation. |
| Feature-based modularization | Keeps features isolated and scalable. |
| Hilt | Dependency injection. |
| Retrofit | REST API communication. |
| GraphQL | Shopify Storefront/product discovery integration where needed. |
| Firebase | Authentication and user-related backend flows. |
| Room | Local persistence for cached/local app data. |
| DataStore | Session, settings, and lightweight preference storage. |
| Coroutines + Flow | Async operations and reactive streams. |
| Coil | Image loading. |
| Map integration | Address and location selection. |
| Material Design / App Theme | Shared UI style, light mode, and dark mode. |

---

## 🧠 Architecture

Carto follows a **feature-based Clean Architecture** approach with a dedicated **core module**.

The project is not designed as one huge app module where UI, network calls, storage, and business rules are mixed together. Instead, it separates shared logic, feature logic, and presentation logic clearly.

```text
Carto
├── app
│   ├── MainActivity
│   ├── App entry point
│   ├── Navigation setup
│   └── Dependency graph initialization
│
├── core
│   ├── common
│   │   ├── shared result wrappers
│   │   ├── constants
│   │   ├── extensions
│   │   └── reusable helpers
│   │
│   ├── data
│   │   ├── shared data models
│   │   ├── local storage helpers
│   │   ├── DataStore/session handling
│   │   └── shared data utilities
│   │
│   ├── network
│   │   ├── Retrofit setup
│   │   ├── API clients
│   │   ├── interceptors
│   │   └── network configuration
│   │
│   ├── database
│   │   ├── Room database setup
│   │   ├── shared entities
│   │   └── shared DAOs
│   │
│   ├── domain
│   │   ├── shared domain models
│   │   ├── base repository contracts
│   │   └── common use-case helpers
│   │
│   └── ui
│       ├── shared components
│       ├── theme helpers
│       ├── reusable dialogs
│       └── common UI utilities
│
├── feature
│   ├── on_boarding
│   ├── login
│   ├── register
│   ├── forgetpassword
│   ├── home
│   ├── brand
│   ├── search
│   ├── product_details
│   ├── favorite
│   ├── shopping_cart
│   ├── ai_integration
│   ├── map
│   ├── addresses
│   ├── profile
│   ├── settings
│   ├── orderhistory
│   ├── orderdetails
│   ├── payment
│   ├── currency
│   ├── home_widget
│   └── ai_widget
│
└── navigation
    └── app routes and screen transitions
```

### Core module responsibility

The `core` module contains shared code used by multiple features.

It keeps the feature modules clean and prevents repeating the same logic everywhere.

The core module is responsible for:

- shared models
- common result/error handling
- networking setup
- Retrofit/API client configuration
- Room database setup
- DataStore/session handling
- reusable UI components
- shared utilities and extensions
- base domain contracts
- dependency injection helpers used across features

The important point is that feature modules can depend on `core`, but `core` should not depend on feature modules.

```text
feature modules → core
app → feature modules + core
core → no feature dependency
```

This keeps the project scalable and prevents circular dependencies.

### Typical feature structure

Most features follow this internal structure:

```text
feature/<feature_name>
├── data
│   ├── remote data sources
│   ├── local data sources
│   ├── DTOs
│   ├── mappers
│   └── repository implementations
│
├── domain
│   ├── models
│   ├── repository contracts
│   └── use cases
│
└── presentation
    ├── screens
    ├── ViewModels
    ├── UI state
    └── events/actions
```

### Layer responsibilities

| Layer | Responsibility |
|---|---|
| `presentation` | Jetpack Compose screens, ViewModels, UI state, user actions, one-time UI effects. |
| `domain` | Business rules, use cases, repository contracts, and domain models. |
| `data` | API calls, local database/cache, DTOs, mappers, and repository implementations. |
| `core` | Shared utilities, networking, database, session, reusable UI, base models, and common contracts. |
| `app` | App startup, navigation host, dependency graph entry point, and global configuration. |
| `navigation` | Route definitions, screen transitions, and argument passing. |

### Data flow

```text
User Action
   ↓
Compose Screen
   ↓
ViewModel
   ↓
Use Case
   ↓
Repository Interface
   ↓
Repository Implementation
   ↓
Remote API / Local Database
   ↓
Mapper
   ↓
Domain Model
   ↓
UI State
   ↓
Compose UI
```

The important point: UI does not talk directly to the network or database. The UI talks to ViewModels, ViewModels talk to use cases/repositories, and data sources handle the actual APIs/storage.

### Why this architecture matters

- Features are easier to maintain.
- Business logic is separated from UI.
- DTOs do not leak into screens.
- Repositories can be mocked or replaced in tests.
- ViewModels focus on state management.
- Local and remote data sources stay isolated.
- Shared logic lives in `core` instead of being duplicated.
- New features can be added without breaking unrelated modules.

---

## 📦 Project Modules

| Module | Responsibility |
|---|---|
| `app` | Main Android application module, app entry point, dependency initialization, navigation host, and global app setup. |
| `core` | Shared logic used across the app: networking, database, DataStore, common models, shared UI components, helpers, and base contracts. |
| `on_boarding` | First-time user onboarding flow. |
| `login` | Login screen and authentication entry point. |
| `register` | Register screen and account creation. |
| `forgetpassword` | Password recovery. |
| `home` | Discover screen, categories, banners, discounts, brands, and product sections. |
| `brand` | Brand listing and brand product browsing. |
| `search` | Product search, suggestions, and product lookup flow. |
| `product_details` | Product detail screen, variants, reviews, colors, sizes, and cart action. |
| `favorite` | Wishlist/saved products feature. |
| `shopping_cart` | Cart items, quantities, totals, and checkout preparation. |
| `ai_integration` | Carto AI shopping assistant feature. |
| `map` | Map and location picking. |
| `addresses` | Address creation and address management. |
| `profile` | Account profile, customer data, order summary, and logout flow. |
| `settings` | Theme, language, and settings-related flows. |
| `orderhistory` | Customer order history. |
| `orderdetails` | Detailed order view. |
| `payment` | Payment/checkout-related flow. |
| `currency` | Currency-related logic. |
| `home_widget` | Home screen widget support. |
| `ai_widget` | AI widget support. |
| `product_reviews` | Product reviews and rating-related UI. |
| `splash.presentation` | Splash/startup presentation flow. |

---

## 🧩 Key Implementation Notes

### Product catalog

Carto integrates with Shopify-based catalog data. Products include:

- title
- description
- images
- vendor/brand
- product type
- category
- variants
- sizes
- colors
- stock quantity
- price and sale price

### Wishlist

Wishlist behavior is separated from normal product fetching. This keeps saved products stable and easier to maintain.

### Cart

The cart flow manages selected products, quantity changes, and totals while keeping UI state clear and predictable.

### Carto AI

Carto AI acts as a smart shopping assistant layer. It is visually separated from normal catalog screens but still belongs to the shopping experience.

### Address and map flow

The address flow supports location selection through a map-based experience, helping users pick delivery locations more clearly.

### Theme

The app supports light and dark mode with a consistent visual language across screens.

---

## 🚀 Run Locally

### Requirements

- Android Studio
- JDK 17 or newer
- Android SDK
- Valid backend/API configuration
- Firebase configuration if auth features are enabled
- Shopify/API credentials handled safely

### Clone

```bash
git clone <your-repository-url>
cd carto
```

### Build

```bash
./gradlew assembleDebug
```

### Install debug build

```bash
./gradlew installDebug
```

---

## 🔐 Security Notes

Do **not** hardcode private API keys or Shopify Admin tokens inside the Android app.

Mobile clients should only use safe public/client-facing APIs. Sensitive Admin operations must stay behind a backend.

Before pushing:

```bash
git status
```

Make sure local secrets and environment files are not tracked.

Common files to keep out of Git:

```gitignore
local.properties
*.jks
*.keystore
.env
google-services.json
```

Only commit `google-services.json` if your team policy allows it and it does not expose sensitive production configuration.

---

## 🛣️ Roadmap

- Improve offline-first catalog behavior.
- Add stronger unit and UI test coverage.
- Improve AI shopping recommendations.
- Expand localization support.
- Improve checkout/payment flow.
- Add richer product filtering and sorting.
- Add CI for build and test validation.
- Improve analytics and monitoring.
- Add more polished product recommendation flows.

---

## 🔁 Repository Redirection

This repository is for the **Carto Android mobile application**.

If you also maintain the **Carto Shopify Dashboard / importer / backend**, keep it in a separate repository and link it here.

Suggested links:

```text
Mobile App: Carto Android Client
Dashboard / Backend: Carto Shopify Dashboard
```

Replace these placeholders with your real repository URLs before publishing.

---

## 📄 License

This project is licensed under the MIT License.

See the [LICENSE](./LICENSE) file for details.

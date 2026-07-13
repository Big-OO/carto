# Carto 🛍️

Carto is a modern native Android e-commerce application built for fashion shopping, product discovery, wishlist management, cart flow, AI assistance, and a polished light/dark user experience.

<p align="center">
  <img src="promo/11-carto-intro-poster.png" alt="Carto app intro poster" width="850"/>
</p>
>>>>>>> 8a52d8997525547b27563fa69fd081edc114099d

## Quick Navigation

<<<<<<< HEAD
**Carto** is a modern, high-performance native Android e-commerce application. It integrates directly with the Shopify ecosystem via REST and GraphQL APIs to deliver a seamless shopping experience. Designed with a mobile-first philosophy, Carto features a rich design system, smooth Jetpack Compose transitions, robust local-first database caching, and a state-of-the-art **AI Shopping Assistant** capable of executing shopping operations directly in the app.

---

## ✨ Key Features

### 🛍️ Shopping Experience
- **Dynamic Product Catalog**: Browse products by brands, vendors, and categories synced live with Shopify.
- **Product Details**: Inspect images, dynamic sizes/colors selection, stock availability, pricing, description, and user reviews.
- **Global Search, Filter & Sort**: Search the catalog instantly. Filter by category, sub-category, or brand, and sort by price, best-sellers, or grouping.
- **Animated Coupons Carousel**: High-performance bottom-shadowed sliding banner displaying available discount rules with interactive click-to-copy animations.

### 🛒 Cart & Wishlist
- **Persistent Wishlist**: Save favorite items for later (authenticated users only).
- **Interactive Shopping Cart**: Add, update quantities (with stock limits check), and remove items. Prices, subtotals, and shipping are updated automatically.

### 💳 Address & Checkout
- **Address Manager**: Create, set default, and manage multiple customer shipping locations.
- **Multi-Payment Pipeline**: Checkout smoothly using Cash on Delivery (COD) or initialize card payment gateways.
- **Automatic Currency Converter**: Seamless price conversions across multiple local currencies using real-time sync.

### 🛜 Offline Resilience
- **Connectivity Monitor**: Dynamic network listeners track internet state. Displays a pulsing offline notification capsule when disconnected and alerts the user with a sleek checkmark banner upon connection recovery.

---

## 🤖 AI Chat Assistant

Carto features an integrated **AI Shopping Agent** that acts as a personalized stylist and shopping companion. Using Google AppFunctions, the assistant understands user requests in natural language and executes in-app actions on their behalf:

1. **Product Discovery**: Search products and check detailed variant options (sizes/colors).
2. **Cart & Wishlist Operations**: Ask the assistant to add items, update quantities, remove items, or view your current cart and wishlist.
3. **Smart Outfits & Comparison**: Request styling outfit ideas or compare specifications of products side-by-side.
4. **Interactive Coupon Checkout**: Inform the assistant of a coupon code (e.g. `banner1`, `banner2`) to automatically validate rules, compute discounts, and compile a Cash on Delivery (COD) order summary for checkout.

---

## 🛠 Tech Stack

- **UI & Animation**: Jetpack Compose, Material 3, Compose Motion & Transitions.
- **Architecture**: MVVM (Model-View-ViewModel), Hilt Dependency Injection.
- **Data & Network**: Retrofit, OkHttp, Apollo GraphQL, Room Database (Caching & Schemas).
- **Authentication**: Firebase Auth (Email/Password & Social Providers).
- **AI Integration**: AppFunctions Framework, Gemini LLM SDK, Kotlin Coroutines & Flows.

---

## 🚀 Getting Started

### Prerequisites
- JDK 17
- Android SDK (API 34+)

### Configuration
Create a `local.properties` file in the root directory and add your credentials:

```properties
SHOPIFY_API_KEY=your_shopify_api_key
SHOPIFY_PASSWORD=your_shopify_admin_password
SHOPIFY_HOSTNAME=your_shop_domain.myshopify.com
SHOPIFY_API_VERSION=2024-04
MAPBOX_ACCESS_TOKEN=your_mapbox_token
SUPABASE_URL=your_supabase_url
SUPABASE_ANON_KEY=your_supabase_anon_key
```

*Note: Firebase `google-services.json` must be placed in the `app/` directory.*

### Build & Run Commands

Use the Gradle wrapper to build the project:

- **Build Debug APK**:
  ```bash
  ./gradlew assembleDebug
  ```
- **Run Unit Tests**:
  ```bash
  ./gradlew testDebugUnitTest
  ```
- **Regenerate Apollo GraphQL Sources**:
  ```bash
  ./gradlew generateApolloSources
  ```
- **Run Lint Check**:
  ```bash
  ./gradlew lintDebug
  ```

---

## 📁 Project Structure

```text
app/
 ├── schemas/                           # Room database schemas
 └── src/
     └── main/
         ├── assets/ai/                 # System prompt files and assistant rules
         ├── graphql/                   # Apollo GraphQL query and schema files
         ├── java/com/shopify/carto/
         │   ├── app/                   # Application and Hilt initialization
         │   ├── core/                  # Shared database, utils, and sessions
         │   ├── feature/               # Feature-sliced modules (Home, Cart, Payment, AI)
         │   │   └── <feature_name>/
         │   │       ├── data/          # Repositories, mappers, API sources
         │   │       ├── domain/        # UseCases and models
         │   │       └── presentation/  # UI Views, Composables, ViewModels
         │   ├── navigation/            # App bottom bar and screen routers
         │   └── ui/theme/              # Material 3 typography and colors
         └── res/                       # Drawables, layouts, string files
```

---
=======
- [Overview](#overview)
- [Core Features](#core-features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Modules](#modules)
- [Screenshots](#screenshots)
- [Setup](#setup)
- [Repository Redirection](#repository-redirection)
- [License](#license)

## Overview

Carto delivers a complete shopping workflow:

- onboarding
- home product discovery
- categories and brands
- product details
- wishlist / saved products
- cart and checkout preparation
- AI shopping assistant
- map-based address picking
- profile and account management
- light and dark mode support

The app is structured as a modular Android project, so every major feature is isolated and easier to scale, test, and maintain.

## Core Features

- **Premium onboarding experience**
- **Home screen with light and dark themes**
- **Product browsing and product details**
- **Category and brand discovery**
- **Wishlist / saved products**
- **Shopping cart flow**
- **Carto AI shopping assistant**
- **Map-based address selection**
- **Profile, settings, and order screens**
- **Clean modular architecture**

## Architecture

Carto follows a **feature-based Clean Architecture** approach.

The project is not a single huge app module where UI, network calls, database logic, and business rules are mixed together. Instead, the app is divided into feature packages and shared core layers.

### High-level structure

```text
app
├── core
│   ├── common utilities
│   ├── networking helpers
│   ├── shared models
│   ├── storage/session helpers
│   └── reusable app-level logic
│
├── feature
│   ├── home
│   ├── brand
│   ├── search
│   ├── favorite
│   ├── shopping_cart
│   ├── product_details
│   ├── profile
│   ├── map
│   ├── addresses
│   ├── ai_integration
│   ├── settings
│   └── other isolated features
│
├── navigation
│   └── app navigation graph and route handling
│
└── ui.theme
    └── shared theme, colors, typography, and design tokens
```

### Typical feature structure

Most features follow this internal layout:

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
| `presentation` | Jetpack Compose screens, ViewModels, UI state, user actions |
| `domain` | Business rules, use cases, repository contracts, domain models |
| `data` | API calls, local database/cache, DTOs, mappers, repository implementations |
| `core` | Shared utilities, base models, networking/session helpers, common logic |
| `navigation` | App routes, screen transitions, argument passing |
| `ui.theme` | Colors, typography, shapes, light/dark theme definitions |
>>>>>>> 8a52d8997525547b27563fa69fd081edc114099d

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

### Why this architecture matters

- Features are easier to maintain.
- Business logic is separated from UI.
- DTOs do not leak into screens.
- Repositories can be mocked or replaced in tests.
- ViewModels focus on state management.
- Local and remote data sources stay isolated.
- New features can be added without breaking unrelated modules.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** Clean Architecture + feature-based modular structure
- **Dependency Injection:** Hilt
- **Networking:** Retrofit / GraphQL
- **Backend integrations:** Shopify + Firebase
- **Local persistence:** Room / DataStore
- **Concurrency:** Kotlin Coroutines / Flow
- **Image loading:** Coil
- **Maps:** Map-based address selection flow
- **Theme:** Light and dark mode

## Modules

### Main feature modules

```text
addresses
ai_integration
ai_widget
brand
currency
favorite
forgetpassword
home
home_widget
login
map
on_boarding
orderdetails
orderhistory
payment
product_details
product_reviews
profile
register
search
settings
shopping_cart
splash.presentation
```

## Screenshots

### Home - Light Mode

![Home Light](mockups/02-home-light-mockup.png)

### Home - Dark Mode

![Home Dark](mockups/07-home-dark-mockup.png)

### More Screens

| Screen | Preview |
|---|---|
| Onboarding | ![Onboarding](mockups/01-onboarding-mockup.png) |
| Saved / Wishlist | ![Saved](mockups/03-saved-mockup.png) |
| Product Details | ![Product Details](mockups/04-product-details-mockup.png) |
| Carto AI | ![Carto AI](mockups/05-carto-ai-mockup.png) |
| Brand Page | ![Brand Page](mockups/06-brand-page-mockup.png) |
| Cart | ![Cart](mockups/08-cart-mockup.png) |
| Map / Address | ![Map Address](mockups/09-map-address-mockup.png) |
| Profile | ![Profile](mockups/10-profile-mockup.png) |

## Setup

```bash
git clone <your-repository-url>
cd carto
```

Open the project in **Android Studio**, sync Gradle, then run it on an emulator or a real Android device.

## Repository Redirection

This repository is for the **Carto Android mobile application**.

If you also maintain the **Shopify dashboard / importer / catalog backend**, keep it in a separate repository and add its link here.

Suggested repository links:

```text
Mobile App: Carto Android Client
Dashboard / Backend: Carto Shopify Dashboard
```

Replace these placeholders with your real repository URLs before publishing.

## License

This project is licensed under the **MIT License**.

See the [LICENSE](LICENSE) file for details.

# <p align="center"><img src="app/src/main/ic_launcher-playstore.png" width="120" height="120" style="border-radius: 50%; box-shadow: 0 4px 12px rgba(0,0,0,0.15); border: 4px solid #ee7300f8;" /></p>

<h1 align="center">Carto</h1>

<p align="center">
  <strong>A Premium Native Android E-Commerce Application Powered by Shopify & AI</strong>
</p>

<p align="center">
  <a href="#-key-features">Features</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-ai-chat-assistant">AI Chat Assistant</a> •
  <a href="#-getting-started">Getting Started</a> •
  <a href="#-project-structure">Structure</a>
</p>

---

## 📌 Project Overview

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

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

# RageshMart

Multi-seller e-commerce marketplace built for the Anna University R2025 Semester 3
Java capstone (Servlets · JDBC · Apache Tomcat). Sellers list products, buyers
browse/cart/checkout (mock payment), and an admin moderates users, orders and listings.

> Built from `com.ragesh.rageshmart` — rename this package/project if you fork it
> for your own submission, per the spec's naming instructions.

## Problem Statement

Anna University students building an e-commerce capstone need a realistic-but-scoped
marketplace: multiple sellers, real search/cart/checkout flows, order history for
both roles, admin moderation, and reviews — without the complexity of a live payment
gateway or real-time infrastructure (explicitly out of scope). RageshMart implements
this as a layered MVC application over raw Servlets/JDBC to demonstrate the
fundamentals the course targets, rather than hiding them behind a framework like Spring.

## Architecture

```
Browser (HTML/CSS/vanilla JS + fetch)
      |
Filter layer -> EncodingFilter, RequestIdFilter, AuthFilter (session check)
      |
Servlets (controller) -> thin, HTTP orchestration only
      |
Service layer -> business rules, validation (no JDBC)
      |
DAO layer -> ALL SQL lives here, PreparedStatement only
      |
HikariCP connection pool (owned by DataSourceListener, a Singleton)
      |
H2 Database
```

Package layout: `controller`, `service` (+`service.impl`), `dao` (+`dao.impl`),
`model`, `dto`, `filter`, `listener`, `chat`, `util`, `exception`.

Design patterns used (see code comments for exact locations):
- **DAO** — data access abstraction (`dao` package)
- **Front Controller** — per-resource Servlets dispatch by URL pattern
- **Singleton** — `DataSourceListener` owns the one HikariCP pool
- **Factory** — `DAOFactory` centralizes DAO construction; `ChatProviderFactory`
  selects the chatbot backend via config flag
- **Strategy** — `ChatProvider` interface swaps `MockChatProvider` / `GeminiChatProvider`
- **Builder** — `ProductRequestDTO.Builder` for complex DTO construction

## Tech Stack

| Component | Choice |
|---|---|
| JDK | 17 |
| Servlet container | Tomcat 9.0.x |
| Build | Maven |
| Database | H2 (server mode in prod, embedded `jdbc:h2:mem:test` in tests) |
| Pooling | HikariCP |
| Views | JSP + JSTL, vanilla JS + fetch |
| JSON | Gson |
| Passwords | jBCrypt |
| Testing | JUnit 5 + Mockito |
| Logging | SLF4J + Logback |
| CI | GitHub Actions (`mvn -B clean verify` on every push) |

## Feature Coverage

F1 auth (buyer/seller + seed admin) · F2 seller listing CRUD · F3 browse/search ·
F4 cart · F5 mock-payment checkout · F6 order history (buyer + seller) · F7 admin
panel · F8 reviews/ratings (delivered orders only) · O2 order status workflow ·
AI chatbot (Section 11/17, mock provider by default).

Not yet wired into a UI: O1 wishlist, O3 seller sales dashboard.

## Setup Instructions

```bash
git clone <your-fork-url>
cd rageshmart
cp src/main/resources/config.properties.example src/main/resources/config.properties
cp .env.example .env   # fill in GEMINI_API_KEY only if you set ai.chatbot.provider=gemini

mvn clean package
```

**Local run (embedded H2, quickest):** leave `db.url` in `config.properties` as
`jdbc:h2:./data/rageshmart;DB_CLOSE_DELAY=-1` and drop the produced
`target/rageshmart.war` into Tomcat's `webapps/` folder, or run via your IDE's
Tomcat integration. The schema is applied and demo accounts are seeded
automatically on first startup (see `DataSourceListener` / `SeedDataInitializer`).

**Demo accounts** (seeded with real bcrypt hashes at startup — change before any
real deployment):

| Role | Email | Password |
|---|---|---|
| Admin | admin@rageshmart.com | AdminPass123 |
| Seller | seller@rageshmart.com | SellerPass123 |
| Buyer | buyer@rageshmart.com | BuyerPass123 |

**Server-mode H2 (for a real deployment):** start H2 as its own process
(`org.h2.tools.Server -tcp -tcpAllowOthers`) and point `db.url` at it — see
Section 10 of the spec PDF for the exact systemd/Tomcat reference setup.

## Deployed Link

_Add your live URL here once deployed (Week 8 / Sep 21 checkpoint)._

## Screenshots

_Add screenshots of the storefront, cart, seller dashboard and admin panel here
before the Full Build + Deploy checkpoint._

## API

All JSON endpoints are versioned under `/api/v1/...` and return the fixed envelope
`{ "success": bool, "data": ..., "error": { "code", "message" } | null }`. See
`dto/ApiResponse.java`. The chatbot proxy lives at `/api/chat` (Section 11).

## What's Left To Build

This scaffold covers the layered architecture end-to-end for F1–F8, O2, and the
chatbot, but a semester project has more surface area than one pass can cover.
Before your MVP review, still do:

- ER / Use Case / Sequence diagrams (Section 5) — not auto-generated here
- Wire `seller.jsp`'s dashboard to also show O3-style totals if you implement O3
- Flesh out DAO/service test coverage across Cart/Order/Review (only User/Product
  have example tests here — copy their pattern)
- Tune `checkstyle.xml` / SpotBugs thresholds and flip `failOnViolation`/`failOnError`
  to `true` once your codebase is clean, per Section 12
- Fill in real deployment details in Section 10 for whichever platform you pick

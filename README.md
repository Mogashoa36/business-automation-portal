# Business Automation Company — Website

Full-stack company website for a software business that specialises in **business process
automation**, delivers **custom business applications**, and provides **website development**
as a supporting service.

> We help businesses replace manual processes with secure, custom software and modern digital
> experiences.

```
company/
├── backend/                            Spring Boot 4.1 · Java 17 · JPA · PostgreSQL
│   └── src/main/java/company/backend/
│       ├── BackendApplication.java
│       ├── ServletInitializer.java
│       ├── config/                     CORS, typed settings, portfolio seeding
│       │   ├── ContactProperties.java
│       │   ├── CorsProperties.java
│       │   ├── PortfolioDataSeeder.java
│       │   └── WebConfig.java
│       ├── controller/                 Thin HTTP adapters
│       │   ├── ApiExceptionHandler.java
│       │   ├── ContactController.java
│       │   └── PortfolioController.java
│       ├── domain/
│       │   └── ServiceInterest.java    The service options the API accepts
│       ├── dto/                        Request/response contracts
│       │   ├── ApiErrorResponse.java
│       │   ├── ContactMessageRequest.java
│       │   ├── ContactMessageResponse.java
│       │   └── PortfolioProjectResponse.java
│       ├── entity/                     JPA entities
│       │   ├── ContactMessage.java
│       │   └── PortfolioProject.java
│       ├── exception/
│       │   ├── ContactSubmissionRateLimitException.java
│       │   ├── InvalidServiceInterestException.java
│       │   └── ResourceNotFoundException.java
│       ├── repo/                       Spring Data repositories
│       │   ├── ContactMessageRepository.java
│       │   └── PortfolioProjectRepository.java
│       └── service/                    Business logic
│           ├── ContactMessageService(.Impl).java
│           └── PortfolioProjectService(.Impl).java
│
└── frontend/business-automation-portal/ Angular 21 · standalone · SSR · SCSS
    └── src/
        ├── environments/environment.ts  Public API/site URLs (no secrets)
        └── app/
            ├── app.ts / .html / .scss   Shell: skip link, header, router, footer
            ├── app.routes.ts            Lazy loaded routes for every page
            ├── components/              One folder per page, per the site structure
            │   ├── home/ services/ solutions/ about/
            │   ├── portfolio/ technology/ contact/ not-found/
            ├── content/                 Typed static marketing content
            ├── layout/                  header/ footer/
            ├── models/                  TypeScript interfaces for API + content
            ├── services/                SEO, portfolio API, contact API
            └── shared/                  Reusable presentation components
```

## Architecture decisions

**The backend persists only genuinely dynamic data.** Contact enquiries and portfolio projects
live in the database because they change and are managed. Services, solutions, technology and
about copy are static marketing content, so they are typed TypeScript in `app/content/` — no
unnecessary tables and no API round trip for text that never changes.

**Services, repositories, controllers, DTOs.** Controllers never contain business logic; they
adapt HTTP to a service call. Services validate, normalise and persist. DTOs keep the public
contract separate from the persistence model, so entities are never exposed directly.

**Lazy loaded routes.** Every page is a separate chunk, so the initial bundle only contains the
shell, header and footer.

**Server-side rendering at request time.** Portfolio content is read from the API, so pages are
rendered on demand (`RenderMode.Server`) rather than prerendered at build time. This means the
build never depends on a running backend and the HTML always matches current database content.

**Graceful portfolio fallback.** If `GET /api/portfolio` cannot be reached, the website falls back
to a bundled snapshot (`app/content/portfolio-fallback.data.ts`) and says so on the page, rather
than showing an empty section.

**Security.** Validation runs in the browser *and* on the server. The API uses DTOs with
constraint annotations, resolves the requested service through an enum (unknown values are
rejected with HTTP 400), rate limits the public contact endpoint, returns a single consistent
error shape, and never leaks stack traces, SQL or entity internals. No credentials, keys or
tokens are committed anywhere — every environment-specific value comes from an environment
variable.

## Running it

### Backend

```bash
cd backend
./mvnw.cmd clean verify     # Windows
./mvnw clean verify         # macOS / Linux
```

It runs immediately with an embedded H2 database and seeds three sample portfolio projects, so
there is nothing to set up. To use PostgreSQL, export:

| Variable                      | Purpose                                       |
| ----------------------------- | --------------------------------------------- |
| `SPRING_DATASOURCE_URL`       | e.g. `jdbc:postgresql://localhost:5432/company` |
| `SPRING_DATASOURCE_USERNAME`  | Database user                                  |
| `SPRING_DATASOURCE_PASSWORD`  | Database password                              |
| `SPRING_JPA_DDL_AUTO`         | `validate` in production, `update` locally     |
| `CORS_ALLOWED_ORIGINS`        | Comma separated list of allowed front-end origins |
| `PORT`                        | HTTP port (default `8080`)                      |
| `SEED_SAMPLE_DATA`            | `false` to skip seeding the sample case studies  |
| `CONTACT_MAX_SUBMISSIONS`     | Contact submissions allowed per window (default `5`) |
| `CONTACT_RATE_WINDOW`         | Rate limit window, ISO-8601 (default `PT10M`)   |

```bash
./mvnw.cmd spring-boot:run
```

### Frontend

```bash
cd frontend/business-automation-portal
npm install
npm start          # http://localhost:4200
npm run build      # production build (server side rendered)
npm test           # unit tests
```

`src/environments/environment.ts` holds the public API base URL and the public site URL used for
canonical links. Change `apiBaseUrl` to wherever the backend runs; if you serve the API and the
site from one origin behind a reverse proxy, set it to `/api`.

> **Before going live:** replace the placeholder contact details in
> `src/app/content/company.ts` (email, phone, location) and the matching values in
> `src/index.html` (JSON-LD). They are marked as placeholders in that file.

## API

| Method | Endpoint                 | Purpose                                        |
| ------ | ------------------------ | ---------------------------------------------- |
| `POST` | `/api/contact`           | Submit an enquiry. Returns `201` + a reference |
| `GET`  | `/api/portfolio`         | All case studies, in display order             |
| `GET`  | `/api/portfolio/featured`| Case studies promoted on the home page         |
| `GET`  | `/api/portfolio/{slug}`  | One case study. `404` when the slug is unknown |

Errors always return:

```json
{
  "timestamp": "2026-09-27T20:42:47.99Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Please correct the highlighted fields and try again.",
  "fieldErrors": { "email": "Enter a valid email address" },
  "path": "/api/contact"
}
```

## Testing

```bash
cd backend  && ./mvnw test        # 22 tests: services, controllers, seeding, validation
cd frontend/business-automation-portal && npm test
```

## Content ownership

| Content                              | Where to change it                                          |
| ------------------------------------ | ---------------------------------------------------------- |
| Company name, contact details        | `src/app/content/company.ts` (and `src/index.html` JSON-LD) |
| Service copy                         | `src/app/content/services.data.ts`                          |
| Solution copy                        | `src/app/content/solutions.data.ts`                         |
| Technology stack                     | `src/app/content/technology.data.ts`                        |
| Home page outcome points             | `src/app/content/home.data.ts`                              |
| Portfolio case studies               | Database rows (seeded by `PortfolioDataSeeder`)             |
| Portfolio fallback snapshot          | `src/app/content/portfolio-fallback.data.ts`                |

### Adding a portfolio project

Insert a row in `portfolio_projects` (unique `slug`, `sample_project = false` for real work). The
website picks it up immediately — no frontend change or rebuild required.

### Sample project honesty

Seeded and bundled projects are flagged `sampleProject = true` and render a visible **"Sample
project"** label. They describe realistic scenarios; they are never presented as work delivered
for a real client.

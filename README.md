# Bizflow Technologies — Website

Full-stack company website for **BIZFLOW Technologies**, a software business in Johannesburg that
specialises in **business process automation**, delivers **custom business applications**, and
provides **website development** as a supporting service.

> We build software that automates business processes, simplifies operations, and turns manual work
> into efficient digital workflows.

|                      |                              |
| -------------------- | ---------------------------- |
| **Display name**     | BIZFLOW                      |
| **Descriptor**       | Technologies                 |
| **Legal name**       | Bizflow Technologies         |
| **Short name**       | Bizflow                      |
| **Location**         | Johannesburg, South Africa   |
| **Business hours**   | Mon–Fri, 08:00–17:00 (SAST)  |
| **Primary CTA**      | Let's Automate Your Business |

Services: Business Process Automation · Custom Business Applications · Website Development.
Solutions: Workflow Management · Operations Management · Employee Management · Procurement ·
Reporting & Dashboards.

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
├── frontend/business-automation-portal/ Angular 21 · standalone · SSR · SCSS
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
│
├── scripts/                          Local development launchers (Windows)
│   ├── start-all.cmd                 Backend + frontend together
│   ├── start-backend.cmd             Backend only, live output
│   ├── start-frontend.cmd            Frontend only, live output
│   └── stop-all.cmd                  Stops both
│
└── README.md
```

## Design system

Warm, editorial and precise — aimed at an enterprise software buyer rather than
a design portfolio. Everything is driven by tokens in `src/styles.scss` so no
component hard-codes a value.

**Palette** — a warm neutral scale, deliberately *not* pure white. Page background
`#ECE9E4` (bg-primary), raised band `#F5F3EF` (bg-secondary), card and input
surface `#FAF9F7`, ink `#211E1B` text, `#625E58` secondary, `#6A645C` muted, `#D6D1CA`
borders. Terracotta `#A94735` is the accent and stays under ~5% of any screen: buttons,
links, index numbers, active nav, hover states. Near-black `#171311` is kept for
the few dark moments only.

Pure white (`--c-pure`) survives in exactly one place: the inside of the product
and UI mockups, where a true application surface is what is being depicted.
It is never a page or section background.

`--text-muted` is darkened to `#6A645C` rather than the softer `#77716A` so that
muted labels clear WCAG AA (≈4.8:1) against the `#ECE9E4` page background. Form
labels and hints use the secondary ink `#625E58` for the same reason.

**Surfaces** — the site is light by default (roughly 70–80% light). A section
opts into the dark palette by adding `.theme-dark`, which re-points the same
semantic variables (`--bg`, `--text`, `--line`, `--accent`, …), so one set of
component styles works on either surface. Dark appears in four deliberate
places: the hero product visual, one feature band on the home page, the closing
CTA, and the footer. Sections are **not** alternated dark/light down the page.

**Type** — Space Grotesk for display and headings, Inter for body, loaded from
Google Fonts with `display=swap` behind a system fallback so the site still
reads correctly while the font loads or if fonts are blocked. The scale is
deliberately restrained: hero `clamp(2rem … 6rem)`, section `… 3rem`,
subsection `… 1.75rem`, card `… 1.16rem`, body `… 1.07rem`.

**Robustness** — the layout is verified from 375px to 1920px and at browser zoom
from 80% to 200%. There is deliberately **no `overflow-x: hidden` on `body`**:
clipping the page would mask real layout bugs and would silently break
`position: sticky`. Overflow is prevented at its source instead, via
`minmax(0, 1fr)` tracks, `min-width: 0` on grid and flex children, `clamp()` with
floors low enough to survive a ~187 CSS px viewport (375px at 200% zoom),
`overflow-wrap: break-word` as the last-resort guard, and `max-width: 100%` with
wrapping on buttons and the brand lock-up. A 200% zoom shrinks the CSS viewport
rather than scaling layout out, so it surfaces the same defects as a small phone
and the same rules cover both.

**Artwork** — the large visuals (hero workflow panel, dashboard, device trio,
operations console) are built from markup and CSS rather than photography. They
are vector, add no network requests, cannot break, and can be swapped for real
product screenshots later without changing any layout. They are styled to read
as an enterprise application — warm chrome, thin connectors, realistic data —
not as decoration. `.frame` is the light application surface and `.frame--dark`
the inverted one used on light pages.

**Real images** — where a genuine asset exists, it is shown rather than
imitated. `app-image` handles every one of them, so the performance and
accessibility rules cannot be forgotten in a template:

- **Space is reserved.** The aspect ratio sits on a wrapper with
  `aspect-ratio`, so the box exists before the bytes arrive. This is the main
  cause of layout shift on an image-led page.
- **Below the fold is lazy.** `loading="lazy"` by default; the LCP image
  opts into `eager` and `fetchpriority="high"`.
- **Decoration is the default.** `alt` defaults to `""`, so an image that
  should have been described cannot silently ship unlabelled. Real alt text
  is passed explicitly where an image carries meaning.
- **Hover is a 1.03 scale**, clipped by the wrapper, and switched off entirely
  under `prefers-reduced-motion`.

No photograph of the team or office ships with the site. A stock image of
someone else's workspace would misrepresent the business, so the About page
renders a slot that says what it is. Supplying a real photo is one line —
`src` and `alt` in `src/app/content/photo.data.ts` — and needs no template
change.

**Depth** — soft shadows only (`0 8px 30px rgba(33,30,27,.07)` and one step
below/above). Radii are moderate: 6px buttons, 10px cards, 16–18px mockups.
Pills are reserved for small tags and badges.

**Motion** — one system, four durations: `--d-fast` 160ms, `--d-ui` 220ms,
`--d-emphasis` 420ms, `--d-reveal` 600ms, all on `cubic-bezier(.22,1,.36,1)`.
Only `transform` and `opacity` are ever animated, so nothing triggers reflow
and there are no scroll listeners beyond the header's one boolean signal.
`data-anim` drives the hero entrance (staggered 80ms apart, `d0`–`d6`). The
`appReveal` directive drives scroll reveals in three flavours: `[appReveal]`
for one element, `[appRevealGroup]` to stagger a list's children 70ms apart, and
`[appMockup]` to assemble a product mockup in the order an app would draw it.
Route changes get an 8px / 220ms fade applied purely in CSS to `main > *`.
Mockups animate **once** on reveal and then hold still — nothing on this site
loops, floats or drifts.

The reveal's hidden state is scoped to `.js` (an inline script adds that class),
so the server-rendered HTML that crawlers and no-JS visitors receive shows all
content immediately. Under `prefers-reduced-motion: reduce` every animation and
transition is removed, all stagger delays are zeroed, and nothing is ever left
invisible.

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

## Administration portal

A separate, authenticated area for managing website content and enquiries. It is logically and
technically distinct from the public marketing site: its own routes, its own shell without the
public header or footer, and its own API surface behind `/api/admin/**`.

| Route                    | Purpose                             |
| ------------------------ | ----------------------------------- |
| `/admin/login`           | Sign in                             |
| `/admin/dashboard`       | Real counts and recent activity     |
| `/admin/portfolio`       | Create, edit, publish, delete       |
| `/admin/faqs`            | Create, edit, publish, delete       |
| `/admin/enquiries`       | Triage, status, internal notes      |
| `/admin/images`          | Upload, replace and caption images  |
| `/admin/users`           | Accounts, roles, password resets    |
| `/admin/activity`        | Audit trail                         |
| `/admin/change-password` | Change your own password            |

## Images and uploads

`/admin/images` is a small media library. An administrator picks a file, sees
its type and size, previews it, adds alt text and an optional caption, and
saves. The resulting URL is pasted into a case study's image field.

Uploads are the only place an unauthenticated visitor could push bytes onto
the server, so the handling is deliberately strict:

- **The file is decoded, not trusted.** The declared MIME type is checked
  against an allow list, the file's magic bytes must agree with it, and the
  image is then fully decoded with `ImageIO`. A script or an HTML document
  renamed to `.png` fails at one of those three steps. SVG is excluded
  deliberately, because it can carry script.
- **The stored name is generated, never supplied.** A UUID plus the extension
  derived from the validated type. The caller's filename is reduced to a
  display label and is never used to build a path, so traversal is removed as
  a class of bug rather than filtered case by case.
- **Uploads live outside the application.** They are written to a configurable
  directory (default `./uploads`, override with `IMAGE_UPLOAD_DIR`) and are
  read back only through `GET /api/images/{filename}`. Nothing maps that
  directory to a static handler, so a stored file can never be executed.
- **Serving is hardened.** The content type comes from the validated record,
  not from the file, and responses carry `nosniff`, an inline
  `Content-Disposition` and a restrictive `Content-Security-Policy`.
- **Alt text is mandatory.** An upload without it is rejected, so a meaningful
  image cannot reach the site undescribed.
- **Size is capped** at 5 MB (`IMAGE_MAX_FILE_BYTES`) before anything is
  written.

`AdminImageSecurityTest` covers each of these with genuinely hostile input:
renamed scripts, HTML, a disallowed MIME type, an oversized file, a traversal
filename and a traversal request path.

The upload directory is runtime data and is excluded by `.gitignore`.

### How authentication works

Authentication is a **server-side session**, not a token held by the browser:

```
Angular sign-in form
        ↓  POST /api/admin/auth/login  { identifier, password }
Spring Security (DaoAuthenticationProvider)
        ↓  BCrypt compares the submitted password against the stored hash
AdminUser lookup, enabled and lockout checks
        ↓
Security context saved to the HTTP session
        ↓  Set-Cookie: JSESSIONID (HttpOnly, SameSite=Lax)
Authenticated session
```

The browser only ever holds an opaque `HttpOnly` cookie that JavaScript cannot read. There is no
token in `localStorage`, nothing for a cross-site script to steal, and logout invalidates the
session **server side** rather than asking the browser to forget a value.

- **Passwords** are BCrypt hashes (strength 12). Plaintext is never stored, returned or logged.
- **Rate limiting** — a per-client sliding window plus a per-account failure counter. Five
  consecutive failures lock that account for 15 minutes.
- **Account enumeration** — an unknown account and a wrong password return an identical response.
- **Roles** — `ADMIN` manages everything; `EDITOR` manages content and enquiries. Account
  management and site settings are `ADMIN` only.
- **CSRF** — Spring Security's cookie-based double submit token, which the Angular `HttpClient`
  echoes automatically. Scoped to `/api/admin/**`; the stateless public contact form is excluded.
- **CORS** — explicit origin list with credentials allowed, so a wildcard is impossible.
- **Route guards** are a UX convenience only. The server refuses every `/api/admin/**` request
  without a valid session regardless of what the browser does.

### First run: creating the first administrator

There is **no default account and no default password** anywhere in the codebase. The first
administrator is created only if you supply bootstrap values before the first start:

```bash
export APP_ADMIN_BOOTSTRAP_USERNAME=your-username
export APP_ADMIN_BOOTSTRAP_EMAIL=you@example.com
export APP_ADMIN_BOOTSTRAP_PASSWORD='a-strong-temporary-password'
export APP_ADMIN_BOOTSTRAP_DISPLAY_NAME='Your Name'
```

The account is created with a **forced password change**, so the bootstrap secret is rotated at
the first sign in. The password is never logged and never echoed back. Once any administrator
exists, the bootstrap values are ignored.

If no bootstrap values are set the application still starts normally and simply logs that no
administrator exists yet.

### Administration environment variables

| Variable                        | Purpose                                              |
| ------------------------------- | ---------------------------------------------------- |
| `APP_ADMIN_SESSION_MINUTES`     | Idle session timeout (default `30`)                   |
| `APP_ADMIN_MAX_FAILED_ATTEMPTS` | Failures before lockout (default `5`)                 |
| `APP_ADMIN_LOCKOUT_MINUTES`     | Lockout duration (default `15`)                       |
| `APP_ADMIN_SECURE_COOKIES`      | `true` behind HTTPS, which any public deployment needs |
| `APP_ADMIN_ALLOWED_ORIGINS`     | Origins allowed to call the admin API                 |

### Database entities added

`admin_users`, `faq_items`, `activity_logs` and `site_contents`, plus `status`, `internal_notes`
and `updated_at` on `contact_messages`, and `published`, `category`, `created_at` and `updated_at`
on `portfolio_projects`. The schema is created by Hibernate `ddl-auto`, consistent with the
existing setup; for production use `SPRING_JPA_DDL_AUTO=validate` and apply the DDL as part of the
deployment.

### What the admin portal deliberately cannot do

Site content is restricted to an **allow list of keys** (`EditableContentKeys`). Database
credentials, API secrets, signing keys, encryption keys and environment variables are not
reachable through the admin API at all — the service rejects any key that is not on the list.
Contact email and phone are not editable either, because no real values have been supplied and
inventing them would be worse than omitting them.

## Client facing features

| Feature                   | Where it comes from                                                    |
| ------------------------- | ---------------------------------------------------------------------- |
| Automation assessment     | `shared/automation-assessment`, logic in `content/assessment.data.ts`   |
| ROI calculator            | `shared/roi-calculator`, logic in `content/roi-calculator.data.ts`      |
| Before / after showcase   | `shared/before-after`                                                  |
| Delivery timeline         | `shared/delivery-timeline`                                             |
| Interactive workflow demo | `shared/workflow-demo`                                                 |
| Service comparison        | `shared/service-comparison`                                            |
| Portfolio filtering       | `components/portfolio`                                                 |
| FAQ section               | `shared/faq-section`, served by `GET /api/faqs`                        |
| Mobile sticky CTA         | `shared/mobile-cta`                                                    |
| Scroll progress           | `shared/scroll-progress`                                               |

**The ROI calculator and assessment never transmit or store anything.** Both calculate entirely
in the browser from a signal in the component: there is no API call, no cookie and no analytics
event carrying a visitor's figures. All results are labelled **Estimate**, and the copy avoids
promising a saving, because a short form cannot honestly produce one.

The workflow demonstration is explicitly labelled as illustrative: it shows the shape of a typical
approval workflow, not a live system.

## Running it

### Quick start (Windows)

```bat
scripts\start-all.cmd     :: starts the backend and the frontend, logs to .logs\
scripts\stop-all.cmd      :: stops both again
```

`start-all.cmd` refuses to run if either port is already taken. To watch the
output live instead, run these in two terminals of their own:

```bat
scripts\start-backend.cmd    :: backend  on http://localhost:8080/api
scripts\start-frontend.cmd   :: frontend on http://localhost:4200
```

On macOS / Linux the equivalents are `cd backend && ./mvnw spring-boot:run`
and `cd frontend/business-automation-portal && npm start`.

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
canonical links and social previews. Both must be updated for a real deployment: point
`apiBaseUrl` at wherever the backend runs, and set `siteUrl` to the live domain so canonical and
Open Graph URLs are correct. If you serve the API and the site from one origin behind a reverse
proxy, set `apiBaseUrl` to `/api`.

Server side rendering only accepts requests for the hosts listed in `angular.json` →
`architect.build.options.ssr.security.allowedHosts` (SSRF protection). `localhost` and `127.0.0.1`
are allowed for development — **add your production domain there**, otherwise the server will refuse
to render and fall back to client side rendering.

> **Before going live:** `src/app/content/company.ts` deliberately ships with `email` and
> `phone` set to empty strings, because no real address or number has been supplied. Until they are
> filled in, the header, footer and contact page **hide** those rows rather than rendering an empty
> `mailto:`/`tel:` link, and the JSON-LD in `src/index.html` omits the `email` and `telephone`
> properties. Add the real values in `company.ts` and mirror them in `index.html` (JSON-LD) — no
> template changes are needed.

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
cd backend  && ./mvnw clean verify     # 22 tests: services, controllers, seeding, validation
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

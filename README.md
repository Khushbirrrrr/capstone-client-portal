# Capstone Client Portal — Sprint 3 preparation

Solo Capstone project. Client: Professor Sasi Arun. Evaluator: Professor Liz Dancy.
Built with Angular 20, Spring Boot (Java 21), PostgreSQL and Docker.

## Features

| Story | Implementation | Frontend route | Backend endpoint |
|---|---|---|---|
| US07 | Sign in / sign out with **portal-only usernames** | `/login` | `/api/auth/*` |
| US09 | Browse approved projects for the current term | `/projects` | `GET /api/projects` |
| US10 | Open approved project details (404 for unavailable projects) | `/projects/:id` | `GET /api/projects/{id}` |
| US11 | Select and order up to five distinct approved projects | `/preferences` | client ranking and API validation |
| US12 | Submit / update own ranked project preferences | `/preferences` | `GET/PUT /api/preferences` |
| US08 | Faculty: create/activate terms and create/delete student groups | `/faculty-management` | `/api/admin/*` |
| US23 | Privacy/security: server-side role checks, credential externalization, BCrypt, CSRF and session cookies | all pages | `SecurityConfig`, `AuthController` |

**Sprint 3 preview:** faculty can generate **draft, non-persistent** recommendations via `POST /api/matching/generate` and `/matching-preview`. This implements a limited US13 prototype only; no finalization, publishing, notifications or fully agreed matching policy yet.

**Out of scope:** finalized matching/assignment and publication (remaining US13–US15 workflows). Rankings are currently per **student account**; faculty groups are administered separately. Matching preview aggregates their members' individual rankings; group-wide submission workflow is not built. No registration or real Sheridan SSO integration.

## Start with Docker (recommended)

1. Install Docker Desktop on your Mac.
2. In the root folder, copy `.env.example` to `.env`.
3. Replace every `CHOOSE_...` placeholder with your own randomly generated **local demo** username/password. Do **not** reuse Sheridan email/SSO credentials, and do not commit `.env`.
4. Start services:

```bash
docker compose up --build -d
docker compose ps
```

5. Open **http://localhost:8088**. Log in using the portal usernames and passwords you placed in `.env`. You can demonstrate Student and Faculty dashboards.
6. View backend logs with `docker compose logs -f backend`.
7. Shut down with `docker compose down` (keeps PostgreSQL volume).

**Existing Sprint 1 database:** Existing legacy email/SSO-style accounts are deactivated on first startup and their stored school emails are replaced by non-deliverable placeholders. New demo usernames/passwords come only from `.env`. The existing project records remain. Back up the database before upgrading; `ddl-auto=update` is for development only.

**Important:** Changing `DB_PASSWORD` in `.env` does not reset the password of an existing initialized PostgreSQL volume. When retaining an existing volume, update that database password in PostgreSQL as well. Do not delete the volume unless you have backed it up.

## Run backend and frontend separately (Mac development)

From the root folder:

```bash
# In one terminal: Docker for database only
docker compose up -d postgres

# In another terminal: export local secrets and start Spring Boot
set -a; source .env; set +a
cd backend
bash mvnw spring-boot:run

# In another terminal: Angular dev server
cd capstone-client-portal
npm ci
npm start
```

Open **http://localhost:4200**. Angular uses `proxy.conf.json` to forward `/api` calls to Spring at localhost:8080.

## Verification commands

```bash
cd backend
bash mvnw test             # H2 test profile, no live PostgreSQL required

cd ../capstone-client-portal
npm ci                    # re-install Mac-native packages, don't reuse uploaded node_modules
npm run build
npm test -- --watch=false # Chrome is required for Karma, if available
```

See `docs/sprint-2/TEST_PLAN.md` and `docs/sprint-2/DEMO_SCRIPT.md` for the testing matrix and demo order.

## Security and hosting review required by Professor Liz

- Login uses unique portal usernames, not Sheridan email credentials. Never reuse institutional passwords.
- All demo usernames and passwords come from `.env`, which is ignored by Git. Old demo passwords, session-cookie text files, `.git` history, and node_modules are excluded from this delivery.
- BCrypt hashes are persisted; plain-text passwords are never returned to the frontend.
- Spring sessions use HttpOnly cookies; HTTP-changing operations require anti-CSRF tokens. Angular sends the `X-XSRF-TOKEN` header.
- Students cannot access faculty APIs; faculty cannot submit student rankings. Preferences are fetched by the logged-in session, not a request-supplied user ID.
- The Docker Compose example binds the frontend to `127.0.0.1` only. This is **local demo hosting**, not public deployment. A public deployment needs HTTPS/TLS termination, `SESSION_COOKIE_SECURE=true`, advisor-approved authentication/security review, reverse proxy/hosting and operational safeguards (rate limiting, DB migrations, backups, monitoring, credential recovery).
- Any passwords that appeared in **previous Git history** must be treated as compromised and rotated. Deleting files from a ZIP does not remove old commits from GitHub. Discuss Git history cleanup with technical advisors.

Project Trello board: https://trello.com/b/2ArA5EeW/capstone-client-portal

## Sprint 3 changes under review

- Faculty group form now uses an **Add member** button; 1–4 usernames, no duplicates, visible entries/removal.
- Backend enforces the four-student limit even if someone bypasses the Angular form.
- Login and group submission buttons require valid client-side fields.
- Faculty-only matching preview aggregates member rankings (5 points for first place to 1 for fifth), reserves each recommended project for one group, and flags unresolved groups.
- Matching preview is **greedy and group-ID ordered**. It may not be globally optimal; faculty should confirm the business rules before release. It does not publish anything or alter saved student rankings.
- Added backend and Angular unit tests. **These changes have not yet been executed in the uploaded environment.** Run and capture results before claiming them as passed.
- Docker frontend now supports runtime `PORT` and `BACKEND_HOST` via the official Nginx template processor. Local Compose still defaults to port 80 and host `backend`.
- See `docs/sprint-3/SPRINT_3_READINESS.md` for demo flow, Trello cards, technical advisor talking points, and deployment checklist.

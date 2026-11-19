# Sprint 3 readiness | Capstone Client Portal

**Presentation target:** Thursday, November 19, 2026. **Client:** Prof. Sasi Arun. **Evaluator:** Prof. Liz Dancy.

## What the instructor said after Sprint 2

- Trello testing column was empty; make tests and evidence visible.
- Technical advisor review of `SecurityConfig` and `AuthController` is outstanding.
- Portal must move off localhost; hosting is the highest-priority unresolved dependency.
- Faculty group creation needs an Add member button and a maximum of four students.
- Client-side validation should mark required fields and disable progression until valid.
- Nginx/reverse proxy choice needs a clear technical justification or simplification.
- Last-minute code changes should be avoided. Test-data hardcoding was called out.
- Instructor asked for the slide deck to be posted for review.

## What was implemented in this *preparation copy*

| Improvement | Source location | What it does | Verification |
|---|---|---|---|
| 4-member API cap | `backend/.../service/TermGroupService.java` | Rejects a fifth member and duplicate/invalid usernames | **Tests added; must be run** |
| Member input UI | `.../pages/faculty-management` | Add/remove one member at a time, visible `0/4` counter | **Tests added; must be run** |
| Frontend validation | Faculty management + login | Disable submit buttons for invalid form states | **Review manually** |
| Matching recommendations preview | `MatchingPreviewService` + `MatchingController` and Angular `/matching-preview` | Faculty previews draft recommendations; shows unresolved groups | **Tests added; must be run** |
| Faculty endpoint security | `SecurityConfig.java` | `/api/matching/**` requires faculty role | **Tests added; must be run** |
| Configurable Nginx proxy | Frontend `Dockerfile` + `nginx.conf` | Serves Angular and proxies `/api/` to a configurable private backend | **Build/deploy must be run** |
| Deployment configuration | `.env.example` and deployment notes below | Provides local example and host-specific settings | **NOT deployed** |

**Avoid claims that the work is finished until verification:** this is a source-code preparation copy, not a tested deployed release.

## Story scope and business rule caveat

- **US08 improvement:** group creation with 1–4 unique active portal student accounts.
- **US07 improvement:** disable login submission until portal username and password look valid.
- **US13 prototype:** faculty-only, read-only matching suggestion preview.
- The matching prototype uses scores of 5, 4, 3, 2, 1 for ranks 1–5. Scores are aggregated from individual group-member preferences. Groups are processed in ascending ID order; each project appears at most once. A group with no candidate is unresolved. **This is a greedy demonstration algorithm, not globally optimal allocation or final published matching.** Confirm business rules with the client and technical advisor.
- **Not implemented:** US14/US15 finalized allocations, manual overrides, publishing, notifications, group-wide consensus ranking, account creation and real SSO.

## Focused live demo (show only tested functionality)

1. **Before the meeting:** open the public HTTPS URL only **if actually deployed and tested**; otherwise state the deployment blocker and show local demo transparently.
2. Sign in as **faculty**. Show that this is a separate portal login, not Sheridan SSO.
3. Open **Terms & student groups**. Enter a valid group name; use **Add member**, observe visible member pills and `1/4` → `4/4`; the fifth cannot be added. Remove a member and show the counter changes. Use existing *distinct, active* student accounts; this seed currently only provides one demo student, so additional demo accounts must be safely provisioned for a full four-member success case.
4. Open **Matching preview**. Select a term, click **Generate matching preview**. Show a recommended group, its score and an unresolved group (if test data supports it). Point out that no assignment was saved or published.
5. Open **Trello**. Walk through Done / Testing / In Progress / Blocked cards, including the hosting and advisor blockers. Do not move a card to Testing until there is a test plan or test evidence attached.
6. Open **code**: `TermGroupService` (server-side four-person cap), `faculty-management.ts/html` (validation), `MatchingPreviewService` (scoring/choice), `SecurityConfig` (faculty endpoint protection), `nginx.conf` (proxy rationale).
7. Open **test results**. Execute the scripts below in advance, screenshot exact terminal result, attach to Testing cards. Do not reuse Sprint 2's 29-test count as a Sprint 3 count without rerunning.

## Verification commands and screenshots

From repository root:

```bash
cd backend
bash mvnw test
```

Screenshot: complete test summary, including *Tests run / Failures / Errors*. If it fails, capture the error and do not claim all passed.

```bash
cd ../capstone-client-portal
npm ci
npm run build
npm test -- --watch=false
```

Screenshot: Angular build output, Karma test output, add-member UI at 4/4 and disabled add, matching preview result with current term.

```bash
cd ..
# After creating your own local .env from .env.example:
docker compose up --build -d
docker compose ps
```

Screenshot: all 3 services running, local student/faculty login, one matching preview. This proves a local integration demo only, not public hosting.

**Other essential evidence:** faculty cannot access student preferences; student cannot access matching endpoints; group over 4 is rejected at API level; newly added `.env` is not committed; login/CSRF verified in running app; public HTTPS URL tested from another device (only if deployed).

## How to answer: “Why use Nginx?”

> I use Nginx inside the frontend container to serve the compiled Angular files and forward `/api` requests to Spring Boot. That keeps frontend requests on the same browser origin, so I avoid separate cross-origin authentication configuration for session and CSRF cookies. It is not mandatory; if the hosting platform provides equivalent static hosting and routing, I could remove it. I kept it here because the current Docker architecture uses the frontend as the single entry point.

**Do not say Nginx itself provides public HTTPS.** In the proposed Railway setup, Railway's public edge would handle HTTPS; Nginx would serve the frontend over the private container port.

## Hosting option: Railway, using the existing Dockerfiles

This is an **unexecuted deployment plan**. Obtain technical-advisor approval before exposing the app to real users; only use synthetic demo data and unique passwords in a private staging preview until review.

1. Push a clean commit to GitHub (no `.env`, credentials, session cookies, or test reports with secrets). Audit previous Git history, rotate historical secrets.
2. Open [Railway](https://railway.com). Create a project and a **PostgreSQL** service. Keep Postgres private.
3. Add a **Backend** GitHub service using root directory `/backend` and its `Dockerfile`. Set `PORT=8080`. Connect database using `DB_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}`, `DB_USERNAME=${{Postgres.PGUSER}}`, `DB_PASSWORD=${{Postgres.PGPASSWORD}}`. Set `SESSION_COOKIE_SECURE=true`. For a synthetic demo, set `PORTAL_DEMO_SEED=true` and supply unique custom portal usernames and passwords (at least 12 characters, separate from any school account). Never paste credentials into slides or Git.
4. Add a **Frontend** GitHub service using root directory `/capstone-client-portal` and its `Dockerfile`. Set `PORT=80`, `BACKEND_HOST=Backend.railway.internal` **using the actual private domain displayed by Railway**. The Docker image substitutes those values in Nginx on startup. Some providers normalise service names; verify exact DNS rather than guessing.
5. Generate a **public domain only for Frontend**, target port `80`. Do not expose the backend or database publicly. Verify `/login` and `/api/auth/csrf`, sign in, use faculty and student pages, then test from an independent device.
6. Capture URL and Railway logs (with passwords redacted) and put the verified public URL on the hosting Trello card. If the app fails, keep Hosting **Blocked**, post failure evidence, and do not claim it works.

**Security before a genuine production launch:** advisor review of `SecurityConfig` and `AuthController`, login rate limiting, account provisioning/recovery, SQL migrations instead of `ddl-auto=update`, secure headers, backup/restore, privacy/retention and access control. Hosting alone does not certify these controls.

## Trello cards to create or update

Suggested workflow: **Backlog → In Progress → Testing → Done** and **Blocked** for external dependencies. Each card should have a description, definition of done, screenshot/evidence, and one clear status.

- **S3 — Public HTTPS hosting** (In Progress/Blocked): public URL loads, student/faculty login works, logs reviewed, advisor approves staging. Evidence: actual URL + browser screenshot. Never mark Done on localhost.
- **S3 — Technical advisor security review** (Blocked until meeting): walkthrough of `SecurityConfig`, `AuthController`, sessions, CSRF, old exposed credentials, deployment and migrations. Evidence: dated meeting, notes, action items.
- **S3 — Group limit 4 + Add member UI** (Testing): demonstrate valid 1–4, fifth rejected, duplicate blocked, unknown user backend rejection. Evidence: test + UI screenshots.
- **S3 — Frontend required-field validation** (Testing): invalid login/group forms keep actions disabled. Evidence: screenshots.
- **S3 — Matching preview (US13 prototype)** (Testing): one recommended group and one unresolved group; no finalization. Evidence: test summary + page screenshot.
- **S3 — Test suite and regression** (Testing): Maven test summary, Angular tests, Docker integration and negative tests. Evidence: dated terminal results.
- **S3 — Slides and demo readiness** (In Progress): deck uploaded to review channel, story/demo order clear, no untested feature in demo.
- **S4 — Final matching/review/publish US14–15** (Backlog): client-approved matching criteria, faculty overrides, finalize/publish, end-to-end tests.

## One-minute intro script

> The Capstone Client Portal is a student-and-faculty application for exploring capstone projects and collecting student preferences. Sprint 2 established term management, rankings and security controls. For Sprint 3, my focus is turning review feedback into measurable improvements: clearer group entry with a four-student limit, validation before submission, and a faculty-only matching preview. I also prioritized testing evidence and hosting. I’ll show what is actually working, explain the design decisions, and identify anything still awaiting verification or technical-advisor approval.

## When the evaluator asks why the matching is greedy

> The current version is a deterministic preview. It processes groups consistently by ID, totals each member's ranked-choice points and avoids recommending the same project twice. I know a greedy algorithm is not globally optimal, so I am not presenting this as final allocation. The final matching rules and faculty adjustments belong in the next iteration after client confirmation.

## End-of-demo conclusion

> This sprint addresses specific review comments and adds a first faculty-facing matching preview. I am keeping deployment, test verification and the advisor review visible as separate acceptance criteria. Sprint 4 will focus on the approved allocation workflow, final security/deployment checks, and a complete end-to-end demonstration.

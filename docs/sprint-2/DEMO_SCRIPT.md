# Sprint 2 Demo and Code Review — Professor Liz Dancy

**Meeting:** Friday, November 6, 2026 at 10:00 AM. Start the Teams meeting on time even if the professor joins later.

## Demo sequence

1. **Intro (1 minute):** "The Capstone Client Portal helps students explore faculty-approved projects and submit ranked project preferences. In Sprint 1 I delivered separate student/faculty login and the approved project list. Sprint 2 adds project details, ranking, submission, and faculty term and group management, with security improvements from our last review."
2. **Student login (1 minute):** Show a **portal-only username** login; explain that no Sheridan credentials are requested. Show rejection of an email-style username only if time allows.
3. **US09→US10 (2 minutes):** Open Browse Projects → View Details. Explain that the backend only returns APPROVED projects for the selected term and returns 404 for invalid IDs.
4. **US11→US12 (3 minutes):** Open My Preferences, select 2–3 projects, move one up, submit, navigate away and back, verify saved order. Update the ranking and submit again.
5. **US08 (2 minutes):** Sign out, sign in as Faculty, show terms, add a teaching term, activate it only if you have a corresponding project catalogue, create a demo group using an existing portal student username, show member list. (Avoid changing the active demo term unless prepared to demonstrate an empty catalogue.)
6. **Go directly into code review** — do not pause for a separate planning presentation.

## Code review sequence

- `AuthController` and `SecurityConfig`: username validation, BCrypt hash checking, session security, CSRF, student/faculty roles.
- `DataInitializer`: local-only demo secrets, disable migrated accounts, avoid institutional emails.
- `ProjectController`, `ProjectService`, `ProjectRepository`: active-term catalogue, approved detail by ID, 404 edge cases.
- `PreferenceController`, `PreferenceService`, `RankedPreference`: session identity, rank validation, transactional replace, ownership.
- `AdminController`, `TermGroupService`: faculty term activation and group creation with validation.
- Angular `pages/login`, `pages/projects`, `pages/project-detail`, `pages/preferences`, `pages/faculty-management`; services and route guard.
- Show automated test files and actual results **only after running them**. Explain negative/edge tests and any known limitations without claiming unverified results.
- Show Docker Compose, Nginx proxy and deployment discussion with technical advisor. Local Docker-only setup is **not** public hosting.

## Likely questions

**Why usernames instead of Sheridan email?** Avoid confusion with Sheridan SSO; portal credentials are separate, and passwords are never stored in GitHub.

**How do you protect student preferences?** The student ID comes from the authenticated session, not a form parameter. Each query filters by that ID and teaching term.

**Why do you delete then insert rankings?** To atomically replace the complete order while preserving unique rank/project constraints, after all input has been validated.

**Why is CSRF enabled?** Session-cookie apps are vulnerable to cross-site request forgery without anti-CSRF tokens on state-changing requests.

**What remains for Sprint 3?** Group-level collaborative ranking, automated matching and recommendations, any requested faculty adjustment workflow, and a technical-advisor-approved secure public hosting plan.

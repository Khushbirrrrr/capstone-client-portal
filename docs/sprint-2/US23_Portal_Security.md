# US23 — Protect Private Information / Redesign Sign-In

**Actors:** Student and Faculty

**User story:** As a portal user, I want my credentials and my private data protected without entering my Sheridan Single Sign-On credentials.

**Sprint 1 feedback addressed:**
- Replace school-email login with unique lowercase-normalized portal usernames; reject `@` email strings.
- Remove redundant email `equalsIgnoreCase` lookup.
- Remove HealthController as requested.
- Disable old Sprint 1 demo accounts during migration; use newly provisioned account names/passwords only from a local `.env` file.
- Never send password hashes or legacy email fields in API responses.
- Use BCrypt password verification and an HttpOnly session cookie; rotate session ID when already present.
- Require Spring Security roles for student preferences and faculty administrative APIs.
- Enable CSRF with an Angular-compatible cookie/header pair (`XSRF-TOKEN` and `X-XSRF-TOKEN`).

**Normal flow:** Student opens login → sees warning "This is not Sheridan SSO" → enters portal-only username/password → session created → student dashboard → logout.

**Edge cases:** Empty username/password; institutional email mistakenly entered; wrong password; unknown or inactive user; unauthorized direct navigation; CSRF missing on save; attempt to access another student's rankings.

**Important limitations requiring technical advisor review:** This local demo does **not** contain user self-registration/password reset, MFA, rate limits, account lockout, HTTPS public deployment, formal DB migrations or a production privacy review. Do not put real student accounts/data into the system until these have been reviewed.

**Code review:** `AuthController`, `DataInitializer`, `SecurityConfig`, `.env.example`, `Auth` Angular service, `login` page, `auth.guard.ts`.

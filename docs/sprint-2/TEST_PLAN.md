# Sprint 2 — acceptance criteria and test matrix

## US07 + US23: portal login and privacy

- **Happy path:** locally configured student login goes to student dashboard; faculty login goes to faculty dashboard.
- **Negative:** unknown username, wrong password, inactive user rejected with 401.
- **Negative:** email-style username rejected; UI explicitly warns not to enter Sheridan SSO credentials.
- **Access:** unsigned users cannot browse projects; students cannot call `/api/admin/**`; faculty cannot submit preferences.
- **CSRF:** PUT to `/api/preferences` without a valid anti-CSRF token is 403.
- **Data:** no seeded plain-text secrets in source or tests; `.env` is ignored; no sensitive account fields in JSON response.

## US08: manage teaching terms and student groups (Faculty)

- Faculty can create terms and set one active term.
- Faculty can select a term, create groups with a name and one or more existing student portal usernames, list groups, and delete groups.
- Unknown or inactive student usernames and duplicate memberships are rejected.
- Duplicate group names within the same term are rejected; duplicate term names are rejected.
- Students cannot access faculty endpoints.

## US09 + US10: approved catalogue and details

- Student sees approved projects in the active teaching term only.
- Clicking `View details` opens project title, description, status, ID and term.
- Unknown/unapproved/wrong-term project IDs return 404 and show a friendly message.
- Unauthenticated/faculty requests to student-only endpoints are denied.

## US11 + US12: rank and update preferences

- Student selects 1–5 approved projects, moves them up/down and removes selections in the UI.
- Duplicate project IDs, more than five, zero projects, unpublished project IDs, wrong-term IDs return 400.
- `PUT /api/preferences` saves rank order 1,2,3… and replaces previous ranking in one transaction.
- `GET /api/preferences` returns **only the signed-in student's** rankings.
- Reopening `/preferences` shows the submitted ordering.
- A failure during validation does not delete previously saved preferences.
- The UI warns that selection changes are unsaved until Submit/Update is clicked.

## Verification status in provided deliverable

- Angular TypeScript compilation: PASS (`tsc --noEmit`).
- Angular template compilation: PASS (`ngc --noEmit`).
- Angular test source TypeScript compilation: PASS (`tsc -p tsconfig.spec.json --noEmit`).
- Full Angular production build, Chrome Karma, Maven tests, PostgreSQL integration and Docker runtime: **NOT VERIFIED IN BUILD ENVIRONMENT**. The uploaded node_modules were macOS ARM-native and missing Linux-native build packages; Maven could not download its distribution. Execute verification commands on a development computer before presenting or deploying.

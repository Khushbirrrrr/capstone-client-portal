# US11 / US12 — Create Ordered Preferences and Submit/Update

**Actor:** Student

**User stories:**
- **US11:** As a student, I want to order my preferred approved projects, so I can express priority.
- **US12:** As a student, I want to submit or update that order, so faculty has my latest rankings.

**Precondition:** Student logged in; approved projects available for the active term.

**Normal flow:**
1. Student opens **My ranked preferences**.
2. The screen loads the current term's approved catalogue and any saved preferences.
3. Student adds up to five projects, uses ↑ / ↓ to reorder, and may remove selections.
4. Student clicks **Submit / Update Preferences**.
5. API validates the number, uniqueness, project status and term for all IDs.
6. Backend replaces only the current student's ranking in a transaction.
7. UI shows a success message and timestamp; a reload reproduces the saved order.

**Postcondition:** Ranked project records contain consecutive order numbers and submission timestamp.

**Edge cases:** Empty list; >5 projects; duplicate IDs; null project; pending/unapproved/wrong-term ID; faculty/anonymous attempt; failing database write (transaction rollback); editing after prior submission.

```mermaid
sequenceDiagram
    actor Student
    participant UI as Angular Preferences
    participant API as PreferenceController
    participant S as PreferenceService
    participant DB as Preference + Project Repositories
    Student->>UI: Add, reorder and click Submit
    UI->>API: PUT /api/preferences with ordered project IDs + CSRF
    API->>S: submit(session.username, term, IDs)
    S->>DB: Validate every project is approved
    alt Input invalid
        S-->>UI: 400, leave previous ranking unchanged
    else Valid order
        S->>DB: Delete only own old rankings; flush
        S->>DB: Insert new ordered rows (same transaction)
        DB-->>UI: Submitted ranking + timestamp
        UI-->>Student: Show saved confirmation
    end
```

**Storyboard:** Browse Projects → My Preferences [available list] → [#1, #2, #3 with arrows, remove] → [Submit/Update] → success message and last submitted timestamp. Reload verifies persistence.

**Code review:** `PreferenceController`, `PreferenceService`, `RankedPreference`, `PreferenceRepository`, Angular `Preferences` + `PreferenceService`.

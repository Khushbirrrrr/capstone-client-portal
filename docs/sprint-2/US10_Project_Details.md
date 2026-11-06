# US10 — View Approved Project Details

**Actor:** Student

**User story:** As a student, I want to open an approved project's details so I can determine whether to include it in my ranked preferences.

**Precondition:** Student logged in; project belongs to current term and is approved.

**Normal flow:** Student logs in → opens **Browse projects** → selects **View details** → sees project title, description, ID, status and term → can **Add to my preferences**.

**Postcondition:** Project details are displayed; optional navigation passes the project ID to the ranking screen without submitting it automatically.

**Edge cases:** Non-numeric or negative project ID; missing ID; draft, pending or wrong-term ID must not be exposed; unauthenticated or faculty requests are denied.

```mermaid
sequenceDiagram
    actor Student
    participant UI as Angular ProjectDetail
    participant API as ProjectController
    participant S as ProjectService
    participant DB as ProjectRepository
    Student->>UI: Click View Details
    UI->>API: GET /api/projects/{id}?term=...
    API->>S: getApprovedProject(id, term)
    S->>DB: findByIdAndStatusAndTerm(id, APPROVED, term)
    alt Project not approved / missing
        DB-->>S: Empty
        S-->>UI: 404 Not Found
    else Approved project
        DB-->>S: Approved project
        S-->>UI: Project details
        UI-->>Student: Display title, description, term
    end
```

**Storyboard:** Approved project card [View details] → detail page [Title] [Status] [Term] [Description] [Add to my preferences] → ranking page.

**Code review:** `ProjectController`, `ProjectService`, `ProjectRepository`, `ProjectDetail`, `ProjectService` Angular.

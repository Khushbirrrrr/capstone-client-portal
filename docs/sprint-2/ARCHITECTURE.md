# Sprint 2 architecture and class responsibilities

```mermaid
flowchart LR
    U[Student browser] --> A[Angular application]
    F[Faculty browser] --> A
    A --> N[Nginx local proxy / Angular dev proxy]
    N --> B[Spring Boot API]
    B --> S[Spring Security session + roles + CSRF]
    S --> C[Auth, Project, Preference, Admin controllers]
    C --> L[Services: projects, preferences, term/group]
    L --> J[JPA repositories]
    J --> D[(PostgreSQL)]
```

## Entity model

```mermaid
erDiagram
    APP_USER ||--o{ RANKED_PREFERENCE : submits
    PROJECT ||--o{ RANKED_PREFERENCE : ranked_as
    TERM ||--o{ STUDENT_GROUP : contains
    APP_USER }o--o{ STUDENT_GROUP : enrolled_in
    APP_USER {
        long id PK
        string username UK
        string passwordHash
        string role
        boolean active
    }
    PROJECT {
        long id PK
        string title
        string description
        string status
        string term
    }
    RANKED_PREFERENCE {
        long id PK
        long user_id FK
        long project_id FK
        string term
        int rank_order
        timestamp submitted_at
    }
    TERM {
        long id PK
        string name UK
        boolean active
    }
    STUDENT_GROUP {
        long id PK
        string name
        long term_id FK
    }
```

*The `APP_USER` entity also retains a legacy non-deliverable email placeholder column solely for compatibility with the Sprint 1 schema. It is **not** used for login.*

## Request flow

1. Angular browser requests `GET /api/auth/csrf`, receives an anti-CSRF cookie, then signs in with a **portal username**.
2. Spring Security saves the user role and username in an HttpOnly session cookie.
3. Angular route guards improve navigation, but backend role restrictions are authoritative.
4. Student project catalogue only reads approved rows; project detail IDs are additionally validated against term and status.
5. Student preference controller obtains the session username (never trusts a client-provided user ID).
6. Faculty term/group endpoints are protected by `ROLE_FACULTY`.
7. JPA repositories persist changes in PostgreSQL; rankings are atomically replaced after validation.

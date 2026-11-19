# Technical advisor checklist — must be reviewed before real accounts or public hosting

1. Confirm portal-only username policy and how portal accounts should be provisioned, reset and revoked. Never collect Sheridan SSO passwords or ask users to reuse them.
2. Audit historical GitHub commits for committed passwords, cookies, database configuration and passwords in old test fixtures. Rotate **all** secrets exposed historically before any deployment.
3. Review use of Spring Security session auth, CSRF cookie, HTTPS-required secure cookies, login rate limiting, account locking and security headers.
4. Approve deployment architecture (hosting provider, managed PostgreSQL, TLS reverse proxy, environment secret store, backup/restore, logging, domain access and incident recovery).
5. Replace Hibernate `ddl-auto=update` with reviewed migrations (Flyway/Liquibase) before moving beyond local development.
6. Decide whether preferences should be per student or shared by a formally assigned student group. Current Sprint 2 implementation is **per account**.
7. Confirm privacy policy, data retention, accessibility and any Sheridan IT/security review before real student data.

Local Compose publishes the frontend at 127.0.0.1:8088 for the laptop demo only. No public hosting has been performed.

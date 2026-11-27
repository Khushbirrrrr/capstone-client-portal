# Sprint 4 candidate (not verified)
New faculty-only final allocation endpoints and UI, immutable saved snapshot with override reason, previous project ID, actor and timestamp. Draft overrides live in the browser until confirmation. One group per project; all groups must be assigned before confirmation. Client approval required. No student publication or client contact details; US19 is partial. No reopen/reset workflow. Existing groups/projects can still be changed elsewhere; saved rows are historical snapshots. Stale check compares regenerated recommendations, not every input change. Serializable transaction and unique constraints are intended to protect concurrent confirmations; integration tests needed.

## Verify before deployment
1. In backend run `bash mvnw test` (Java 21).
2. In capstone-client-portal run `npm ci`, `npm run build`, `npm test -- --watch=false --browsers=ChromeHeadless`.
3. Use a separate staging database, back it up, and start via existing Docker configuration. Hibernate update creates final_allocations; verify schema and unique constraints. Never use real client data for testing.
4. Faculty: select a term, generate preview, change a recommendation to an approved unused project, enter reason, confirm. Reload and choose View saved allocations. Verify persisted rows and audit fields.
5. Try missing reason, duplicate project, unresolved group, stale preview, student access, and missing CSRF; expect rejection without saved rows. Use separate fresh test terms for successful confirmation.
6. Existing DataInitializer already rejects blank/short passwords; collision guard added. findAll migration still needs review; no change to migration semantics. Username regex already places hyphen last. Second-term fixture not added.
7. CI workflow added but not executed. Do not deploy just before the demo; preserve known-good Sprint 3 deployment until checks pass.

# Contributing to RageshMart

Exact steps from `git clone` to a running local instance (Section 18, rule 4).

1. `git clone <your-fork-url> && cd rageshmart`
2. `cp src/main/resources/config.properties.example src/main/resources/config.properties`
3. `cp .env.example .env` (only needed if you set `ai.chatbot.provider=gemini`)
4. `mvn clean package` — this also runs Checkstyle/SpotBugs and the test suite
5. Deploy `target/rageshmart.war` to a local Tomcat 9.0.x, or run it through your
   IDE's Tomcat/Servlet integration
6. Open `http://localhost:8080/rageshmart/` — schema + demo accounts are seeded
   automatically on first startup

## Branching & commits

- `main` is always deployable. Work in `feature/<name>` branches.
- Commit messages follow Conventional Commits: `feat:`, `fix:`, `test:`, `docs:`.
- Minimum 3 commits/week across the checkpoint window (Section 8).
- PRs are self-reviewed; describe what changed, why, and how it was tested.
  Check off: tests added, docs updated, migration included if schema changed.

## Adding a feature / fixing a bug (Section 15)

1. Open a GitHub Issue first (Feature/Change Request or Bug Report template).
2. Add a 3–4 line impact analysis on the issue before implementing: does this need
   a DB migration? Does an API response shape change? Additive or shared-code?
3. Branch, implement, open a PR against `main`.
4. Bump the version per semver and add one line to `CHANGELOG.md` in the same PR.

## Database changes

Never hand-edit a live table. Add a new numbered file under `db/migrations/`
(e.g. `V2__add_wishlist_table.sql`) and update `src/main/resources/schema.sql`
to match so local/test setups stay in sync.

## Definition of Done (Section 19)

- [ ] Compiles with no Checkstyle/SpotBugs major warnings
- [ ] Unit/DAO tests written and passing
- [ ] Code self-reviewed prior to merge
- [ ] Migration script included if schema changed
- [ ] Verified against the deployed URL, not only localhost
- [ ] README/API docs updated if behavior changed
- [ ] Merged to `main` only when CI is green

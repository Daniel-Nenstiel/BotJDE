# BotJDE — Agent Guide

A personal Discord bot for friends servers. Spring Boot 3, Discord4J, PostgreSQL, MyBatis, Flyway. Two Docker environments: dev (Snip3sh0t, port 8081) and prod (BJDE, port 8080).

## Architecture

- **New slash command**: `@Component implements SlashCommand` in `commands/`. Auto-discovered by Spring, auto-registered with Discord at startup.
- **New scheduled message**: Extend `BaseScheduledMessage`. Orchestrator runs hourly; puzzles at 6am, birthdays/anniversaries at 9am.
- **Persistence**: MyBatis XML mappers. Canonical XMLs are in `mappers/` — ignore duplicates under `scheduled/*/mapper/` (stale).
- **Config**: Server-specific config in `application-dev.yml` / `application-prod.yml`. Base `application.yml` is defaults + env refs only.
- **Formatting**: Spotless enforces 2-space indentation (`.editorconfig`). Auto-format with `./gradlew spotlessApply`.
- **Integration tests**: Tagged `@Tag("integration")`, use H2 in PostgreSQL mode, excluded from default `./gradlew test`.

## Rules — Always Do

1. New slash command → add to commands table in `readme.md` + add unit tests in `src/test/.../commands/`
2. New `Server` config field → update `Server.java` + **both** `application-dev.yml` and `application-prod.yml`
3. Database change → new Flyway migration only (`V{n+1}__description.sql`), never edit existing
4. Any code change → run `./gradlew test`
5. Formatting & Lint → `./gradlew spotlessApply checkstyleMain checkstyleTest`
6. User-facing change → update `readme.md`
7. Architectural / tooling / rule change → update `AGENTS.md`
8. Shell commands → log to `AI.commands.log` via `2>&1 | tee -a AI.commands.log`

## Rules — Never Do

1. Modify existing Flyway migrations
2. Put secrets in any `.yml` — use env vars
3. Delete `PocketCommandHandler` — intentional skeleton from `origin/pocket` branch
4. Put server-specific config in `application.yml`

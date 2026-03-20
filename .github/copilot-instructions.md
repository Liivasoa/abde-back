# Copilot Repository Instructions

## Default Routing Policy

- For any non-trivial request, always delegate to `RootAgent` first.
- A request is considered non-trivial if it includes one or more of the following:
  - code changes
  - architecture decisions
  - refactoring
  - test strategy
  - migration or persistence work
  - multi-file analysis

## Allowed Direct Handling (No Delegation)

- You may answer directly without delegating to `RootAgent` only when:
  - the user asks a short clarification question
  - the user asks for a very small informational answer
  - no code or file update is needed

## Fallback Behavior

- If `RootAgent` is unavailable, continue locally while still enforcing RootAgent rules and project constraints.
- When continuing locally, keep the same process discipline:
  - English-only outputs for project artifacts
  - TDD Red/Green/Refactor
  - feature implementation order: Domain -> Application -> Persistence -> Controller (if needed)
  - commit discipline at each step

## Delegation Intent

- `RootAgent` is the default orchestrator for this repository.
- Specialized agents (`DomainExpert`, `TestEngineer`, `DbMigrationAgent`) are selected by `RootAgent` as needed.

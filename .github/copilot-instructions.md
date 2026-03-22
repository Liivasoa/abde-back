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
- Always mention what agent you are delegating to when you do so, and provide a brief rationale for the delegation.
- After `RootAgent` is selected, it must delegate execution by task type and should not perform all implementation tasks itself:
  - `CodeExpert` for production code implementation/refactoring.
  - `TestExpert` for test design, test implementation, and test strategy.
  - `DbExpert` for Flyway migrations and persistence schema evolution.
- `RootAgent` may orchestrate, sequence, and validate, but execution work for the above task types must be delegated.

## Allowed Direct Handling (No Delegation)

- You may answer directly without delegating to `RootAgent` only when:
  - the user asks a short clarification question
  - the user asks for a very small informational answer
  - no code or file update is needed

## Fallback Behavior

- If `RootAgent` is unavailable, continue locally while still enforcing RootAgent rules and project constraints.
- When continuing locally, keep the same process discipline:
  - English-only outputs for project artifacts
  - strict TDD Red/Green/Refactor with test-first gate (no production code before failing tests)
  - mandatory stop after each TDD phase for user review/commit:
    - stop after Red
    - stop after Green
    - stop after Refactor
  - feature implementation order: Domain -> Application -> Persistence -> Controller (if needed)
  - commit discipline at each step

## Delegation Intent

- `RootAgent` is the default orchestrator for this repository.
- Specialized agents (`CodeExpert`, `TestExpert`, `DbExpert`) are selected by `RootAgent` by task type.

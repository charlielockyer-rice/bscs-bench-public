# Docker Sandbox Migration

This document replaces the "combined container" direction for benchmark runs.

## Goal

Keep agents isolated from the host while preserving strict, disposable grading
semantics for code tests.

The new target design is:

- Agent runs inside a Docker Sandbox.
- Tests run in fresh ephemeral course containers.
- Agent and tests share the same mounted workspace.
- Agent and tests do not share the same live container instance.

## Why We Are Changing Direction

The combined-container approach mixed two incompatible trust models:

- Agent container needs broader capabilities:
  - network access
  - agent auth/config
  - longer-lived process state
- Test execution needs tighter isolation:
  - no network by default
  - no agent credentials
  - disposable process/filesystem state

Trying to make both happen in one live container produced complexity and
behavior drift, especially around network isolation and runner parity.

## Target Architecture

### 1. Outer Boundary: Docker Sandbox

`bench-cli` launches the selected agent inside a Docker Sandbox.

Responsibilities:

- isolate the agent from the host
- provide workspace access
- provide agent auth/config inside the sandbox
- expose MCP `test` / `submit` tools to the agent

### 2. Test Boundary: Fresh Course Containers

Each MCP `test` call launches a fresh course image container from the sandbox's
private Docker daemon.

Responsibilities:

- mount the same workspace
- run grading/test commands
- use strict runtime flags:
  - `--network none` by default
  - course-specific resource limits
  - minimal mounts
- exit after each run

### 3. MCP Model

There should be one benchmark MCP implementation for benchmark runs.

Preferred location:

- keep the host/sandbox-oriented TypeScript MCP server in `agent/mcp-server.ts`

The MCP tool should not directly execute grading logic "inside itself" for a
special container path. Instead it should call an execution backend that knows
how to:

- run `test`
- run `submit`
- launch fresh course containers

## What Stays

- Host-mode runners and grading logic
- Course-specific grading images
- `agent/mcp-server.ts` as the primary MCP server
- `bin/test` and `bin/grade` as grading entrypoints
- host execution as the default path unless `--sandbox` is set

## What Goes Away

These were part of the abandoned combined-container path.

### Dead Architecture

- `run_agent_in_container()` in `bin/bench-cli`
- container-mode MCP config generation in `bin/bench-cli`
- the `BSCS_IN_CONTAINER` benchmark architecture
- combined "agent + course runtime" image design as the primary benchmark path
- `bin/build-image` in its current agent-specific form
- `docs/CONTAINER_ARCHITECTURE.md`
- "combined container mode" documentation in per-course `CLAUDE.md` files

### Code Removed or Being Removed

- `bin/bscs-mcp-container.py`
- `_container_mcp_server_config()` in `bin/bench-cli`
- `_write_container_mcp_config()` in `bin/bench-cli`
- `COURSE_CONTAINERS` agent-image assumptions in `bin/bench-cli`
- course Dockerfiles that install agent CLIs

## Answer: Do We Still Need The Little Python MCP?

No, not in the target design.

`bin/bscs-mcp-container.py` existed only to support the
same-container/combined-container path. Once benchmark MCP moved back to the
main TypeScript server path, the Python MCP became dead code.

The preferred end state is:

- one MCP server implementation
- one execution backend abstraction
- no container-specific MCP fork

## Recommended Image Model

Split images by responsibility:

### Agent Sandbox Image

Contains:

- the agent CLI
- benchmark MCP client/server dependencies
- minimal benchmark orchestration tooling

Does not contain:

- course-specific compilers/runtime payloads unless truly shared

### Course Test Images

Contain:

- course runtime and compiler toolchain
- tests and fixtures
- runner/framework dependencies

Do not contain:

- agent CLIs
- agent auth/config assumptions
- MCP server copies

## Migration Phases

### Phase 0: Stabilize Current Behavior

- Keep host execution as the stable default
- Stop expanding combined-container behavior
- Treat current combined-container code as transitional only

### Phase 1: Add Sandbox Backend

Add a new execution backend to `bench-cli`:

- `host`
- `sandbox`

Responsibilities of sandbox backend:

- launch Docker Sandbox
- use a tiny local template layer that adds Bun so `bin/bscs-mcp` works unchanged
- mount workspace
- inject agent auth/config
- run selected CLI inside sandbox

Do not reuse `run_agent_in_container()` for this.

### Phase 2: Add Test Executor Abstraction

Create a single execution layer for MCP `test` / `submit`.

Example shape:

- `execute_test(workspace, mode)`
- `execute_submit(workspace)`

Backends:

- host backend
- sandbox backend

The sandbox backend should launch a fresh course test container from the
sandbox's Docker daemon.

### Phase 3: Convert Course Images

Replace `Dockerfile.combined` files with grading-only images.

Preferred cleanup:

- rename to something like `Dockerfile.grading`
- remove agent CLI installation
- remove `bin/bscs-mcp-container.py` copies
- remove `BSCS_IN_CONTAINER=1` image contract

### Phase 4: Delete Combined-Container Code

After at least a few representative courses pass in sandbox mode:

- delete remaining container MCP helpers from `bin/bench-cli`
- delete any remaining agent-in-course-container code
- delete old combined-container docs

### Phase 5: Optional Default Flip

Only after sandbox mode is stable:

- make sandbox mode the default
- consider whether an explicit host flag is even needed

## Cleanup Principles

- No legacy flags for the abandoned combined-container design
- No second MCP server long-term
- No duplicate host/container orchestration logic
- No course image should know about agent auth
- No runner should need special benchmark-only `BSCS_IN_CONTAINER` behavior for
  the new architecture

## Practical First Implementation Slice

The smallest robust slice is:

1. Implement sandbox backend for one agent, likely Claude.
2. Keep the existing `bin/bscs-mcp` semantics, but run them from inside the sandbox.
3. Make MCP `test` launch one fresh course test container from the sandbox.
4. Validate on:
   - one Python course
   - one Java course
   - one C course
5. Then generalize to Gemini, Opencode, and Codex.

## Success Criteria

The migration is complete when all of the following are true:

- agent never runs directly on the host in sandbox mode
- tests do not run in the agent's live container
- tests use fresh course containers
- network isolation matches old grading expectations
- host fallback still works
- `bin/bscs-mcp-container.py` is deleted
- combined agent/course images are deleted or renamed into grading-only images

# Agent CLI Reference

bench-cli supports five agent backends. This document covers how each is invoked, their capabilities, and known differences.

## Quick Reference

```bash
bin/bench-cli --agent claude             # Default. Claude Code CLI
bin/bench-cli --agent codex              # OpenAI Codex CLI
bin/bench-cli --agent gemini             # Google Gemini CLI
bin/bench-cli --agent opencode           # Opencode CLI
bin/bench-cli --agent cursor             # Cursor CLI
```

## Feature Parity Matrix

| Feature | Claude Code | Codex CLI | Gemini CLI | Opencode CLI | Cursor CLI |
|---|---|---|---|---|---|
| **Command** | `claude` | `codex` | `gemini` | `opencode` | `agent` |
| **Install** | `npm i -g @anthropic-ai/claude-code` | `npm i -g @openai/codex` | `npm i -g @google/gemini-cli` | `npm i -g opencode-ai` | `curl https://cursor.com/install -fsS \| bash` |
| **Default model** | `opus` | `gpt-5.3-codex` | `flash` | `minimax/minimax-m2.5` | `claude-sonnet-4.5` |
| **Prompt delivery** | stdin via `-p` flag | stdin via `-` positional | `--prompt ""` + stdin | stdin (non-TTY, appended to positional) | stdin via `-p` flag |
| **Trace format** | `--output-format stream-json` | `--json` (exec mode) | `--output-format stream-json` | `--format json` | `--output-format stream-json` |
| **Auto-approve** | `--dangerously-skip-permissions` | `-a never` | `--yolo` | `opencode.json` permission + `OPENCODE_PERMISSION` env | `--force --approve-mcps --trust` |
| **Tool restriction** | `--allowedTools` (allowlist) | `--sandbox workspace-write` | None (see below) | None (permission-based deny only) | None (permissions config possible) |
| **MCP config** | `.mcp.json` (workspace-local) | `codex mcp add` (global `~/.codex/`) | `.gemini/settings.json` (workspace-local) | `opencode.json` (workspace-local) | `.cursor/mcp.json` (workspace-local) |
| **Project context** | `--add-dir <path>` | N/A | `--include-directories <path>` | Auto (reads from cwd) | Auto (reads from cwd) |
| **Cost in trace** | Yes (result event) | No | Depends on version | Yes (step_finish events) | No (dashboard only) |
| **Token counts** | Yes (result event) | Yes (turn.completed events) | Yes (result event stats) | Yes (step_finish events) | No (dashboard only) |
| **Session persistence** | `--no-session-persistence` for grading | N/A | N/A | N/A | N/A |

## Per-Agent Details

### Claude Code

**Phase 1 (agent run) command:**
```
claude -p --verbose \
  --output-format stream-json \
  --model <model> \
  --dangerously-skip-permissions \
  --add-dir <project-root> \
  --allowedTools Read Write Edit Glob Grep "Bash(git:*)" \
    mcp__bscs-bench__test mcp__bscs-bench__submit
```

**Phase 2/3 (LLM grading/review) command:**
```
claude -p --output-format text --no-session-persistence --model <model>
```

**MCP config:** Written as `.mcp.json` in workspace directory. Cleaned up in finally block.

**Trace parsing:** The final JSONL line with `{"type": "result"}` contains `total_cost_usd`, `usage.input_tokens`, `usage.output_tokens`, `duration_ms`, `num_turns`, `session_id`, `modelUsage`.

**Models:** `opus`, `sonnet`, `haiku` (short names passed directly to `--model`).

**Environment:** Removes `CLAUDECODE` env var to prevent nested-session errors.

### Codex CLI

**Phase 1 command:**
```
codex -a never exec --json --sandbox workspace-write -m <model> -
```

**Phase 2/3 command:**
```
codex -a never exec --sandbox read-only -o <output-file> -m <model> -
```

**MCP config:** Registered globally via `codex mcp add bscs-bench -- <bscs-mcp> --workspace <dir>`. Removed via `codex mcp remove bscs-bench`. Global config avoids per-project trust requirements.

**Trace parsing:** Events are different from Claude/Gemini:
- `session.start` — contains `session_id`
- `item.completed` where `item.type == "agent_message"` — counted as turns (one per API call)
- `turn.completed` — contains `usage.input_tokens` and `usage.output_tokens`
- Codex doesn't report cost in events (always 0)

**Models:** `gpt-5.3-codex`, `gpt-5.4`, `gpt-5-codex`, `gpt-4.1`, `gpt-4.1-mini`.

**Reasoning flag:** `--reasoning {minimal,low,medium,high,xhigh}` maps to `-c model_reasoning_effort="<level>"`. Only supported for Codex.

### Gemini CLI

**Phase 1 command:**
```
gemini --output-format stream-json --model <model> --yolo --prompt "" \
  --include-directories <project-root>
```
Prompt is still piped on stdin; the empty `--prompt` forces headless mode without putting the full benchmark prompt on argv.

**Phase 2/3 command:**
```
gemini --output-format text --yolo --model <model> --prompt ""
```

**MCP config:** Written as `.gemini/settings.json` in workspace directory:
```json
{
  "mcpServers": {
    "bscs-bench": {
      "command": "<project-root>/bin/bscs-mcp",
      "args": ["--workspace", "<workspace-dir>"]
    }
  }
}
```
Entire `.gemini/` directory is removed in finally block (Gemini CLI may create session data there).

**Trace parsing:** Stream-json events match Claude Code's format:
- `init` — `session_id`, `model`
- `message` — assistant messages counted as turns
- `result` — aggregated stats (tokens, cost)

The metrics parser tries multiple field names for cost: `stats.cost_usd`, `stats.totalCost`, `stats.total_cost`.

**Models:** `flash` (default), `pro`, `gemini-2.5-pro`, `gemini-2.5-flash`. Short names like `flash` and `pro` are passed directly to `--model` — Gemini CLI resolves them internally.

**Auth:** Requires `GEMINI_API_KEY` or `GOOGLE_API_KEY` environment variable, or Google Cloud auth (`GOOGLE_APPLICATION_CREDENTIALS`).

### Opencode CLI

**Phase 1 (agent run) command:**
```
opencode run --format json --model <model>
```
Prompt piped via stdin (appended to positional arg in non-TTY mode).
Permission auto-approved via `opencode.json` config + `OPENCODE_PERMISSION` env var.

**Phase 2/3 (LLM grading/review) command:**
```
opencode run --format default --model <model>
```

**MCP config:** Written as `opencode.json` in workspace directory:
```json
{
  "mcp": {
    "bscs-bench": {
      "type": "local",
      "command": ["<project-root>/bin/bscs-mcp", "--workspace", "<workspace-dir>"],
      "enabled": true
    }
  },
  "permission": "allow"
}
```
Note: Unlike Claude/Gemini MCP config, Opencode uses `command` as an array (not string + args) and requires a `type` field. The `permission` field is embedded alongside MCP config. Cleaned up in finally block.

**Trace parsing:** JSON events (JSONL, one per line):
- `step_start` -- counted as turns (one per agent step)
- `step_finish` -- contains `part.cost` (USD) and `part.tokens` with `input`, `output`, `reasoning`, `cache.read`, `cache.write`
- `tool_use` -- tool call completions
- `text` -- assistant text output
- `error` -- session errors

All events include `type`, `timestamp`, and `sessionID`.

**Models:** Uses `provider/model` format. Default: `minimax/minimax-m2.5`. Any model available through configured providers works (e.g., `openrouter/anthropic/claude-sonnet-4-5`, `anthropic/claude-opus-4`).

**Auth:** Requires provider authentication via `opencode auth login` (stored in `~/.local/share/opencode/auth.json`). For OpenRouter, authenticate once and all OpenRouter models are available. Alternative: `OPENCODE_CONFIG_CONTENT` env var for inline config.

**Permission system:** Opencode uses a config-based permission system (not CLI flags):
- `OPENCODE_PERMISSION` env var: JSON value, e.g., `'"allow"'` for full auto-approve
- `opencode.json` config: `"permission": "allow"` (embedded with MCP config)
- The `run` command also auto-rejects any permission requests in headless mode

### Cursor CLI

**Phase 1 (agent run) command:**
```
agent -p \
  --output-format stream-json \
  --model <model> \
  --force \
  --approve-mcps \
  --trust
```
Prompt is piped via stdin using `-p` flag (same semantics as Claude Code). `--force` auto-approves file changes, `--approve-mcps` auto-approves MCP servers, `--trust` skips workspace confirmation in headless mode.

**Phase 2/3 (LLM grading/review) command:**
```
agent -p --output-format text --force --trust --model <model>
```

**MCP config:** Written as `.cursor/mcp.json` in workspace directory:
```json
{
  "mcpServers": {
    "bscs-bench": {
      "command": "<project-root>/bin/bscs-mcp",
      "args": ["--workspace", "<workspace-dir>"]
    }
  }
}
```
Uses the same `mcpServers` schema as Claude Code's `.mcp.json`. Entire `.cursor/` directory is removed in finally block (Cursor CLI may create session data there).

**Trace parsing:** Stream-json events (NDJSON), similar to Claude/Gemini:
- `system` / `init` / `system/init` — session metadata (`session_id`, `model`)
- `assistant` / `message` — assistant messages counted as turns
- `tool_use` — tool call events (started/completed subtypes)
- `result` — final outcome with `duration_ms`, `duration_api_ms`, `session_id`

The metrics parser tries multiple field names for cost: `stats.cost_usd`, `stats.totalCost`, `stats.total_cost`. Event type names are handled defensively since the exact stream-json schema may vary across Cursor CLI versions.

**Models:** `claude-sonnet-4.5` (default), `gpt-5.2`, `claude-sonnet-4.5-thinking`. Model names follow Cursor's routing convention — passed directly to `--model`. List available models with `agent --list-models`.

**Auth:** Requires `CURSOR_API_KEY` environment variable, or `agent login` for persistent auth. Also accepts `--api-key <key>` flag.

## Known Limitations

### Gemini: No Tool Restriction

Claude Code uses `--allowedTools` to restrict the agent to specific tools (file ops + git + MCP). Codex uses `--sandbox workspace-write` to restrict file writes.

Gemini CLI has no equivalent. `--yolo` auto-approves all tool calls, and `--allowed-tools` is an auto-approve bypass list (not a restriction). The agent can run arbitrary shell commands.

**Impact:** Gemini agents may use shell commands to run tests directly instead of using the MCP `test` tool. This bypasses attempt tracking and submission enforcement. The prompt instructs the agent to use MCP tools, but there's no enforcement.

### Gemini: Token/Cost Accuracy

The `result` event's stats schema is not fully documented. The metrics parser uses fallback field names. Actual token counts and cost may be zero if the Gemini CLI version doesn't populate these fields.

### Gemini: No `--reasoning` Flag

The `--reasoning` flag is Codex-only. Gemini uses thinking budget configuration via `settings.json` generation config (`thinkingBudget` in `generateContentConfig`), which is not exposed as a CLI flag. bench-cli does not currently support this.

### Opencode: No Tool Restriction

Like Gemini CLI, Opencode has no tool allowlist mechanism. Claude Code uses `--allowedTools` and Codex uses `--sandbox`. Opencode's permission system can `deny` specific tools but cannot restrict to an allowlist. The agent may use shell commands instead of MCP tools. The prompt instructs the agent to use MCP tools, but there's no enforcement.

### Opencode: Model Format Requires Provider Prefix

Opencode models use `provider/model` format (e.g., `openrouter/anthropic/claude-sonnet-4-5`). This differs from Claude Code (short names like `opus`) and Gemini (short names like `flash`). The provider must be configured via `opencode auth login` before use.

### Opencode: No `--reasoning` Flag

Like Gemini, Opencode doesn't have a `--reasoning` flag. Opencode supports model `--variant` (provider-specific reasoning effort like `high`, `max`) but bench-cli does not currently expose this.

### Opencode: Server Startup Overhead

`opencode run` starts an internal server for each invocation (bootstraps config, initializes session, processes prompt, then exits). This adds startup overhead compared to lighter CLIs. Not significant for benchmark runs but noticeable for quick Phase 2/3 grading tasks.

### Opencode: Auth Persistence Required

Unlike Claude Code (uses `ANTHROPIC_API_KEY`) or Gemini (`GEMINI_API_KEY`), Opencode stores credentials in `~/.local/share/opencode/auth.json` via the `opencode auth login` flow. For CI/automation, use `OPENCODE_CONFIG_CONTENT` env var to inject provider config inline.

### Cursor: Binary Name Conflict

The Cursor CLI binary is named `agent`, which is generic and could shadow other tools in `PATH`. Ensure `agent` resolves to Cursor's CLI (verify with `agent --version`). If needed, install to a specific path or create a symlink/alias.

### Cursor: No Tool Restriction

Like Gemini and Opencode, Cursor CLI has no tool allowlist mechanism. `--force` auto-approves all tool calls. The agent can run arbitrary shell commands. Cursor has `permissions.allow`/`permissions.deny` in its config, but these control command/path patterns, not tool-level restrictions. The prompt instructs the agent to use MCP tools, but there's no enforcement.

### Cursor: No Token/Cost Reporting

Cursor CLI does not include token counts or cost in any output format (`text`, `json`, or `stream-json`). The `result` event only contains `duration_ms`, `duration_api_ms`, `session_id`, and `is_error`. Token usage is tracked server-side and visible on the Cursor web dashboard, but not exposed through the CLI. Per-workspace token breakdowns are not possible.

### Cursor: No `--reasoning` Flag

Like Gemini and Opencode, Cursor doesn't expose a reasoning effort flag. The `--reasoning` flag is Codex-only.

### Codex: No Cost Reporting

Codex CLI doesn't report cost in its JSON events. `cost_usd` is always 0 in results.

### Codex: Turn Counting

Codex wraps the entire session in one "turn" (`turn.completed` count is always 1). bench-cli counts `item.completed` events with `item.type == "agent_message"` instead, giving a per-API-call turn count comparable to Claude Code.

## Opencode-Specific Features (Not Yet Integrated)

Opencode CLI has several capabilities beyond what bench-cli currently uses:

### Server Mode + SDK
`opencode serve` starts a persistent HTTP server with full REST API. The `@opencode-ai/sdk` npm package provides programmatic control. This could replace subprocess-based integration for better session management and structured output.

### Structured JSON Output
The SDK supports schema-validated JSON responses via `format: { type: "json_schema", schema: {...} }`. This could improve LLM grading reliability by enforcing output structure.

### Custom Tools (TypeScript)
Tools can be defined as TypeScript files in `.opencode/tools/`. These could replace MCP for test/submit functionality with potentially lower overhead.

### Model Variants
Opencode supports `--variant` for provider-specific reasoning effort (e.g., Anthropic "high"/"max", OpenAI reasoning levels). bench-cli could expose this similar to Codex's `--reasoning` flag.

### Agent Skills
Reusable instruction sets in `.opencode/skills/<name>/SKILL.md`. Could be used to package benchmark-specific instructions.

### 30+ Native Providers
Opencode natively supports OpenRouter, Anthropic, OpenAI, Google, AWS Bedrock, Azure, Groq, Together, local models (Ollama), and many more -- without proxy configuration.

### AGENTS.md Rules
Opencode reads `AGENTS.md` (falling back to `CLAUDE.md`) for project-level instructions. Compatible with existing CLAUDE.md files.

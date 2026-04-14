export const DEFAULT_MAX_STEPS = 250;
export const MAX_TOOL_OUTPUT_LENGTH = 2000;

export type WorkspaceStatus = "idle" | "running" | "completed" | "failed";

export interface TokenUsage {
  input: number;
  output: number;
  total: number;
  cost: number;
}

export interface WorkspaceState {
  workspaceId: string;
  status: WorkspaceStatus;
  model?: string;
  startedAt?: string;
  steps: number;
  tokens: TokenUsage;
  gradeResult?: { passed: number; total: number; percentage: number };
  cached?: boolean;
}

export interface TestResult {
  name: string;
  passed: boolean;
  points: number;
  pointsMax: number;
  input?: string;
  expected?: string;
  actual?: string;
  error?: string;
}

export type SSEEvent =
  | { type: "text"; text: string }
  | { type: "tool_start"; tool: string; args: Record<string, unknown> }
  | { type: "tool_end"; tool: string; result: string }
  | { type: "test"; tests: TestResult[]; summary: { passed: number; total: number; percentage: number }; compilationError?: string }
  | { type: "malloc_perf"; performanceIndex: number; utilizationScore: number; throughputScore: number }
  | { type: "submit"; codeResults?: { passed: number; total: number; percentage: number }; writtenResults?: { pointsEarned: number; pointsMax: number; percentage: number }; llmGraded: boolean }
  | { type: "file_change"; path: string; action: "write" | "edit" }
  | { type: "step"; step: number; tools: string[] }
  | { type: "tokens"; inputTokens: number; outputTokens: number; totalTokens: number; cost: number }
  | { type: "done"; success: boolean; steps: number; gradeResult?: { passed: number; total: number; percentage: number } }
  | { type: "error"; error: string };

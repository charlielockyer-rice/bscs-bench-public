import type { ModelMessage } from "ai"

import type { GradeResult } from "./gradeParser"
import type { SSEEvent } from "../shared/benchContracts"

export type EventCallback = (event: SSEEvent) => void

export interface EventEmitter {
  emit(event: SSEEvent): void
  subscribe(callback: EventCallback): () => void
}

export interface WorkspaceState {
  status: "idle" | "running" | "completed" | "failed"
  controller?: AbortController
  emitter?: EventEmitter
  events: SSEEvent[]
  model?: string
  startedAt?: Date
  steps: number
  totalInputTokens: number
  totalOutputTokens: number
  totalCost: number
  lastGradeResult?: GradeResult
}

export interface PersistedMessages {
  workspaceId: string
  model?: string
  timestamp: string
  messageCount: number
  truncated: boolean
  messages: ModelMessage[]
}

export type WorkspaceValidationResult =
  | { valid: true; workspaceDir: string }
  | { valid: false; error: string }

export interface ModuleConfig {
  module_number: number
  module_name: string
  max_attempts: number
  total_points: number
  total_tests: number
  functions: string[]
  supports_performance: boolean
}

export interface Attempt {
  attempt_number: number
  timestamp: string
  code_hash: string
  tests_passed: number
  tests_total: number
  points_earned: number
  points_possible: number
  pass_rate: number
  score_percentage: number
  errors: string[]
}

export interface AttemptsData {
  module_number: number
  attempts: Attempt[]
}

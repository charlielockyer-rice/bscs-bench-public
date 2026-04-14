import * as path from "path"
import { executeTest, spawnAndCollect } from "./gradeExecutor"

export type TestRunType = "test" | "submit"
export type TestExecutionMode = "host" | "sandbox"

export interface TestExecutionBackend {
  readonly mode: TestExecutionMode
  runTest(workspaceRoot: string, runType: TestRunType): Promise<string>
  runMallocPerf(workspaceRoot: string): Promise<string>
}

class HostTestExecutionBackend implements TestExecutionBackend {
  readonly mode = "host" as const

  constructor(private readonly projectRoot: string) {}

  runTest(workspaceRoot: string, runType: TestRunType): Promise<string> {
    return executeTest(workspaceRoot, this.projectRoot, runType)
  }

  runMallocPerf(workspaceRoot: string): Promise<string> {
    const runnerScript = path.resolve(this.projectRoot, "comp321", "runner.py")
    return spawnAndCollect("python3", [runnerScript, "malloc", workspaceRoot, "--perf"], this.projectRoot, 180_000)
  }
}

class SandboxTestExecutionBackend implements TestExecutionBackend {
  readonly mode = "sandbox" as const

  constructor(private readonly _projectRoot: string) {}

  runTest(_workspaceRoot: string, _runType: TestRunType): Promise<string> {
    return Promise.reject(new Error(
      "Sandbox test execution backend is not implemented yet. " +
      "See docs/DOCKER_SANDBOX_MIGRATION.md."
    ))
  }

  runMallocPerf(_workspaceRoot: string): Promise<string> {
    return Promise.reject(new Error(
      "Sandbox malloc performance execution is not implemented yet. " +
      "See docs/DOCKER_SANDBOX_MIGRATION.md."
    ))
  }
}

export function createTestExecutionBackend(
  projectRoot: string,
  mode: TestExecutionMode = "host",
): TestExecutionBackend {
  if (mode === "sandbox") {
    return new SandboxTestExecutionBackend(projectRoot)
  }
  return new HostTestExecutionBackend(projectRoot)
}

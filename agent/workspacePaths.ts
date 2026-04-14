/**
 * Workspace path resolution helpers.
 *
 * Supports both flat (workspaces/id) and model-directory
 * (workspaces/model-dir/id) workspace layouts.
 */

import * as fs from "fs"
import * as path from "path"

/**
 * Resolve a workspace directory given a workspaces root and a workspace ID.
 *
 * Search order:
 * 1. Flat: workspacesDir/workspaceId
 * 2. Explicit nested: if workspaceId contains "/", try workspacesDir/workspaceId
 * 3. Search all model dirs: workspacesDir/star/workspaceId
 *
 * Returns the resolved absolute path to the workspace directory.
 * Throws if no matching workspace is found.
 */
export function resolveWorkspaceDir(workspacesDir: string, workspaceId: string): string {
  // 1. Check flat: workspaces/id
  const flat = path.join(workspacesDir, workspaceId)
  if (fs.existsSync(flat) && isDirSync(flat)) return flat

  // 2. Check if id contains slash: workspaces/model/name
  if (workspaceId.includes("/")) {
    const nested = path.join(workspacesDir, workspaceId)
    if (fs.existsSync(nested) && isDirSync(nested)) return nested
  }

  // 3. Search all model dirs: workspaces/*/id
  if (fs.existsSync(workspacesDir)) {
    for (const entry of fs.readdirSync(workspacesDir, { withFileTypes: true })) {
      if (!entry.isDirectory()) continue
      const candidate = path.join(workspacesDir, entry.name, workspaceId)
      if (fs.existsSync(candidate) && isDirSync(candidate)) return candidate
    }
  }

  throw new Error("Workspace not found: " + workspaceId)
}

/**
 * Try to resolve a workspace directory, returning null instead of throwing.
 */
export function tryResolveWorkspaceDir(workspacesDir: string, workspaceId: string): string | null {
  try {
    return resolveWorkspaceDir(workspacesDir, workspaceId)
  } catch {
    return null
  }
}

/**
 * Iterate all workspace directories under the workspaces root.
 * Supports both flat and model-directory layouts.
 *
 * Yields { id, dir } for each valid workspace found.
 * - Flat workspace workspaces/foo yields { id: "foo", dir: ".../workspaces/foo" }
 * - Nested workspace workspaces/model/foo yields { id: "foo", dir: ".../workspaces/model/foo", modelDir: "model" }
 */
export function* iterateWorkspaces(
  workspacesDir: string,
  isValidWorkspace?: (dir: string) => boolean
): Generator<{ id: string; dir: string; modelDir?: string }> {
  if (!fs.existsSync(workspacesDir)) return

  for (const entry of fs.readdirSync(workspacesDir, { withFileTypes: true })) {
    if (!entry.isDirectory()) continue

    const entryDir = path.join(workspacesDir, entry.name)

    // Check if this is a direct workspace (has workspace.yaml/json or passes validation)
    if (isWorkspaceDir(entryDir, isValidWorkspace)) {
      yield { id: entry.name, dir: entryDir }
      continue
    }

    // Otherwise, treat as a model directory and scan its children
    for (const child of fs.readdirSync(entryDir, { withFileTypes: true })) {
      if (!child.isDirectory()) continue
      const childDir = path.join(entryDir, child.name)
      if (isWorkspaceDir(childDir, isValidWorkspace)) {
        yield { id: child.name, dir: childDir, modelDir: entry.name }
      }
    }
  }
}

/**
 * Derive the project root from a workspace path.
 *
 * Walks up the directory tree looking for marker files (package.json + bin/).
 * This works for both flat workspaces (root/workspaces/name) and
 * model-directory workspaces (root/workspaces/model/name).
 */
export function deriveProjectRoot(workspaceDir: string): string {
  let dir = path.resolve(workspaceDir)
  const fsRoot = path.parse(dir).root

  while (dir !== fsRoot) {
    // Check for project root markers
    if (
      fs.existsSync(path.join(dir, "package.json")) &&
      fs.existsSync(path.join(dir, "bin"))
    ) {
      return dir
    }
    dir = path.dirname(dir)
  }

  throw new Error(
    "Cannot find project root from workspace: " + workspaceDir +
    ". Expected to find package.json + bin/ in an ancestor directory."
  )
}

// -- Internal helpers ---------------------------------------------------------

function isDirSync(p: string): boolean {
  try {
    return fs.statSync(p).isDirectory()
  } catch {
    return false
  }
}

function isWorkspaceDir(dir: string, isValid?: (dir: string) => boolean): boolean {
  if (isValid) return isValid(dir)
  // Default: look for workspace.yaml or workspace.json
  return (
    fs.existsSync(path.join(dir, "workspace.yaml")) ||
    fs.existsSync(path.join(dir, "workspace.json"))
  )
}

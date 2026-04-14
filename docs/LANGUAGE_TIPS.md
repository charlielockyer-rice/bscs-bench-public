# Language & Domain Tips Archive

Reference material for agent benchmarking prompts. These tips contain domain-specific
guidance that may be useful for prompt engineering but are not included in every solver
prompt by default. They were originally embedded in `agent/prompts.ts`.

---

## HJlib Parallel Constructs (Java)

*Source: `HJLIB_CONTEXT` in `agent/prompts.ts`*

### Construct Reference

- `async` - Launch an asynchronous task
- `finish` - Wait for all enclosed async tasks to complete
- `future` - Create a task that returns a value
- `forall` / `forasync` - Parallel loop constructs
- `phaser` - Synchronization barrier for iterative algorithms
- `isolated` - Mutual exclusion for critical sections

### Key HJlib Imports

```java
import static edu.rice.hj.Module1.*;  // async, finish, forasync
import edu.rice.hj.api.*;              // HjFuture, HjPhaser
```

### Key Patterns

- Wrap parallel regions in `finish { ... }`
- Use `async { ... }` inside finish blocks for parallel tasks
- For divide-and-conquer: recursive async with base case threshold
- For barriers: use phasers with signal/wait or next()

---

## Malloc Optimization Strategies (C)

*Source: `MALLOC_CONTEXT` in `agent/prompts.ts`*

### Performance Index Formula

The benchmark scores your implementation on a PERFORMANCE INDEX (0-100):
- 40 points: Memory UTILIZATION (how efficiently you use heap space)
- 60 points: THROUGHPUT (operations per second, capped at libc performance)

```
Performance index = (util_score/40)*40 + min(throughput/libc_throughput, 1)*60
```

A basic implicit free list with first-fit typically scores ~40-50/100.
Target score: 90/100 or higher requires sophisticated techniques.

### Optimization Strategies (in order of impact)

1. **SEGREGATED FREE LISTS** (biggest improvement)
   - Maintain separate free lists for different size classes
   - Size classes: {16-32}, {33-64}, {65-128}, ..., {4097+}
   - O(1) insertion, faster search than single list

2. **BETTER FIT POLICIES**
   - Best-fit: Find smallest block that fits (better utilization)
   - Better-fit: Search limited number then take best (balance)
   - Avoid first-fit (poor utilization)

3. **IMMEDIATE COALESCING**
   - Merge adjacent free blocks on free()
   - Use boundary tags (footer) for O(1) coalescing

4. **SPLITTING POLICY**
   - Only split if remainder >= minimum block size
   - Avoid creating tiny unusable fragments

5. **REALLOC OPTIMIZATION**
   - Check if next block is free and large enough
   - Expand in-place instead of malloc+copy+free

6. **FOOTER OPTIMIZATION** (advanced)
   - Only free blocks need footers
   - Use allocation bit to skip footer for allocated blocks

### Block Structure (suggested)

```c
// Allocated block: [header | payload...]
// Free block:      [header | next | prev | ... | footer]
// Header/Footer:   [size (aligned) | alloc bit]
```

---

## TypeScript Web App Organization

*Source: `TYPESCRIPT_CONTEXT` in `agent/prompts.ts`*

### CRITICAL: Code Organization

This is a large project. DO NOT put all code in src/main.ts. You MUST organize code into multiple files.

### Recommended File Structure

- `src/main.ts` -- Thin entry point (~50 lines). Import and register web components, initialize the app.
- `src/types.ts` -- TypeScript interfaces and type definitions
- `src/api.ts` -- All database/API interaction logic
- `src/state.ts` -- Shared application state management
- `src/components/` -- One file per web component:
  - `src/components/login-view.ts`
  - `src/components/workspace-panel.ts`
  - `src/components/channel-list.ts`
  - `src/components/post-view.ts`
  - etc.

### Rules

- Each web component should be its own class in its own file
- Use Shadow DOM (mode: "open") for encapsulation
- Keep individual files under 200 lines
- Keep src/main.ts as a small entrypoint; if it grows large, split logic into modules/components
- Export/import between modules using ES module syntax
- Use `customElements.define()` to register each component

You can create new .ts files anywhere under src/. You are encouraged to do so.

### Build System

- Build: `npm run build` (uses Parcel bundler -- resolves imports automatically)
- Start: `npm start` (dev server with hot reload)
- The bundler handles all imports, so freely split code across files

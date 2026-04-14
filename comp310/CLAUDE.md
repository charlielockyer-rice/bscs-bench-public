# COMP 310: Advanced Object-Oriented Programming

## Course Overview

Java-based advanced OOP course covering design patterns: Union, Abstract, Strategy, Command/Visitor, Interaction, and Extended Visitor. Progressive Ballworld project chain builds complexity across 6 homework assignments.

**All tests run in Docker** - no local Java installation required.

## Directory Structure

```
comp310/
├── CLAUDE.md                    # This file
├── Dockerfile                   # Docker image (Java 17 + JUnit 5 + provided.jar)
├── docker-compose.yml           # Docker compose config
├── .dockerignore                # Excludes coursework/libraries from Docker context
├── build.sh                     # Compile + test + JAR script (runs in container)
├── runner.py                    # Test runner (Docker-based)
├── grading_prompt.md            # LLM grading guidance (ChatApp design)
├── lib/
│   └── provided/                # Pre-compiled library sources (provided.jar)
│       ├── utils/               # General utilities (dispatcher, loader, etc.)
│       ├── logger/              # Logging framework
│       ├── listFW/              # Functional list visitor framework
│       ├── basicVisitorFW/      # Basic visitor pattern framework
│       ├── extvisitor/          # Extended visitor pattern
│       └── ballworld/           # Ballworld extended visitors
├── configs/                     # Assignment YAML configs
│   ├── hw1.yaml                 # Shapes & GUI
│   ├── hw2.yaml                 # Basic Ballworld
│   ├── hw3.yaml                 # Strategy Ballworld
│   ├── hw4.yaml                 # Paint & Visitor Ballworld
│   ├── hw5.yaml                 # Interaction Ballworld
│   ├── hw6.yaml                 # Extended Visitor Ballworld
│   └── chatapp_design.yaml      # ChatApp API Design (LLM-graded)
├── tests/                       # JUnit 5 test suites
│   ├── hw01/ShapeTest.java
│   ├── hw02/BallTest.java
│   ├── hw03/StrategyTest.java
│   ├── hw04/PaintStrategyTest.java
│   ├── hw05/InteractTest.java
│   └── hw06/ListVisitorTest.java, ExtVisitorTest.java
├── rubrics/
│   └── chatapp_design_rubric.md
├── hw01/                        # HW1: Shapes & GUI (starter template)
├── hw02/                        # HW2: Basic Ballworld
├── hw03/                        # HW3: Strategy Ballworld
├── hw04/                        # HW4: Paint & Visitor Ballworld
├── hw05/                        # HW5: Interaction Ballworld
├── hw06/                        # HW6: Extended Visitor Ballworld
├── chatapp_design/              # ChatApp API Design (LLM-graded)
├── coursework/                  # Raw student solutions (reference only)
└── libraries/                   # Raw library repos (reference only)
```

## Quick Start

```bash
# Run tests (Docker image builds automatically on first run)
python3 comp310/runner.py hw01 comp310/hw01

# Verbose output
python3 comp310/runner.py hw03 comp310/hw03 --verbose

# JSON output
python3 comp310/runner.py hw03 comp310/hw03 --json

# Force rebuild Docker image
python3 comp310/runner.py hw01 comp310/hw01 --rebuild
```

## Homework Summary

| HW | Topic | Design Pattern | Key Student Work | Tests |
|----|-------|---------------|------------------|-------|
| 1 | Shapes & GUI | Union | Shape hierarchy, paint() methods | ~10 |
| 2 | Basic Ballworld | Abstract | ABall physics, concrete ball types | ~15 |
| 3 | Strategy Ballworld | Strategy | 8 strategies + SwitcherStrategy | ~20 |
| 4 | Paint & Visitor | Command/Visitor | Paint strategies, update strategies, IBallCmd | ~15 |
| 5 | Interaction | Interaction | Interact strategies, criteria strategies | ~15 |
| 6 | Extended Visitor | Extended Visitor | Typed hosts, config algos, list visitors | ~20 |
| 7 | ChatApp Design | Multiple | API interface design (LLM-graded) | LLM |

**Total: ~95 code tests + 1 LLM-graded**

## Sequential Dependencies (Ballworld Chain)

```
HW01 (standalone) ─── Shapes & GUI
HW02 (chain start) ── Basic Ballworld
  └─ HW03 ─────────── Strategy Ballworld (depends_on: comp310_hw2)
       └─ HW04 ────── Paint & Visitor (depends_on: comp310_hw3)
            └─ HW05 ─ Interaction (depends_on: comp310_hw4)
                 └─ HW06 Extended Visitor (depends_on: comp310_hw5)
HW07 (standalone) ─── ChatApp API Design
```

Each HW restructures the architecture significantly. The agent gets `previous_solution/` as reference via the `depends_on` mechanism.

## Docker Architecture

The runner uses Docker with:
- **Base image**: eclipse-temurin:17-jdk-jammy
- **Memory limit**: 4GB
- **CPU limit**: 2 cores
- **Network**: Disabled (no network needed)
- **Build system**: javac + JUnit 5 standalone (no Maven/Gradle)
- **Pre-compiled**: `provided.jar` contains all library classes

### Build Flow (inside container)

1. Compile student `src/*.java` against `provided.jar`
2. Compile test sources against student code + provided + JUnit
3. Run JUnit 5 via standalone console launcher
4. Attempt fat JAR (tracked metric, non-fatal)

## Key Libraries (provided.jar)

| Package | Library | Used By |
|---------|---------|---------|
| `provided.utils.*` | General-Utils | HW02-06 |
| `provided.logger.*` | Logger | HW04-06 |
| `provided.listFW.*` | List Visitor Framework | HW06 |
| `provided.basicVisitorFW.*` | Basic Visitor Framework | HW06 |
| `provided.extvisitor.*` | Extended Visitor | HW06 |
| `provided.ballworld.extVisitors.*` | Ballworld Extended Visitors | HW06 |

## Solution Files by HW

### HW01 (Shapes)
- `src/hw01/shape/AShape.java` - Abstract base (position)
- `src/hw01/shape/ASimpleShape.java` - Abstract simple (has color)
- `src/hw01/shape/Rectangle.java` - Concrete rectangle
- `src/hw01/shape/Ellipse.java` - Concrete ellipse
- `src/hw01/shape/Circle.java` - Circle extends Ellipse
- `src/hw01/shape/CompositeShape.java` - Composite (delegates to children)

### HW02 (Basic Ballworld)
- `src/hw02/ball/ABall.java` - Abstract ball (move, bounce, paint)
- `src/hw02/ball/BounceUpBall.java` - Diameter varies with x position
- `src/hw02/ball/DVDLogoBall.java` - Color change on bounce
- `src/hw02/ball/SineSpeedBall.java` - Oscillating velocity
- `src/hw02/ball/BallModel.java` - Model with dispatcher + reflection loading

### HW03 (Strategy Ballworld)
- `src/hw03/model/strategy/StraightStrategy.java` - No-op (empty)
- `src/hw03/model/strategy/GravityStrategy.java` - +1 to y-velocity
- `src/hw03/model/strategy/ColorStrategy.java` - Random color
- `src/hw03/model/strategy/ReverseStrategy.java` - Flip velocity every 10 ticks
- `src/hw03/model/strategy/ExpandingStrategy.java` - Radius oscillates 5-60
- `src/hw03/model/strategy/TeleportingStrategy.java` - Random location every 20 ticks
- `src/hw03/model/strategy/RandomWalkStrategy.java` - Velocity ±1 each tick
- `src/hw03/model/strategy/ExplodingStrategy.java` - Split on bounce
- `src/hw03/model/strategy/SwitcherStrategy.java` - Runtime strategy switching

### HW04-06
See instructions.md in each HW directory for details.

## Config File Format

```yaml
assignment_number: 3
assignment_name: "hw03"
display_name: "Strategy Ballworld"
language: java
runtime:
  version: "17"
depends_on: "comp310_hw2"     # Sequential dependency
grading:
  total_points: 100
  timeout_seconds: 60
  max_attempts: 10
source_dir: "hw03"
source_files:
  - path: "src/hw03/model/strategy/GravityStrategy.java"
tests:
  classes:
    - "comp310.tests.hw03.StrategyTest"
```

## Workspace Integration

```bash
# Create all COMP 310 workspaces
bin/setup_course ./comp310

# Create single workspace
bin/setup_workspace ./comp310 3

# Grade a workspace
bin/grade ./workspaces/comp310_hw03 -v
```

## Troubleshooting

### Docker image won't build
```bash
python3 comp310/runner.py hw01 <workspace> --rebuild
# Or manually:
docker build -t comp310-runner comp310/
```

### Compilation errors
- Ensure `module-info.java` is NOT in student source (build.sh excludes it)
- All library classes are in `provided.jar` on the classpath
- Student code compiles against Java 17

### Test reports missing
- Check `workspace/test-reports/` for JUnit XML files
- If directory is empty, compilation likely failed - check stderr output

## Gotchas & Learnings

- **[2026-03] bench-cli sandboxing is opt-in**: `bin/bench-cli` runs on the host by default. Use `--sandbox` if you want the agent inside Docker Sandbox. Direct `runner.py` calls still work unchanged.

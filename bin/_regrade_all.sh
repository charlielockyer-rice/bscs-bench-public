#!/bin/bash
# Sequential re-grading of all 9 models with latest test suites
# Run in tmux: tmux new-session -d -s regrade './bin/_regrade_all.sh; echo Done; read'

set -e
cd "$(dirname "$0")/.."

MODELS=(
  claude-haiku-4-5
  claude-opus-4-6
  claude-sonnet-4-0
  claude-sonnet-4-6
  composer-2
  gemini-3-flash-preview
  gpt-5-4
  minimax-minimax-m2-5
  minimax-minimax-m2-7
)

echo "========================================="
echo "Re-grading all 9 models sequentially"
echo "Started: $(date)"
echo "========================================="

for model in "${MODELS[@]}"; do
  echo ""
  echo "========================================="
  echo "GRADING: $model"
  echo "Started: $(date)"
  echo "========================================="
  bin/bench-cli --grade-only --model-dir "$model" 2>&1
  echo "Finished $model: $(date)"
done

echo ""
echo "========================================="
echo "Phase 1 (re-grading) complete: $(date)"
echo "========================================="

echo ""
echo "Starting Phase 2: LLM grading..."
for model in "${MODELS[@]}"; do
  echo ""
  echo "========================================="
  echo "LLM GRADING: $model"
  echo "Started: $(date)"
  echo "========================================="
  bin/bench-cli --written-grade-only --model-dir "$model" 2>&1
  echo "Finished $model: $(date)"
done

echo ""
echo "========================================="
echo "Phase 2 (LLM grading) complete: $(date)"
echo "========================================="

echo ""
echo "Starting Phase 3: Code review..."
for model in "${MODELS[@]}"; do
  echo ""
  echo "========================================="
  echo "CODE REVIEW: $model"
  echo "Started: $(date)"
  echo "========================================="
  bin/bench-cli --code-review-only --model-dir "$model" 2>&1
  echo "Finished $model: $(date)"
done

echo ""
echo "========================================="
echo "ALL 3 PHASES COMPLETE: $(date)"
echo "========================================="

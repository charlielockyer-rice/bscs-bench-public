# Run Status & TODO

Last updated: 2026-03-24

## Currently Running (as of 13:30)

| Session | Task |
|---------|------|
| `sonnet-4-0-comp310-run-130739` | Sonnet 4.0 solving comp310 hw1-7 |
| `sonnet-4-0-comp421-fix` | Sonnet 4.0 comp421 midterm → lab3 |
| `gemini-3-pro-comp310-run-130309` | Gemini 3.0 Pro solving comp310 hw1-7 |
| `gemini-flash-gaps` | Gemini 3 Flash comp382_hw1 → comp310 hw1-6 |
| `gpt-5-4-all-grade+review-130438` | GPT 5.4 full grading + code review |
| `opus-4-6-all-grade+review-131954` | Opus 4.6 full grading + code review |

## Publish-Ready (all phases complete)

- **composer-2** — 66/66 agent, grading + review done

## Will Be Ready After Current Runs

- **gpt-5-4** — grading + review running now
- **claude-opus-4-6** — grading + review running now

## TODO: Agent Work Needed

### claude-sonnet-4-0 (running now, 2 left after comp310)
After comp310 + comp421 runs finish, kick off grading + review:
```bash
bin/bench-cli --model-dir claude-sonnet-4-0 --grade-only --with-grading --code-review --tmux
```

### minimax-m2-5 — comp310 hw1-6
```bash
bin/bench-cli --model-dir minimax-minimax-m2-5 --agent opencode --model minimax/minimax-m2.5 --tmux comp310
```
Then grading + review:
```bash
bin/bench-cli --model-dir minimax-minimax-m2-5 --grade-only --with-grading --code-review --tmux
```

### minimax-m2-7 — comp310 hw1-6
```bash
bin/bench-cli --model-dir minimax-minimax-m2-7 --agent opencode --model minimax/minimax-m2.7 --tmux comp310
```
Then grading + review:
```bash
bin/bench-cli --model-dir minimax-minimax-m2-7 --grade-only --with-grading --code-review --tmux
```

### claude-sonnet-4-6 — needs grading + review (60/66 agent done, 6 empty comp310 hw1-6)
Agent work:
```bash
bin/bench-cli --model-dir claude-sonnet-4-6 --model sonnet --tmux comp310
```
Then grading + review:
```bash
bin/bench-cli --model-dir claude-sonnet-4-6 --grade-only --with-grading --code-review --tmux
```

### claude-haiku-4-5 — 16 empty
Missing: comp310 hw1-6, comp411 assign2-6, comp421 (all 5)
```bash
bin/bench-cli --model-dir claude-haiku-4-5 --model haiku --tmux comp310
bin/bench-cli --model-dir claude-haiku-4-5 --model haiku --tmux comp411
bin/bench-cli --model-dir claude-haiku-4-5 --model haiku --tmux comp421
```
Then grading + review:
```bash
bin/bench-cli --model-dir claude-haiku-4-5 --grade-only --with-grading --code-review --tmux
```

### gemini-3-flash-preview (running comp382_hw1 + comp310 now, 1 left after)
Remaining after current run: comp421_lab2
```bash
bin/bench-cli --model-dir gemini-3-flash-preview --agent gemini --model gemini-3-flash-preview comp421_lab2
```
Then grading + review:
```bash
bin/bench-cli --model-dir gemini-3-flash-preview --grade-only --with-grading --code-review --tmux
```

### gemini-3-pro-preview — 28 empty after comp310 finishes
Missing: comp215 hw3-6, comp322 hw1-4, comp341 hw1-7, comp382 hw3-6, comp411 assign4-6, comp421 all 5
```bash
bin/bench-cli --model-dir gemini-3-pro-preview --agent gemini --model gemini-3-pro-preview --tmux comp341
bin/bench-cli --model-dir gemini-3-pro-preview --agent gemini --model gemini-3-pro-preview --tmux comp322
bin/bench-cli --model-dir gemini-3-pro-preview --agent gemini --model gemini-3-pro-preview --tmux comp215
bin/bench-cli --model-dir gemini-3-pro-preview --agent gemini --model gemini-3-pro-preview --tmux comp382
bin/bench-cli --model-dir gemini-3-pro-preview --agent gemini --model gemini-3-pro-preview --tmux comp411
bin/bench-cli --model-dir gemini-3-pro-preview --agent gemini --model gemini-3-pro-preview --tmux comp421
```
Then grading + review.

### gemini-3-1-pro-preview — 44 empty
Low priority. Has 22 done (comp140, comp182, partial comp215/321/382/411).

### gemini-3-1-pro-cursor — 66 empty (fresh)
```bash
bin/bench-cli --model-dir gemini-3-1-pro-cursor --agent cursor --model gemini-3.1-pro-preview --tmux
```

## Low Priority / Parked

### nvidia-nemotron-super — 42 empty, ran via custom harness. Resume later.
### nvidia-nemotron-nano — 16/16 done but only comp140+comp182. Partial course set.
### openrouter-hunter-alpha — 16/16 done but only comp140+comp182. Partial course set.
### minimax-m2-5-run-b — secondary run, 40/46 done. Needs grading if we want it.

## Notes

- comp310 hw1-6 is the most common gap across models (Docker-based Java)
- comp310 hw7 is an LLM-graded design task (no code tests, no code review)
- Written-only assignments (comp182 written HWs, comp382, comp421 exams) don't get code review — this is expected
- Code-only assignments (comp215, comp411, comp421 labs) don't get LLM written grades — also expected
- "claude" vs "opus" tagged grade files are both from Opus (different runs, same model)
- Use `--model gemini-3-pro-preview` (not `--model pro`) for Gemini 3.0 — `pro` resolves to 3.1

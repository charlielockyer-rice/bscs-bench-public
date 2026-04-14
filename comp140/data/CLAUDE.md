# comp140/data

Synthetic test data for Module 3 (Stock Prediction) and Module 4 (Kevin Bacon) assignments.

## Structure

```
data/
├── generate_stock_data.py      # Generator for stock prices
├── generate_movie_data.py      # Generator for movie graph
├── comp140_module3_*.txt       # Stock price data (DJIA, GOOG, FSLR)
├── comp140_module4_*.json      # Movie graph edge lists
└── comp140_module4_actors_*.txt # Actor name lists
```

## Data Formats

**Stock Files** (`comp140_module3_*.txt`):
- Space-separated float prices (500 days each)
- Consumed by `lib/comp140_module3.py:get_historical_prices()`

**Movie Graph** (`comp140_module4_*.json`):
- JSON array of `[actor1, actor2, [movie1, movie2, ...]]` tuples
- Consumed by `lib/comp140_module4.py:load_graph()`

**Actor Lists** (`comp140_module4_actors_*.txt`):
- Newline-separated actor names
- Consumed by `lib/comp140_module4.py:load_actors()`

## Generator Details

**Stock Data** - Uses geometric Brownian motion with seeded randomness:
| Symbol | Initial | Volatility | Seed |
|--------|---------|------------|------|
| DJIA   | 13000   | 15%        | 42   |
| GOOG   | 700     | 25%        | 123  |
| FSLR   | 30      | 45%        | 456  |

**Movie Data** - Builds actor-movie bipartite graph:
| Size   | Actors | Movies | Seed |
|--------|--------|--------|------|
| small  | 50     | 20     | 42   |
| medium | 150    | 50     | 123  |

Uses power-law distribution: ~10% of actors are "stars" with 3x casting probability.

## Regenerating Data

```bash
cd comp140/data
python generate_stock_data.py
python generate_movie_data.py
```

Seeds are fixed, so output is deterministic.

## Key Concepts

- Data files are loaded via `codeskulptor.file2url()` which resolves to local paths
- The lib modules handle both URL and local file loading transparently
- Edge list format: each edge stores the set of movies two actors share (not just one)

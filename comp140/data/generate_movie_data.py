#!/usr/bin/env python3
"""
Generate synthetic movie graph data for the Kevin Bacon game.

This creates a realistic-looking actor-movie graph with:
- Actors with plausible names
- Movies with plausible titles
- Power-law distribution for actor appearances (some "stars" appear in many movies)

Usage:
    python generate_movie_data.py

Output files:
    comp140_module4_small.json       - Small graph (50 actors, 20 movies)
    comp140_module4_medium.json      - Medium graph (150 actors, 50 movies)
    comp140_module4_actors_small.txt - Actor list for small graph
    comp140_module4_actors_medium.txt - Actor list for medium graph
"""

import json
import random
from collections import defaultdict
from itertools import combinations
from pathlib import Path


# Name components for generating actor names
FIRST_NAMES = [
    "James", "Mary", "Robert", "Patricia", "John", "Jennifer", "Michael", "Linda",
    "David", "Elizabeth", "William", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
    "Thomas", "Sarah", "Christopher", "Karen", "Charles", "Lisa", "Daniel", "Nancy",
    "Matthew", "Betty", "Anthony", "Margaret", "Mark", "Sandra", "Donald", "Ashley",
    "Steven", "Kimberly", "Paul", "Emily", "Andrew", "Donna", "Joshua", "Michelle",
    "Kenneth", "Dorothy", "Kevin", "Carol", "Brian", "Amanda", "George", "Melissa",
    "Timothy", "Deborah", "Ronald", "Stephanie", "Edward", "Rebecca", "Jason", "Sharon",
    "Jeffrey", "Laura", "Ryan", "Cynthia", "Jacob", "Kathleen", "Gary", "Amy",
    "Nicholas", "Angela", "Eric", "Shirley", "Jonathan", "Anna", "Stephen", "Brenda",
    "Larry", "Pamela", "Justin", "Emma", "Scott", "Nicole", "Brandon", "Helen",
    "Benjamin", "Samantha", "Samuel", "Katherine", "Raymond", "Christine", "Gregory", "Debra",
    "Frank", "Rachel", "Alexander", "Carolyn", "Patrick", "Janet", "Jack", "Catherine",
]

LAST_NAMES = [
    "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
    "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
    "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White",
    "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker", "Young",
    "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
    "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell",
    "Carter", "Roberts", "Turner", "Phillips", "Evans", "Parker", "Edwards", "Collins",
    "Stewart", "Morris", "Murphy", "Rivera", "Cook", "Rogers", "Morgan", "Peterson",
    "Cooper", "Reed", "Bailey", "Bell", "Gomez", "Kelly", "Howard", "Ward",
    "Cox", "Diaz", "Richardson", "Wood", "Watson", "Brooks", "Bennett", "Gray",
    "James", "Reyes", "Cruz", "Hughes", "Price", "Myers", "Long", "Foster",
    "Sanders", "Ross", "Morales", "Powell", "Sullivan", "Russell", "Ortiz", "Jenkins",
]

# Movie title components
ADJECTIVES = [
    "The", "A", "Last", "First", "Final", "Secret", "Hidden", "Lost", "Dark",
    "Bright", "Silent", "Endless", "Eternal", "Broken", "Fallen", "Rising", "Golden",
    "Silver", "Iron", "Steel", "Crimson", "Midnight", "Shadow", "Distant", "Ancient",
]

NOUNS = [
    "Night", "Day", "Storm", "River", "Mountain", "Forest", "City", "Road",
    "Journey", "Dream", "Memory", "Promise", "Secret", "Legacy", "Destiny", "Empire",
    "Kingdom", "Quest", "Adventure", "Mystery", "Truth", "Honor", "Glory", "Justice",
    "Heart", "Soul", "Spirit", "Mind", "Eye", "Hand", "Voice", "World",
]

SUFFIXES = [
    "", "", "", "",  # Empty more common
    "Returns", "Rises", "Falls", "Awakens", "Begins", "Ends",
    "II", "III", "Reborn", "Unleashed", "Revealed",
]

GENRES = [
    "of Darkness", "of Light", "of Tomorrow", "of Yesterday", "of Shadows",
    "in the Dark", "in Time", "Beyond", "Within", "Beneath",
    "", "", "", "", "",  # Empty more common
]


def generate_actor_names(count: int, seed: int = None) -> list[str]:
    """Generate unique actor names."""
    if seed is not None:
        random.seed(seed)

    names = set()
    while len(names) < count:
        first = random.choice(FIRST_NAMES)
        last = random.choice(LAST_NAMES)
        name = f"{first} {last}"
        names.add(name)

    return sorted(names)


def generate_movie_titles(count: int, seed: int = None) -> list[str]:
    """Generate unique movie titles."""
    if seed is not None:
        random.seed(seed)

    titles = set()
    while len(titles) < count:
        adj = random.choice(ADJECTIVES)
        noun = random.choice(NOUNS)
        suffix = random.choice(SUFFIXES)
        genre = random.choice(GENRES)

        title = f"{adj} {noun}"
        if genre:
            title = f"{title} {genre}"
        if suffix:
            title = f"{title} {suffix}"

        titles.add(title.strip())

    return sorted(titles)


def assign_actors_to_movies(
    actors: list[str],
    movies: list[str],
    min_cast: int = 4,
    max_cast: int = 10,
    star_count: int = 5,
    star_bias: float = 3.0,
    seed: int = None
) -> dict[str, list[str]]:
    """
    Assign actors to movies with power-law distribution.

    Some actors (stars) appear in many more movies than average.

    Args:
        actors: List of actor names
        movies: List of movie titles
        min_cast: Minimum cast size per movie
        max_cast: Maximum cast size per movie
        star_count: Number of "star" actors with higher appearance rates
        star_bias: How much more likely stars are to be cast
        seed: Random seed

    Returns:
        Dict mapping movie title to list of actors
    """
    if seed is not None:
        random.seed(seed)

    # Designate some actors as "stars" who appear more frequently
    stars = set(random.sample(actors, min(star_count, len(actors))))

    # Create weights: stars have higher weight
    weights = []
    for actor in actors:
        if actor in stars:
            weights.append(star_bias)
        else:
            weights.append(1.0)

    movie_casts = {}
    for movie in movies:
        cast_size = random.randint(min_cast, max_cast)
        # Weighted random sampling without replacement
        cast = weighted_sample(actors, weights, cast_size)
        movie_casts[movie] = cast

    return movie_casts


def weighted_sample(population: list, weights: list[float], k: int) -> list:
    """Weighted random sampling without replacement."""
    # Normalize weights
    total = sum(weights)
    probs = [w / total for w in weights]

    # Sample without replacement
    selected = []
    available = list(range(len(population)))
    available_probs = probs[:]

    for _ in range(min(k, len(population))):
        # Renormalize probabilities
        total_prob = sum(available_probs[i] for i in available)
        if total_prob == 0:
            break

        # Random selection
        r = random.random() * total_prob
        cumulative = 0
        # Default to last available item to handle floating-point edge cases
        chosen_idx = available[-1]

        for idx in available:
            cumulative += available_probs[idx]
            if r <= cumulative:
                chosen_idx = idx
                break

        selected.append(population[chosen_idx])
        available.remove(chosen_idx)

    return selected


def build_edge_list(movie_casts: dict[str, list[str]]) -> list[tuple[str, str, list[str]]]:
    """
    Build edge list from movie casts.

    For each movie, every pair of actors shares that movie.

    Returns:
        List of (actor1, actor2, [movie1, movie2, ...]) tuples
    """
    # Track which movies each pair of actors share
    pair_movies = defaultdict(set)

    for movie, cast in movie_casts.items():
        for actor1, actor2 in combinations(cast, 2):
            # Sort to ensure consistent ordering
            key = tuple(sorted([actor1, actor2]))
            pair_movies[key].add(movie)

    # Convert to edge list format
    edges = []
    for (actor1, actor2), movies in sorted(pair_movies.items()):
        edges.append((actor1, actor2, sorted(movies)))

    return edges


def generate_graph(
    num_actors: int,
    num_movies: int,
    name: str,
    output_dir: Path,
    seed: int = None
):
    """Generate a complete movie graph dataset."""
    print(f"Generating {name} graph ({num_actors} actors, {num_movies} movies)...")

    # Generate data
    actors = generate_actor_names(num_actors, seed=seed)
    movies = generate_movie_titles(num_movies, seed=seed + 1000 if seed is not None else None)
    movie_casts = assign_actors_to_movies(
        actors, movies,
        star_count=max(3, num_actors // 10),
        seed=seed + 2000 if seed is not None else None
    )
    edges = build_edge_list(movie_casts)

    # Write JSON edge list
    json_file = output_dir / f"comp140_module4_{name}.json"
    with open(json_file, "w") as f:
        json.dump(edges, f, indent=2)

    # Write actor list
    actors_file = output_dir / f"comp140_module4_actors_{name}.txt"
    with open(actors_file, "w") as f:
        f.write("\n".join(actors) + "\n")

    # Print statistics
    unique_actors_in_graph = set()
    for actor1, actor2, _ in edges:
        unique_actors_in_graph.add(actor1)
        unique_actors_in_graph.add(actor2)

    print(f"  Edges: {len(edges)}")
    print(f"  Actors in graph: {len(unique_actors_in_graph)}")
    print(f"  Files: {json_file.name}, {actors_file.name}")
    print()


def main():
    """Generate all movie graph files."""
    output_dir = Path(__file__).parent

    # Generate small graph
    generate_graph(
        num_actors=50,
        num_movies=20,
        name="small",
        output_dir=output_dir,
        seed=42
    )

    # Generate medium graph
    generate_graph(
        num_actors=150,
        num_movies=50,
        name="medium",
        output_dir=output_dir,
        seed=123
    )


if __name__ == "__main__":
    main()

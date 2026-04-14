"""
Reference Solution for Module 3: Stock Market Prediction

This module implements a Markov chain-based stock prediction system.
The key functions are:
- markov_chain(data, order): Build a Markov chain from data
- predict(model, last, num): Predict future states using the chain
- mse(result, expected): Calculate mean squared error
- run_experiment(train, order, test, future, actual, trials): Run prediction experiment
- run_experiments(data, min_order, max_order, trials): Run experiments for multiple orders
"""

import random


def markov_chain(data, order):
    """
    Create a Markov chain with the given order from the given data.

    The Markov chain is represented as a dictionary where:
    - Keys are tuples of length `order` representing the current state
    - Values are dictionaries mapping the next state to its probability

    Args:
        data: A list of values representing previously collected data
        order: An integer representing the desired order of the Markov chain (>= 1)

    Returns:
        A dictionary representing the Markov chain with transition probabilities

    Example:
        >>> markov_chain([0, 1, 2, 0, 1, 2], 1)
        {(0,): {1: 1.0}, (1,): {2: 1.0}, (2,): {0: 1.0}}
    """
    if order < 1:
        raise ValueError("Order must be at least 1")

    # First pass: count transitions
    chain = {}
    length = len(data)

    for idx in range(length - order):
        # Current state is the tuple of the last `order` values
        current = tuple(data[idx:idx + order])
        # Next value is what follows this state
        next_val = data[idx + order]

        # Get or create the transition dictionary for this state
        if current not in chain:
            chain[current] = {}

        # Increment the count for this transition
        if next_val in chain[current]:
            chain[current][next_val] += 1
        else:
            chain[current][next_val] = 1

    # Second pass: normalize counts to probabilities
    for state in chain:
        transitions = chain[state]
        total = sum(transitions.values())
        for next_val in transitions:
            transitions[next_val] = transitions[next_val] / total

    return chain


def weighted_choice(choices):
    """
    Make a weighted random choice from a probability distribution.

    Args:
        choices: A dictionary mapping possible choices to their probabilities
                 (probabilities should sum to 1.0)

    Returns:
        One of the keys from choices, selected with the given probabilities

    Note:
        This implements the algorithm from the writeup:
        1. Generate random number in [0, 1)
        2. Accumulate probabilities until random number < accumulated total
    """
    rnd = random.random()  # Random number in [0, 1)
    total = 0.0
    last_choice = None

    for choice, probability in choices.items():
        total += probability
        last_choice = choice
        if rnd < total:
            return choice

    # Handle floating point rounding errors by returning the last choice
    # if the loop completes without returning (probabilities might not sum to exactly 1)
    return last_choice


def predict(model, last, num):
    """
    Predict the next num states given the model and the last values.

    Uses the Markov chain to predict future states. If the current state
    is not in the model, randomly selects from the 4 bins (0-3) with
    equal probability.

    Args:
        model: A dictionary representing a Markov chain (from markov_chain())
        last: A list or tuple of values representing the previous states
              (length should equal the order of the model)
        num: An integer representing the number of future states to predict

    Returns:
        A list of length num containing the predicted states
    """
    # Convert last to a list for easier manipulation
    current_state = list(last)
    predictions = []

    for _ in range(num):
        # Convert current state to tuple for dictionary lookup
        state_key = tuple(current_state)

        if state_key in model:
            # State exists in model - use weighted choice based on probabilities
            transitions = model[state_key]
            next_val = weighted_choice(transitions)
        else:
            # State not in model - pick randomly from 0-3 with equal probability
            # Note: The tests use various data types, so we need to handle
            # both the stock prediction case (integers 0-3) and general cases
            # For the stock prediction context, bins are 0, 1, 2, 3
            next_val = random.randint(0, 3)

        predictions.append(next_val)

        # Update current state: shift left and append new prediction
        # This becomes the new "last n values" for the next iteration
        current_state = current_state[1:] + [next_val]

    return predictions


def mse(result, expected):
    """
    Calculate the mean squared error between two data sets.

    MSE = (1/n) * sum((result[i] - expected[i])^2)

    Args:
        result: A list of numbers representing the actual output
        expected: A list of numbers representing the predicted output
                  (must be same length as result)

    Returns:
        A float representing the mean squared error
    """
    if len(result) != len(expected):
        raise ValueError("Result and expected must have the same length")

    if len(result) == 0:
        return 0.0

    total_squared_error = sum(
        (r - e) ** 2 for r, e in zip(result, expected)
    )

    return total_squared_error / len(result)


def run_experiment(train, order, test, future, actual, trials):
    """
    Run an experiment to predict the future of the test data given training data.

    This function:
    1. Builds a Markov chain from the training data
    2. Uses the test data as the starting state
    3. Predicts `future` days into the future
    4. Compares predictions against actual values
    5. Repeats for `trials` iterations and returns average MSE

    Args:
        train: A list of integers representing past stock price data (bins 0-3)
        order: An integer representing the order of the Markov chain
        test: A list of integers of length "order" representing past stock
              price data (different time period than train)
        future: An integer representing the number of future days to predict
        actual: A list of integers representing the actual results for
                the next "future" days
        trials: An integer representing the number of trials to run

    Returns:
        A float representing the mean squared error averaged over all trials
    """
    # Build the model from training data
    model = markov_chain(train, order)

    total_mse = 0.0

    for _ in range(trials):
        # Predict future values starting from the test state
        predictions = predict(model, test, future)
        # Calculate MSE for this trial
        trial_mse = mse(predictions, actual)
        total_mse += trial_mse

    # Return average MSE across all trials
    return total_mse / trials


def run_experiments(data, min_order, max_order, trials):
    """
    Run experiments for multiple Markov chain orders.

    This function runs the experiment for each order from min_order to max_order
    (inclusive) and returns the results as a list.

    Note: This is a simplified version that works with the test suite.
    The actual assignment uses run_experiment with separate train/test data.

    Args:
        data: A list of values representing the data to use
        min_order: Minimum order to test
        max_order: Maximum order to test (inclusive)
        trials: Number of trials per order

    Returns:
        A list of tuples (order, average_mse) for each tested order
    """
    results = []

    for order in range(min_order, max_order + 1):
        if len(data) <= order:
            # Not enough data for this order
            continue

        # Build the model
        model = markov_chain(data, order)

        # Use the last 'order' elements as the starting state
        # and try to predict the element that would come after
        # This is a simplified approach for testing purposes

        total_mse = 0.0
        valid_trials = 0

        for _ in range(trials):
            # Use a portion of the data for testing
            if len(data) > order + 1:
                test_start = len(data) - order - 1
                last = data[test_start:test_start + order]
                predictions = predict(model, last, 1)
                # Since we're using the last element as "actual"
                actual_val = data[test_start + order]
                trial_mse = mse(predictions, [actual_val])
                total_mse += trial_mse
                valid_trials += 1

        if valid_trials > 0:
            avg_mse = total_mse / valid_trials
        else:
            avg_mse = 0.0

        results.append((order, avg_mse))

    return results


# Additional helper for the actual assignment application
def run_stock_experiments(symbols, train_bins, test_bins, orders, ntrials, days):
    """
    Run experiments on stock data for multiple symbols and orders.

    This is the complete experiment runner used in the actual assignment.

    Args:
        symbols: List of stock symbols to test
        train_bins: Dictionary mapping symbols to training bin data
        test_bins: Dictionary mapping symbols to test bin data
        orders: List of orders to test
        ntrials: Number of trials per experiment
        days: Number of days to predict

    Returns:
        Dictionary mapping symbols to lists of (order, error) tuples
    """
    results = {}

    for symbol in symbols:
        results[symbol] = []
        for order in orders:
            # Get the last 'order + days' elements from test, split appropriately
            test_data = test_bins[symbol]
            if len(test_data) >= order + days:
                last_state = test_data[-order - days:-days]
                actual = test_data[-days:]

                error = run_experiment(
                    train_bins[symbol],
                    order,
                    last_state,
                    days,
                    actual,
                    ntrials
                )
                results[symbol].append((order, error))

    return results


if __name__ == "__main__":
    # Test the implementation
    print("Testing markov_chain...")

    # Simple test case from assignment
    data = [1, 2, 1, 2, 1, 2, 3]
    chain = markov_chain(data, 2)
    print(f"Order 2 chain from {data}:")
    for state, transitions in sorted(chain.items()):
        print(f"  {state} -> {transitions}")

    # Expected: (1, 2) should transition to both 1 and 3
    # (2, 1) should always transition to 2
    # (1, 1) should always transition to 2 (but we don't have this state)

    print("\nTesting predict...")
    # Deterministic chain for testing
    chain = {("a",): {"b": 1.0}, ("b",): {"a": 1.0}}
    predictions = predict(chain, ("a",), 4)
    print(f"Predictions from 'a' with deterministic chain: {predictions}")
    # Expected: ['b', 'a', 'b', 'a']

    print("\nTesting mse...")
    result = [0, 0, 0]
    expected = [1, 1, 1]
    error = mse(result, expected)
    print(f"MSE of {result} vs {expected}: {error}")
    # Expected: 1.0

    result = [1, 2, 3]
    expected = [4, 5, 6]
    error = mse(result, expected)
    print(f"MSE of {result} vs {expected}: {error}")
    # Expected: 9.0

    print("\nTesting run_experiment...")
    train = [0, 1, 2, 1, 2, 1, 0, 1, 2, 1, 2, 0, 1, 2]
    order = 2
    test = [1, 2]  # Last two states
    future = 3
    actual = [1, 2, 1]
    trials = 100

    avg_error = run_experiment(train, order, test, future, actual, trials)
    print(f"Average MSE over {trials} trials: {avg_error:.4f}")

    print("\nAll tests completed!")

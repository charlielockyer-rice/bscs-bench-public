# Stock Prediction Writeup Solutions

## 3.A.i. Markov Chains

**Input:**
- An ordered sequence of data, *data*
- The order, *n*, of the desired Markov Chain

1. Let *chain* be a new empty mapping.
2. Let *length* be the number of elements in *data*. (Assume the elements of *data* are $data_0$ through $data_{length-1}$.)
3. For each number, *idx*, from 0 to *length* - *n* - 1, do the following:
   1. Set *current* to be the sequence of values $data_{idx}$ through $data_{idx+n-1}$
   2. Let *next* be the mapping for $chain_{current}$, or a new empty mapping if one does not exist.
   3. If $data_{idx+n}$ is a key in the mapping *next*, increment $next_{data_{idx+n}}$ by 1, otherwise, set $next_{data_{idx+n}}$ to be 1.
   4. Set $chain_{current}$ to the mapping *next*.
4. For every key, *state*, in the *chain* mapping, normalize the values of the mapping $chain_{state}$ so that they sum to 1. This is accomplished by dividing each count in the mapping by the total counts in that mapping. This converts the counts in the mappings into probabilities.
5. Return *chain*.

---

## 3.B.i. Prediction

For this recipe, it is useful to break the problem into two parts: *weighted_choice* and *predict*.

### weighted_choice

**Input:** A mapping, *choices*, that maps possible choices to the probability of selecting that choice.

1. Select a random number, *rnd*, in $[0, 1)$.
2. Let *total* be 0.
3. For each key, *choice*, in the *choices* mapping, do the following:
   1. Let *total* be *total* + $choices_{choice}$.
   2. If *rnd* < *total*, then return *choice*.

*Note that if the probabilities in choices sum up to 1, then this recipe will always return a value. If they do not (due to rounding error, for example), it would be a good idea to return the last value in choice if the loop finishes without returning anything.*

### predict

**Input:**
- An $n^{th}$ order Markov Chain, *model*
- The last *n* values, *last*
- The number of predictions to make, *num*

1. Let *choices* be a new empty sequence.
2. For each number, *trial*, from 0 to *num* - 1, do the following:
   1. If $model_{last}$ exists, do the following:
      1. Let *next* be the mapping $model_{last}$.
      2. Let $choices_{trial}$ be the result of calling *weighted_choice* with *next* as an input.
   2. Otherwise, let $choices_{trial}$ be a randomly selected integer between 0 and 3 with equal probability.
   3. Let *last* be a new sequence containing $last_1, .., last_{n-1}, choices_{trial}$.
3. Return *choices*.

---

## 5. Discussion

**1. Best Markov Chain Orders**

The best order for FSLR is 7, for GOOG is 3, and for DJIA is 5 (note that these answers will depend on your results in 4.B and may not be exactly the same, but they should be close). The modeling we are doing is not completely accurate, as stock prices depend on much more than just the previous days' trends. Further, these three stocks behave significantly differently, as the price fluctuation and histogram of bins shows. So, it makes sense that different models would more accurately predict the different stocks.

**2. Which Stock Has Lowest Error**

The DJIA has the lowest error. Looking at the price change plot, it is easy to see that its price shows fewer fluctuations than the others, so it makes sense that the transition probabilities in the model are much higher for transitions to the middle bins (1 and 2) than the outer bins (0 and 3), leading to better predictions. FSLR is far less stable, for example, and has far fewer patterns in its price fluctuations, potentially indicating that factors other than price history are more important.

While you were not expected to know anything about the stock market, this behavior also intuitively makes sense. The DJIA averages the performance of large, mature companies that probably do not all have many large changes in price at the same time from day to day. In contrast, FSLR is a young company that might be impacted very heavily by external events.

**3. Number of States in an $n^{th}$ Order Markov Chain**

Each of the $n$ values can take on one of the numbers 0 through 3, so there are $4^n$ possible states in an $n^{th}$ order Markov Chain.

**4. Data Sufficiency**

502 days is not enough data to see all of the states if $n$ is larger than 4, as $4^5 = 1024$. Even if $n$ is 4 or less, it is still possible that there are states that do not appear in the training data. If there is not enough data then the predictions are likely to be less accurate. If the testing data includes states that did not appear in the training data, then we will predict the next day with equal probability from the bins. While this is the best we can do, it does not take into account the history of the stock, so is likely to be inaccurate. If the models are at all accurate, we would expect better predictions when the states in the testing data are a part of the model.

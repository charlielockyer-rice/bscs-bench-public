Reasoning about Algorithms &emsp; October 13, 2022

Rice University &emsp; COMP 382, Fall 2022

Michael Burke &emsp; Konstantinos Mamouras &emsp; Homework 4

---

# Homework 4 – Due 11:59pm, October 23, 2022

1. **(Time-varying shortest path)** (20 points) Suppose you are planning a road trip in unpredictable weather. You have a map of the area represented as a directed graph, where nodes represent intermediate destinations and edges represent roads between them.

   You have access to a website that answers queries in the following form: given an edge $e = (v, w)$ connecting two sites $v$ and $w$, and given a proposed starting time $t$ from the location $v$, the website returns a value $f_e(t)$, the predicted arrival time at $w$. The website guarantees that $f_e(t) \ge t$ for all edges $e$ and all times $t$ (i.e., you don't travel backward in time), and $f_e(t)$ is a monotone function of $t$ (i.e., you don't arrive earlier by starting later). Other than that, the functions $f_e(t)$ may be arbitrary.

   You want to use the website to find the fastest way to travel through the directed graph from the starting point to the intended destination. The starting time is 0. Give an efficient algorithm to do this, where we treat a single query to the website to be a single computational step.

2. **(Weighted sum of completion times)** (20 points) Consider a small business that faces the following scheduling problem. The business has $n$ customers and wants to schedule jobs from these customers on a single machine. The $i$-th job takes $t_i$ units to complete and has a weight $w_i$ that represents its importance.

   Given a schedule (an ordering of jobs), let $C_i$ be the finishing time of job $i$. For example, if $j$ and $k$ are the first two jobs in the schedule, then $C_j = t_j$ and $C_k = t_j + t_k$. The business aims to find an *optimal schedule*, i.e., a schedule of jobs that minimizes the weighted sum $\sum_{i=1}^{n} w_i C_i$ of completion times.

   Give an efficient algorithm to solve this problem. The input of the algorithm is a set of $n$ jobs, each with a processing time $t_i$ and a weight $w_i$. The output is a schedule that is optimal according to the definition above.

3. **(Smallest complete supervisory committee)** (20 points) The manager of a student union comes to you with the following problem. She's in charge of a group of $n$ students, each of whom is scheduled to work one *shift* during the week. There are different jobs associated with these shifts but we can view each shift as a single contiguous interval of time. There can be multiple shifts going on at once.

   She's trying to choose a subset of these $n$ students to form a *supervisory committee* that she can meet with once a week. She considers such a committee to be *complete* if, for every student not on the committee, the student's shift overlaps with the shift of some student who is on the committee. In this way, each student's performance can be observed by at least one person who's serving on the committee.

   Give an efficient algorithm that takes the schedule of $n$ shifts and produces a complete supervising committee containing as few students as possible.

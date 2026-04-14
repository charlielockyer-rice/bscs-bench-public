# COMP 382: Reasoning about Algorithms
## Homework 7

**Total Points: 100** (NP-Completeness Reductions)

**Note:** Solutions to polynomial-time mapping reductions must be accompanied by proofs of correctness.

---

## Problem 1: Efficient Recruiting [25 points]

Suppose you are helping organize a summer camp. The camp is supposed to have at least one counselor who is skilled at each of the n sports covered by the camp (baseball, volleyball, and so on). They have received job applicants from m potential counselors. For each of the n sports, there is a subset of the m applicants qualified in that sport.

The question is: For a given number k < m, is it possible to hire at most k of the counselors and have at least one counselor qualified in each of the n sports? We'll call this the *Efficient Recruiting* problem.

**Show that** Vertex Cover ≤ₚ Efficient Recruiting.

---

## Problem 2: Plot Fulfillment [25 points]

Hypertext fiction is a type of fiction consisting of a set of pages, each containing some text, along with links between them. The reader of such a hypertext story follows a *trail* — a sequence of pages that begins with a *start page* s and finishes at an *end page* t — through the story.

Suppose you have created a piece of hypertext fiction, and you would like to know if there is a trail through your story that contains each of a set of *thematic elements*. You have n thematic elements, the i-th of which can be identified with a set Tᵢ of pages.

We formalize this as the *Plot Fulfillment* problem, which asks: is there a trail from s to t that contains at least one page from each of the sets Tᵢ?

**Show that** Hitting Set ≤ₚ Plot Fulfillment.

---

## Problem 3: Path Selection [25 points]

Suppose you are managing a communication network, modeled by a directed graph G = (V, E). There are c users who want to use the network. User i (we have 1 ≤ i ≤ c) issues a *request* to reserve a specific path Pᵢ in G on which to transmit data.

You want to accept as many path requests as possible, except if you accept Pᵢ and Pⱼ, then Pᵢ and Pⱼ cannot share any nodes.

The *Path Selection* problem formalizes this intent. It asks: Given a directed graph G = (V, E), a set of path requests P₁, ..., Pₓ, and a number k, is it possible to select at least k of the paths so that no two of the selected paths share any nodes?

**Show that** Independent Set ≤ₚ Path Selection.

---

## Problem 4: Half 3-SAT Satisfiability [25 points]

Given a 3-SAT formula φ with n variables and m clauses, where m is even. We wish to determine whether there is a truth assignment to the variables of φ such that exactly half the clauses evaluate to true and half the clauses evaluate to false.

**Show that** 3-SAT ≤ₚ Half 3-SAT Satisfiability.

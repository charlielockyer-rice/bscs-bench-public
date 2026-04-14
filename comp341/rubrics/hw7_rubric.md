---
total_points: 100
written_questions_points: 17
---

# COMP 341 Homework 7: Hybrid Deep Learning (House Images) - Written Questions Rubric

## Part 1: Exploring the Home Images (8 points)

### Question 1: "Using the dimensionality reduction results and/or any algorithms we covered in class, estimate the number of houses in our training data that have a drawing as their primary image." (8 pts)

**Expected Insights:**
- Use clustering, outlier detection, or visual inspection
- Identify that drawings (schematics/floorplans) have different pixel patterns
- Reference the 2D plot where drawings should cluster separately
- Provide a specific number estimate
- Correct order of magnitude is expected (typically 5-20 homes)
- Show methodology for arriving at estimate

**Rubric:**
- **Full credit (8 pts)**: Describes methodology, identifies approximate cluster/outliers, gives reasonable estimate with evidence
- **Partial credit (5-7 pts)**: Reasonable methodology but estimate is questionable
- **Partial credit (2-4 pts)**: Gives a number without clear methodology
- **No credit (0 pts)**: Missing

---

## Part 2: Model Comparison (9 points)

### Question 2: "Based on the results that you see in your plot, what do you think about using images, tabular features, or both for predicting list price?" (9 pts)

**Expected Insights:**
- Reference the loss plot comparing three models
- Compare final validation losses:
  - HybridHouseNN (both)
  - HouseImageOnly
  - HouseFeatsOnly
- Discuss convergence speed (which learns faster?)
- Images alone may struggle (house exterior doesn't fully capture value)
- Tabular features (sq_ft, beds, baths) are highly predictive
- Combination may or may not beat tabular alone
- Consider overfitting patterns (train vs validation gap)

**Rubric:**
- **Full credit (9 pts)**: Compares all three models with specific loss values, explains why results make sense given the data characteristics
- **Partial credit (6-8 pts)**: Compares models but analysis is incomplete
- **Partial credit (3-5 pts)**: Generic statement without reference to results
- **No credit (0 pts)**: Missing

---

## Grading Summary

| Question | Topic | Points |
|----------|-------|--------|
| Q1 | Drawing detection estimate | 8 |
| Q2 | Image vs tabular vs hybrid comparison | 9 |
| **Total Written** | | **17** |

## Extra Credit: TensorBoard (up to 5 pts)

Students set up TensorBoard to log training/validation loss and use it to guide hyperparameter choices.

**Rubric:**
- **Full credit (5 pts)**: TensorBoard properly configured with %tensorboard magic, demonstrates use for guiding epochs and learning rate choices
- **Partial credit (3-4 pts)**: TensorBoard set up but not used effectively
- **Minimal credit (1-2 pts)**: Partial implementation

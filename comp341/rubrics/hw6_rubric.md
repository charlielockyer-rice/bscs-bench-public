---
total_points: 100
written_questions_points: 15
---

# COMP 341 Homework 6: Fashion-MNIST CNN - Written Questions Rubric

## Part 4: Checking Performance (15 points)

### Question 1: "Based on your plot, does it seem like our model converged within 15 epochs? Would you add more epochs? Or stop earlier? Explain." (5 pts)

**Expected Insights:**
- Reference the training/validation loss plot
- Convergence indicators:
  - Loss curves flatten (plateau)
  - Training and validation curves stabilize
  - No significant improvement in later epochs
- If still decreasing: may benefit from more epochs
- If validation loss increases while training decreases: overfitting, stop earlier
- Consider the gap between train and validation loss

**Rubric:**
- **Full credit (5 pts)**: References specific plot behavior (where curves plateau/diverge), makes reasoned recommendation with evidence
- **Partial credit (3-4 pts)**: Reasonable answer but lacks specific reference to plot patterns
- **Partial credit (1-2 pts)**: Generic statement without evidence
- **No credit (0 pts)**: Missing

### Question 2: "Does our model have any trouble distinguishing any items of clothing? If so, which ones does it tend to mix up?" (5 pts)

**Expected Insights:**
- Reference the confusion matrix
- Common confusions in Fashion-MNIST:
  - Shirt vs T-shirt/Top (similar silhouettes)
  - Pullover vs Coat (similar upper body garments)
  - Sneaker vs Ankle Boot (footwear)
- Note which pairs have highest off-diagonal values
- Explain WHY confusion makes sense (visual similarity)

**Rubric:**
- **Full credit (5 pts)**: Identifies specific confused pairs from confusion matrix with plausible visual similarity explanation
- **Partial credit (3-4 pts)**: Identifies pairs but explanation is weak
- **Partial credit (1-2 pts)**: Vague statement about confusion without specifics
- **No credit (0 pts)**: Missing

### Question 3: "In this homework we tried one model architecture (a CNN with 2 convolutions and 3 fully connected layers) and achieved reasonable performance on the validation set. Could we instead use the output of the CNN with a classical machine learning algorithm? Explain your answer." (5 pts)

**Expected Insights:**
- YES, this is a valid approach (transfer learning / feature extraction)
- CNN layers can be seen as learned feature extractors
- Can take output after conv layers (flattened) as features
- Feed to SVM, Random Forest, Logistic Regression, etc.
- Benefits: may be faster to train classical model, interpretability
- Drawbacks: may not optimize jointly, may lose some information
- This is similar to using pre-trained CNN features

**Rubric:**
- **Full credit (5 pts)**: Correctly explains that CNN can be used as feature extractor for classical ML, discusses pros/cons
- **Partial credit (3-4 pts)**: Understands the concept but explanation is incomplete
- **Partial credit (1-2 pts)**: Vague answer or incorrect reasoning
- **No credit (0 pts)**: Missing or says it's impossible

---

## Grading Summary

| Question | Topic | Points |
|----------|-------|--------|
| Q1 | Convergence analysis | 5 |
| Q2 | Confusion matrix interpretation | 5 |
| Q3 | CNN + classical ML feasibility | 5 |
| **Total Written** | | **15** |

## Extra Credit: Hyperparameter Tuning (up to 10 pts)

Students explore hyperparameters (learning rate, activation functions, regularization, architecture changes) and demonstrate improvements.

**Rubric:**
- **2 pts per hyperparameter** that improves performance or convergence speed
- Must use consistent random seed and data splits for valid comparison
- Must clearly document what was changed and the effect

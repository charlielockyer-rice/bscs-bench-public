package edu.rice.comp322.trees;

import edu.rice.comp322.provided.trees.GList;
import edu.rice.comp322.provided.trees.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import edu.rice.comp322.provided.trees.generatetree.TreeGenerator;

import static edu.rice.comp322.solutions.TreeSolutions.*;

public class TreeSolutionsTest {

    private Tree<Integer> tree1 = TreeGenerator.generateIntTreeOne();
    private Tree<Integer> tree2 = TreeGenerator.generateIntTreeTwo();
    private Tree<Integer> tree3 = TreeGenerator.generateIntTreeThree();

    @Test
    public void problem1() {
        Assertions.assertEquals(156, problemOne(tree1));
        Assertions.assertEquals(2, problemOne(tree2));
        Assertions.assertEquals(121, problemOne(tree3));
    }

    @Test
    public void problem2() {
        Assertions.assertEquals(156, problemTwo(tree1));
        Assertions.assertEquals(2, problemTwo(tree2));
        Assertions.assertEquals(121, problemTwo(tree3));
    }


}

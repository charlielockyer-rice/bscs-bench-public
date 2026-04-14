package edu.rice.comp322.trees;

import edu.rice.comp322.provided.trees.GList;
import edu.rice.comp322.provided.trees.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import edu.rice.comp322.provided.trees.generatetree.TreeGenerator;

import static edu.rice.comp322.solutions.TreeSolutions.*;

public class TreeSolutionsTestPrivate {

    private Tree<Integer> tree1 = TreeGenerator.generateIntTreeOne();
    private Tree<Integer> tree2 = TreeGenerator.generateIntTreeTwo();
    private Tree<Integer> tree3 = TreeGenerator.generateIntTreeThree();



    @Test
    public void problem3() {
        Assertions.assertEquals(GList.of(15, 5, 4, 17, 5, 4, 2, 23, 5, 4, 17, 5, 4, 2, 9, 0),
            tree3.fold(GList.empty(), (a, b) -> a.prepend(b)));
    }

    @Test
    public void problem4() {
        Assertions.assertEquals(156, problemFour(tree1));
        Assertions.assertEquals(2, problemFour(tree2));
        Assertions.assertEquals(121, problemFour(tree3));

    }
}

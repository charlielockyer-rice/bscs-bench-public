package test.rice.parse;

import main.rice.node.*;
import main.rice.obj.PyIntObj;
import main.rice.parse.ConfigFile;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the ConfigFile class.
 * Total: 1.5 points (3 tests)
 */
class ConfigFileTest {

    // Simple config file with basic types
    private static ConfigFile simpleConfig;
    private static List<APyNode<?>> simpleNodes;

    // Config file with nested types
    private static ConfigFile nestedConfig;
    private static List<APyNode<?>> nestedNodes;

    // Config file with empty nodes list
    private static ConfigFile emptyConfig;

    @BeforeAll
    static void setUp() {
        // Set up simple config with int and bool nodes
        PyIntNode intNode = new PyIntNode();
        intNode.setExDomain(Arrays.asList(1, 2, 3));
        intNode.setRanDomain(Arrays.asList(1, 2, 3, 4, 5));

        PyBoolNode boolNode = new PyBoolNode();
        boolNode.setExDomain(Arrays.asList(0, 1));
        boolNode.setRanDomain(Arrays.asList(0, 1));

        simpleNodes = Arrays.asList(intNode, boolNode);
        simpleConfig = new ConfigFile("testFunc", simpleNodes, 100);

        // Set up nested config with list of ints
        PyIntNode innerIntNode = new PyIntNode();
        innerIntNode.setExDomain(Arrays.asList(1, 2));
        innerIntNode.setRanDomain(Arrays.asList(1, 2, 3));

        PyListNode<PyIntObj> listNode = new PyListNode<>(innerIntNode);
        listNode.setExDomain(Arrays.asList(0, 1, 2));
        listNode.setRanDomain(Arrays.asList(0, 1, 2, 3));

        nestedNodes = Arrays.asList(listNode);
        nestedConfig = new ConfigFile("nestedFunc", nestedNodes, 50);

        // Set up empty config
        emptyConfig = new ConfigFile("emptyFunc", new ArrayList<>(), 0);
    }

    /**
     * Tests that getFuncName returns the correct function name.
     * (0.5 pts)
     */
    @Test
    void testGetFuncName() {
        assertEquals("testFunc", simpleConfig.getFuncName());
        assertEquals("nestedFunc", nestedConfig.getFuncName());
        assertEquals("emptyFunc", emptyConfig.getFuncName());
    }

    /**
     * Tests that getNumRand returns the correct number of random test cases.
     * (0.5 pts)
     */
}

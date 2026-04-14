package test.rice.parse;

import main.rice.node.*;
import main.rice.parse.ConfigFile;
import main.rice.parse.ConfigFileParser;
import main.rice.parse.InvalidConfigException;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the ConfigFileParser class.
 * Total: 71.5 points (~70 tests)
 */
class ConfigFileParserTestPrivate {

    private ConfigFileParser parser;

    @BeforeEach
    void setUp() {
        parser = new ConfigFileParser();
    }

    // ==================== ERROR HANDLING TESTS ====================

    /**
     * Tests that parsing non-JSON content throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testSpuriousSpaceRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1 ~ 5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious parenthesis at end of types throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testSpuriousParenEndTypes() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int))"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious parenthesis at end of exhaustive domain throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testSpuriousParenEndExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3))"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious parenthesis at end of random domain throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testSpuriousParenEndRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5))"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that missing parenthesis in types throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testMissingParenTypes() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that missing parenthesis in exhaustive domain throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testMissingParenExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2 1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that missing parenthesis in random domain throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testMissingParenRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3 1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious colon in types throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testSpuriousColonTypes() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int:)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious colon in exhaustive domain throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testSpuriousColonExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3:)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious colon in random domain throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testSpuriousColonRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5:)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious colon at end of types throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testSpuriousColonEndTypes() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int):"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious colon at end of exhaustive domain throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testSpuriousColonEndExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3):"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious colon at end of random domain throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testSpuriousColonEndRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5):"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    // ==================== DOMAIN VALIDATION TESTS ====================

    /**
     * Tests that lower bound exceeding upper bound in exhaustive domain throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testLowerBoundExceedsUpperBoundExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["5~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that lower bound exceeding upper bound in random domain throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testLowerBoundExceedsUpperBoundRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["10~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that non-integer range in exhaustive domain throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testNonIntegerRangeExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1.5~3.5"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that non-integer range in random domain throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testNonIntegerRangeRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1.5~5.5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that boolean value greater than 1 throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testBoolValGreaterThanOne() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["bool"],
                "exhaustive domain": ["[0, 1, 2]"],
                "random domain": ["[0, 1]"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that negative boolean value throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testBoolValNegative() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["bool"],
                "exhaustive domain": ["[-1, 0, 1]"],
                "random domain": ["[0, 1]"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that decimal value for integer throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testIntValDecimal() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["[1, 2.5, 3]"],
                "random domain": ["[1, 2, 3]"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    // ==================== DOMAIN LENGTH MISMATCH TESTS ====================

    /**
     * Tests that exhaustive domain being too short throws InvalidConfigException.
     * (1.0 pts)
     */
    @Test
    void testExDomainTooShort() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int", "bool"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5", "[0, 1]"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that exhaustive domain with missing internal element throws InvalidConfigException.
     * (1.0 pts)
     */
    @Test
    void testExDomainMissingInternal() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:bool)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5:[0, 1])"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that exhaustive domain being too long throws InvalidConfigException.
     * (1.0 pts)
     */
    @Test
    void testExDomainTooLong() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3", "[0, 1]"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that exhaustive domain with extra internal element throws InvalidConfigException.
     * (1.0 pts)
     */
    @Test
    void testExDomainExtraInternal() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3:[0, 1])"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that random domain being too short throws InvalidConfigException.
     * (1.0 pts)
     */
    @Test
    void testRanDomainTooShort() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int", "bool"],
                "exhaustive domain": ["1~3", "[0, 1]"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that random domain with missing internal element throws InvalidConfigException.
     * (1.0 pts)
     */
    @Test
    void testRanDomainMissingInternal() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:bool)"],
                "exhaustive domain": ["0~2(1~3:[0, 1])"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that random domain being too long throws InvalidConfigException.
     * (1.0 pts)
     */
    @Test
    void testRanDomainTooLong() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5", "[0, 1]"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that random domain with extra internal element throws InvalidConfigException.
     * (1.0 pts)
     */
    @Test
    void testRanDomainExtraInternal() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5:[0, 1])"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    // ==================== TYPE VALIDATION TESTS ====================

    /**
     * Tests that unexpected type throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testUnexpectedType() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["unknown"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that negative domain for dict throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testNegativeDomainDict() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:int)"],
                "exhaustive domain": ["-1~2(1~3:1~3)"],
                "random domain": ["0~3(1~5:1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that negative domain for list throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testNegativeDomainList() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["-1~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that negative domain for dict array throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testNegativeDomainDictArray() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:int)"],
                "exhaustive domain": ["[-1, 0, 1](1~3:1~3)"],
                "random domain": ["0~3(1~5:1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that negative domain for list array throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testNegativeDomainListArray() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["[-1, 0, 1](1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that float domain for int array throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testFloatDomainIntArray() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["[1.0, 2.0, 3.0]"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that float domain for dict array throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testFloatDomainDictArray() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:int)"],
                "exhaustive domain": ["[0.5, 1.5](1~3:1~3)"],
                "random domain": ["0~3(1~5:1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that float domain for list array throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testFloatDomainListArray() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["[0.5, 1.5](1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    // ==================== VALID PARSING TESTS ====================

    /**
     * Tests parsing num random correctly.
     * (0.5 pts)
     */
    @Test
    void testParseNumRandom() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 100
            }
            """;
        ConfigFile config = parser.parse(json);
        assertEquals(100, config.getNumRand());
    }

    /**
     * Tests parsing function name correctly.
     * (0.5 pts)
     */
    @Test
    void testParseFname() throws InvalidConfigException {
        String json = """
            {
                "fname": "mySpecialFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        assertEquals("mySpecialFunc", config.getFuncName());
    }

    /**
     * Tests parsing a single bool type.
     * (1.0 pts)
     */
    @Test
    void testParseTypesOneBool() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["bool"],
                "exhaustive domain": ["[0, 1]"],
                "random domain": ["[0, 1]"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyBoolNode);
    }

    /**
     * Tests parsing a single int type.
     * (1.0 pts)
     */
    @Test
    void testParseTypesOneInt() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~5"],
                "random domain": ["1~10"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyIntNode);
        // Check domains
        PyIntNode intNode = (PyIntNode) nodes.get(0);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), intNode.getExDomain());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), intNode.getRanDomain());
    }

    /**
     * Tests parsing a single float type.
     * (1.0 pts)
     */
    @Test
    void testParseTypesOneFloat() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["float"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyFloatNode);
    }

    /**
     * Tests parsing a single string type.
     * (1.5 pts)
     */
    @Test
    void testParseTypesOneString() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["str(abc)"],
                "exhaustive domain": ["0~2"],
                "random domain": ["0~3"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyStringNode);
    }

    /**
     * Tests parsing a single list type.
     * (1.5 pts)
     */
    @Test
    void testParseTypesOneList() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyListNode);
        // Check that the left child is an int node
        PyListNode<?> listNode = (PyListNode<?>) nodes.get(0);
        assertTrue(listNode.getLeftChild() instanceof PyIntNode);
    }

    /**
     * Tests parsing a single tuple type.
     * (1.5 pts)
     */
    @Test
    void testParseTypesOneTup() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["tuple(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyTupleNode);
    }

    /**
     * Tests parsing a single set type.
     * (1.5 pts)
     */
    @Test
    void testParseTypesOneSet() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["set(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PySetNode);
    }

    /**
     * Tests parsing a single dict type.
     * (2.0 pts)
     */
    @Test
    void testParseTypesOneDict() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:bool)"],
                "exhaustive domain": ["0~2(1~3:[0, 1])"],
                "random domain": ["0~3(1~5:[0, 1])"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyDictNode);
        // Check children
        PyDictNode<?, ?> dictNode = (PyDictNode<?, ?>) nodes.get(0);
        assertTrue(dictNode.getLeftChild() instanceof PyIntNode);
        assertTrue(dictNode.getRightChild() instanceof PyBoolNode);
    }

    /**
     * Tests parsing multiple simple types.
     * (1.0 pts)
     */
    @Test
    void testParseTypesMultipleSimple() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int", "bool", "float"],
                "exhaustive domain": ["1~3", "[0, 1]", "1~2"],
                "random domain": ["1~5", "[0, 1]", "1~3"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(3, nodes.size());
        assertTrue(nodes.get(0) instanceof PyIntNode);
        assertTrue(nodes.get(1) instanceof PyBoolNode);
        assertTrue(nodes.get(2) instanceof PyFloatNode);
    }

    /**
     * Tests parsing multiple nested types.
     * (2.0 pts)
     */
    @Test
    void testParseTypesMultipleNested() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)", "dict(int:bool)"],
                "exhaustive domain": ["0~2(1~3)", "0~2(1~3:[0, 1])"],
                "random domain": ["0~3(1~5)", "0~3(1~5:[0, 1])"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0) instanceof PyListNode);
        assertTrue(nodes.get(1) instanceof PyDictNode);
    }

    // ==================== EXHAUSTIVE DOMAIN PARSING TESTS ====================

    /**
     * Tests parsing exhaustive domain for one bool.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneBool() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["bool"],
                "exhaustive domain": ["[0, 1]"],
                "random domain": ["[0, 1]"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyBoolNode node = (PyBoolNode) config.getNodes().get(0);
        List<? extends Number> exDomain = node.getExDomain();
        assertTrue(exDomain.contains(0));
        assertTrue(exDomain.contains(1));
    }

    /**
     * Tests parsing exhaustive domain for one int.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneInt() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~5"],
                "random domain": ["1~10"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyIntNode node = (PyIntNode) config.getNodes().get(0);
        List<? extends Number> exDomain = node.getExDomain();
        assertEquals(5, exDomain.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), exDomain);
    }

    /**
     * Tests parsing exhaustive domain for one int with explicit values.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneIntExplicitVals() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["[1, 3, 5, 7]"],
                "random domain": ["1~10"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyIntNode node = (PyIntNode) config.getNodes().get(0);
        List<? extends Number> exDomain = node.getExDomain();
        assertEquals(4, exDomain.size());
        assertTrue(exDomain.containsAll(Arrays.asList(1, 3, 5, 7)));
    }

    /**
     * Tests parsing exhaustive domain for one float.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneFloat() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["float"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyFloatNode node = (PyFloatNode) config.getNodes().get(0);
        List<? extends Number> exDomain = node.getExDomain();
        assertEquals(3, exDomain.size());
    }

    /**
     * Tests parsing exhaustive domain for one float with explicit values.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneFloatExplicitVals() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["float"],
                "exhaustive domain": ["[1.5, 2.5, 3.5]"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyFloatNode node = (PyFloatNode) config.getNodes().get(0);
        List<? extends Number> exDomain = node.getExDomain();
        assertEquals(3, exDomain.size());
    }

    /**
     * Tests parsing exhaustive domain for one string.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneString() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["str(abc)"],
                "exhaustive domain": ["0~2"],
                "random domain": ["0~3"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyStringNode node = (PyStringNode) config.getNodes().get(0);
        List<? extends Number> exDomain = node.getExDomain();
        assertEquals(3, exDomain.size());
        assertEquals(Arrays.asList(0, 1, 2), exDomain);
    }

    /**
     * Tests parsing exhaustive domain for one list.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneList() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyListNode<?> node = (PyListNode<?>) config.getNodes().get(0);
        assertEquals(Arrays.asList(0, 1, 2), node.getExDomain());
        assertEquals(Arrays.asList(1, 2, 3), node.getLeftChild().getExDomain());
    }

    /**
     * Tests parsing exhaustive domain for one tuple.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneTup() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["tuple(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyTupleNode<?> node = (PyTupleNode<?>) config.getNodes().get(0);
        assertEquals(Arrays.asList(0, 1, 2), node.getExDomain());
    }

    /**
     * Tests parsing exhaustive domain for one set.
     * (1.0 pts)
     */
    @Test
    void testParseExDomainOneSet() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["set(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~3(1~5)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PySetNode<?> node = (PySetNode<?>) config.getNodes().get(0);
        assertEquals(Arrays.asList(0, 1, 2), node.getExDomain());
    }

    /**
     * Tests parsing exhaustive domain for one dict.
     * (2.0 pts)
     */
    @Test
    void testParseExDomainOneDict() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:bool)"],
                "exhaustive domain": ["0~2(1~3:[0, 1])"],
                "random domain": ["0~3(1~5:[0, 1])"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyDictNode<?, ?> node = (PyDictNode<?, ?>) config.getNodes().get(0);
        assertEquals(Arrays.asList(0, 1, 2), node.getExDomain());
        assertEquals(Arrays.asList(1, 2, 3), node.getLeftChild().getExDomain());
    }

    /**
     * Tests parsing exhaustive domain for multiple simple types.
     * (2.0 pts)
     */
    @Test
    void testParseExDomainMultipleSimple() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int", "bool"],
                "exhaustive domain": ["1~3", "[0, 1]"],
                "random domain": ["1~5", "[0, 1]"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(Arrays.asList(1, 2, 3), nodes.get(0).getExDomain());
    }

    /**
     * Tests parsing exhaustive domain for multiple nested types.
     * (2.0 pts)
     */
    @Test
    void testParseExDomainMultipleNested() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)", "set(bool)"],
                "exhaustive domain": ["0~2(1~3)", "0~1([0, 1])"],
                "random domain": ["0~3(1~5)", "0~2([0, 1])"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(2, nodes.size());
        assertEquals(Arrays.asList(0, 1, 2), nodes.get(0).getExDomain());
        assertEquals(Arrays.asList(0, 1), nodes.get(1).getExDomain());
    }

    // ==================== RANDOM DOMAIN PARSING TESTS ====================

    /**
     * Tests parsing random domain for one bool.
     * (0.5 pts)
     */
    @Test
    void testParseRanDomainOneBool() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["bool"],
                "exhaustive domain": ["[0, 1]"],
                "random domain": ["[0, 1]"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyBoolNode node = (PyBoolNode) config.getNodes().get(0);
        List<? extends Number> ranDomain = node.getRanDomain();
        assertTrue(ranDomain.contains(0));
        assertTrue(ranDomain.contains(1));
    }

    /**
     * Tests parsing random domain for one int.
     * (0.5 pts)
     */
    @Test
    void testParseRanDomainOneInt() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~10"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyIntNode node = (PyIntNode) config.getNodes().get(0);
        assertEquals(10, node.getRanDomain().size());
    }

    /**
     * Tests parsing random domain for one float.
     * (0.5 pts)
     */
    @Test
    void testParseRanDomainOneFloat() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["float"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyFloatNode node = (PyFloatNode) config.getNodes().get(0);
        assertEquals(5, node.getRanDomain().size());
    }

    /**
     * Tests parsing random domain for one string.
     * (1.0 pts)
     */
    @Test
    void testParseRanDomainOneString() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["str(abc)"],
                "exhaustive domain": ["0~2"],
                "random domain": ["0~5"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyStringNode node = (PyStringNode) config.getNodes().get(0);
        assertEquals(6, node.getRanDomain().size());
    }

    /**
     * Tests parsing random domain for one list.
     * (1.0 pts)
     */
    @Test
    void testParseRanDomainOneList() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~5(1~10)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyListNode<?> node = (PyListNode<?>) config.getNodes().get(0);
        assertEquals(6, node.getRanDomain().size());
        assertEquals(10, node.getLeftChild().getRanDomain().size());
    }

    /**
     * Tests parsing random domain for one list with explicit values.
     * (1.0 pts)
     */
    @Test
    void testParseRanDomainOneListExplicitVals() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["[0, 2, 4]([1, 3, 5])"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyListNode<?> node = (PyListNode<?>) config.getNodes().get(0);
        assertEquals(3, node.getRanDomain().size());
    }

    /**
     * Tests parsing random domain for one tuple.
     * (1.0 pts)
     */
    @Test
    void testParseRanDomainOneTup() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["tuple(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~5(1~10)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyTupleNode<?> node = (PyTupleNode<?>) config.getNodes().get(0);
        assertEquals(6, node.getRanDomain().size());
    }

    /**
     * Tests parsing random domain for one set.
     * (1.0 pts)
     */
    @Test
    void testParseRanDomainOneSet() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["set(int)"],
                "exhaustive domain": ["0~2(1~3)"],
                "random domain": ["0~5(1~10)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PySetNode<?> node = (PySetNode<?>) config.getNodes().get(0);
        assertEquals(6, node.getRanDomain().size());
    }

    /**
     * Tests parsing random domain for one dict.
     * (2.0 pts)
     */
    @Test
    void testParseRanDomainOneDict() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:bool)"],
                "exhaustive domain": ["0~2(1~3:[0, 1])"],
                "random domain": ["0~5(1~10:[0, 1])"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyDictNode<?, ?> node = (PyDictNode<?, ?>) config.getNodes().get(0);
        assertEquals(6, node.getRanDomain().size());
        assertEquals(10, node.getLeftChild().getRanDomain().size());
    }

    /**
     * Tests parsing random domain for multiple simple types.
     * (2.0 pts)
     */
    @Test
    void testParseRanDomainMultipleSimple() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int", "float"],
                "exhaustive domain": ["1~3", "1~2"],
                "random domain": ["1~10", "1~5"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(10, nodes.get(0).getRanDomain().size());
        assertEquals(5, nodes.get(1).getRanDomain().size());
    }

    /**
     * Tests parsing random domain for multiple nested types.
     * (2.0 pts)
     */
    @Test
    void testParseRanDomainMultipleNested() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)", "tuple(float)"],
                "exhaustive domain": ["0~2(1~3)", "0~2(1~3)"],
                "random domain": ["0~5(1~10)", "0~4(1~8)"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        List<APyNode<?>> nodes = config.getNodes();
        assertEquals(6, nodes.get(0).getRanDomain().size());
        assertEquals(5, nodes.get(1).getRanDomain().size());
    }

    /**
     * Tests that duplicate values in domain are removed.
     * (1.0 pts)
     */
    @Test
    void testParseRemoveDups() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["[1, 1, 2, 2, 3]"],
                "random domain": ["[1, 1, 2, 2, 3, 3]"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyIntNode node = (PyIntNode) config.getNodes().get(0);
        // Duplicates should be removed (using HashSet internally)
        assertEquals(3, node.getExDomain().size());
        assertEquals(3, node.getRanDomain().size());
    }

    /**
     * Tests parsing exhaustive domain for nested dicts.
     * (2.0 pts)
     */
    @Test
    void testParseExDomainNestedDicts() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict(int:dict(int:bool))"],
                "exhaustive domain": ["0~1(1~2:0~1(1~2:[0, 1]))"],
                "random domain": ["0~2(1~3:0~2(1~3:[0, 1]))"],
                "num random": 10
            }
            """;
        ConfigFile config = parser.parse(json);
        PyDictNode<?, ?> node = (PyDictNode<?, ?>) config.getNodes().get(0);
        assertEquals(Arrays.asList(0, 1), node.getExDomain());
        assertTrue(node.getRightChild() instanceof PyDictNode);
    }
}

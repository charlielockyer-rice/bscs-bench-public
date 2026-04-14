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
class ConfigFileParserTest {

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
    void testConfigNotJSON() {
        String notJSON = "this is not valid JSON";
        assertThrows(InvalidConfigException.class, () -> parser.parse(notJSON));
    }

    /**
     * Tests that missing fname field throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testMissingFname() {
        String json = """
            {
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that missing types field throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testMissingTypes() {
        String json = """
            {
                "fname": "testFunc",
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that missing exhaustive domain field throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testMissingExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that missing random domain field throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testMissingRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that missing num random field throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testMissingNumRand() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"]
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that fname not being a string throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testFnameNotString() {
        String json = """
            {
                "fname": 123,
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that types not being an array throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testTypesNotArray() {
        String json = """
            {
                "fname": "testFunc",
                "types": "int",
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that types not being an array of strings throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testTypesNotArrayOfStrings() {
        String json = """
            {
                "fname": "testFunc",
                "types": [1, 2, 3],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that exhaustive domain not being an array throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testExDomainNotArray() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": "1~3",
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that exhaustive domain not being an array of strings throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testExDomainNotArrayOfStrings() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": [1, 2, 3],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that random domain not being an array throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testRanDomainNotArray() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": "1~5",
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that random domain not being an array of strings throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testRanDomainNotArrayOfStrings() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": [1, 2, 3],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that num random not being an integer throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testNumRandNotInt() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": "ten"
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that negative num random throws InvalidConfigException.
     * (0.4 pts)
     */
    @Test
    void testNumRandNegative() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": -5
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    // ==================== SPURIOUS CHARACTER TESTS ====================

    /**
     * Tests that spurious parenthesis in types throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testSpuriousParenTypes() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int("],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious space in types throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testSpuriousSpaceTypes() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["i nt"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious parenthesis in exhaustive domain throws InvalidConfigException.
     * (0.3 pts)
     */
    @Test
    void testSpuriousParenExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2((1~3"],
                "random domain": ["0~3(1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious space in exhaustive domain throws InvalidConfigException.
     * (0.5 pts)
     */
    @Test
    void testSpuriousSpaceExDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["int"],
                "exhaustive domain": ["1 ~ 3"],
                "random domain": ["1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious parenthesis in random domain throws InvalidConfigException.
     * (0.2 pts)
     */
    @Test
    void testSpuriousParenRanDomain() {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~3"],
                "random domain": ["0~3((1~5"],
                "num random": 10
            }
            """;
        assertThrows(InvalidConfigException.class, () -> parser.parse(json));
    }

    /**
     * Tests that spurious space in random domain throws InvalidConfigException.
     * (0.5 pts)
     */

    // ==================== COMPOUND TYPE PARSING TESTS ====================

    /**
     * Tests parsing a list(bool) type with domain specification.
     * Type format: "list (bool" — note space before paren, no closing paren.
     * Domain format: "outer (inner" — e.g. "0~2 (0~1" means list sizes 0-2, bool values 0-1.
     * (1.0 pt)
     */
    @Test
    void testParseListType() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["list (bool"],
                "exhaustive domain": ["0~2 (0~1"],
                "random domain": ["3~5 (0~1"],
                "num random": 6
            }
            """;
        ConfigFile result = parser.parse(json);
        assertEquals("testFunc", result.getFuncName());
        assertEquals(6, result.getNumRand());

        List<APyNode<?>> nodes = result.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyListNode);
        assertTrue(nodes.get(0).getLeftChild() instanceof PyBoolNode);

        // Outer domain: list sizes 0,1,2
        assertEquals(List.of(0, 1, 2), nodes.get(0).getExDomain());
        assertEquals(List.of(3, 4, 5), nodes.get(0).getRanDomain());

        // Inner domain: bool values 0,1
        assertEquals(List.of(0, 1), nodes.get(0).getLeftChild().getExDomain());
        assertEquals(List.of(0, 1), nodes.get(0).getLeftChild().getRanDomain());
    }

    /**
     * Tests parsing a dict(int:float) type with domain specification.
     * Type format: "dict (int : float" — colon separates key and value types.
     * Domain format: "outer (key_domain : value_domain".
     * (1.0 pt)
     */
    @Test
    void testParseDictType() throws InvalidConfigException {
        String json = """
            {
                "fname": "testFunc",
                "types": ["dict (int : float"],
                "exhaustive domain": ["0~1 (0~0 : 1~5"],
                "random domain": ["0~2 (0~5 : -7~8"],
                "num random": 100
            }
            """;
        ConfigFile result = parser.parse(json);
        List<APyNode<?>> nodes = result.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof PyDictNode);
        assertTrue(nodes.get(0).getLeftChild() instanceof PyIntNode);
        assertTrue(nodes.get(0).getRightChild() instanceof PyFloatNode);
    }
}

"""
Test script to verify all COMP 140 module imports work correctly.
Run this to validate the library setup.
"""

import sys
import os

# Add lib directory to path
lib_path = os.path.join(os.path.dirname(__file__), 'lib')
sys.path.insert(0, lib_path)

def test_imports():
    """Test that all modules can be imported."""
    modules_to_test = [
        # CodeSkulptor stubs
        ('simplegui', 'GUI framework stub'),
        ('simplemap', 'Google Maps stub'),
        ('simpleplot', 'Plotting stub'),
        ('codeskulptor', 'CodeSkulptor utilities'),
        ('numeric', 'Matrix operations'),

        # Course helper modules
        ('comp140_module1', 'Circle calculation'),
        ('comp140_module2', 'Spot It! game'),
        ('comp140_module3', 'Stock analysis'),
        ('comp140_module4', 'Kevin Bacon game'),
        ('comp140_module4_graphs', 'Undirected Graph'),
        ('comp140_module5', 'QR Code generator'),
        ('comp140_module5_z256', 'Z256 arithmetic'),
        ('comp140_module6', 'Sports Analytics'),
        ('comp140_module7', 'Map Search'),
        ('comp140_module7_graphs', 'Directed Graph'),
    ]

    print("Testing COMP 140 module imports...\n")
    success_count = 0
    fail_count = 0

    for module_name, description in modules_to_test:
        try:
            module = __import__(module_name)
            print(f"  [OK] {module_name}: {description}")
            success_count += 1
        except Exception as e:
            print(f"  [FAIL] {module_name}: {e}")
            fail_count += 1

    print(f"\nResults: {success_count} passed, {fail_count} failed")
    return fail_count == 0


def test_numeric():
    """Test basic numeric module operations."""
    print("\nTesting numeric module operations...")
    import numeric

    # Test matrix creation
    m = numeric.Matrix([[1, 2], [3, 4]])
    print(f"  Created 2x2 matrix: shape = {m.shape()}")

    # Test matrix multiplication
    m2 = m * m
    print(f"  Matrix squared: {m2[0, 0]}, {m2[0, 1]}, {m2[1, 0]}, {m2[1, 1]}")

    # Test transpose
    mt = m.transpose()
    print(f"  Transpose: shape = {mt.shape()}")

    # Test inverse
    mi = m.inverse()
    print(f"  Inverse computed successfully")

    print("  [OK] Numeric module tests passed")


def test_graphs():
    """Test graph implementations."""
    print("\nTesting graph modules...")

    # Test undirected graph
    import comp140_module4_graphs as graphs4
    g = graphs4.Graph()
    g.add_node('A')
    g.add_node('B')
    g.add_edge('A', 'B', {'weight': 1})
    print(f"  Undirected graph: nodes = {g.nodes()}")
    print(f"  Neighbors of A: {g.get_neighbors('A')}")

    # Test directed graph
    import comp140_module7_graphs as graphs7
    dg = graphs7.DiGraph()
    dg.add_edge('X', 'Y')
    dg.add_node_attr('X', 'value', 10)
    dg.add_edge_attr('X', 'Y', 'weight', 5)
    print(f"  Directed graph: nodes = {dg.nodes()}")
    print(f"  X->Y edge weight: {dg.get_edge_attr('X', 'Y', 'weight')}")

    print("  [OK] Graph module tests passed")


def test_z256():
    """Test Z256 arithmetic."""
    print("\nTesting Z256 arithmetic...")
    import comp140_module5_z256 as z256

    # Test add (XOR)
    result = z256.add(10, 5)
    print(f"  10 + 5 in Z256 = {result}")

    # Test mul
    result = z256.mul(3, 7)
    print(f"  3 * 7 in Z256 = {result}")

    # Test div
    result = z256.div(21, 7)
    print(f"  21 / 7 in Z256 = {result}")

    print("  [OK] Z256 module tests passed")


if __name__ == "__main__":
    all_passed = test_imports()

    if all_passed:
        test_numeric()
        test_graphs()
        test_z256()
        print("\n=== All tests passed! ===")
    else:
        print("\n=== Some imports failed. Check error messages above. ===")
        sys.exit(1)

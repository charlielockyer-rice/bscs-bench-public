"""
YAML Parsing Utilities

Simple YAML parsing without external dependencies for configuration files.
"""


def parse_simple_yaml(content: str) -> dict:
    """Parse simple YAML without external dependencies.

    Supports:
    - Top-level key-value pairs (strings, numbers)
    - Nested dictionaries (one level deep)
    - Lists of strings

    Args:
        content: YAML content as a string

    Returns:
        Parsed dictionary
    """
    result = {}
    current_key = None
    current_list = None
    current_dict = None

    for line in content.split('\n'):
        # Skip comments and empty lines
        if not line.strip() or line.strip().startswith('#'):
            continue

        # Check indent level
        indent = len(line) - len(line.lstrip())

        # Key: value pairs
        if ':' in line and not line.strip().startswith('-'):
            key, _, value = line.partition(':')
            key = key.strip()
            value = value.strip()
            # Strip inline comments (but preserve # inside quoted strings)
            if not (value.startswith('"') or value.startswith("'")):
                if '#' in value:
                    value = value.split('#')[0].strip()

            if indent == 0:
                # Top-level key
                if value:
                    # Simple value
                    if value.startswith('"') and value.endswith('"'):
                        result[key] = value[1:-1]
                    elif value.replace('.', '').replace('-', '').isdigit():
                        result[key] = float(value) if '.' in value else int(value)
                    else:
                        result[key] = value
                    current_key = None
                    current_dict = None
                    current_list = None
                else:
                    # Start of a list or dict
                    current_key = key
                    result[key] = None
                    current_list = None
                    current_dict = None
            elif current_key:
                # Nested key-value in a dict
                if result[current_key] is None:
                    result[current_key] = {}
                    current_dict = result[current_key]
                if current_dict is not None:
                    if value.replace('.', '').replace('-', '').isdigit():
                        current_dict[key] = float(value) if '.' in value else int(value)
                    elif value.startswith('"') and value.endswith('"'):
                        current_dict[key] = value[1:-1]
                    else:
                        current_dict[key] = value

        elif line.strip().startswith('- '):
            # List item
            if current_key and result.get(current_key) is None:
                result[current_key] = []
                current_list = result[current_key]
            if current_list is not None:
                value = line.strip()[2:].strip()
                if value.startswith('"') and value.endswith('"'):
                    value = value[1:-1]
                current_list.append(value)

    return result

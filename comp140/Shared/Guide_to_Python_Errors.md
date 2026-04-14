# Guide to Python Errors (Resource)

This guide helps to explain some common Python errors by translating them into English. You may not have seen all of the Python constructs described in the errors, but you should look over these so that you have an idea what might be going wrong when you see these errors. You should also refer back to this page whenever you see an error you don't understand. While this is not a complete or comprehensive list of all possible causes of these errors, it covers many common situations you might encounter.

Note that CodeSkulptor uses "syntax highlighting" in the editor. This can help you locate errors. Python keywords, function names, strings, etc. are colored differently. This can sometimes enable you to quickly see problems with your code. Get used to the highlighting and use it to help you debug what you are typing!
### SyntaxError:*"I don't recognize what you just wrote as Python code."*

'Syntax' refers to the rules that dictate how you're allowed to write programs in a particular programming language. For example, the rule that states that you may not begin a variable name with a digit is part of the*syntax*of Python. A`SyntaxError`will pop up if you violate any of Python's syntax rules. This could be forgetting to close a parenthesis or quote, mistakes in indentation, forgetting colons after function headers, etc. Note that syntax errors can be tricky - sometimes the real error is actually before or after the line that the error is reported on. When you go hunting for syntax errors, try looking at the entire block of code that surrounds the line where the error appears.
- Example 1:`SyntaxError`: bad input (' ') -[https://py3.codeskulptor.org/#examples3_errors_syntax1.py](https://py3.codeskulptor.org/#examples3_errors_syntax1.py)
- Example 2:`SyntaxError`: bad token('"') -[https://py3.codeskulptor.org/#examples3_errors_syntax2.py](https://py3.codeskulptor.org/#examples3_errors_syntax2.py)
- Example 3:`SyntaxError`: bad input('print') -[https://py3.codeskulptor.org/#examples3_errors_syntax3.py](https://py3.codeskulptor.org/#examples3_errors_syntax3.py)
- Example 4:`SyntaxError`: 'return' outside function -[https://py3.codeskulptor.org/#examples3_errors_syntax4.py](https://py3.codeskulptor.org/#examples3_errors_syntax4.py)
- Example 5:`SyntaxError`: can't assign to literal -[https://py3.codeskulptor.org/#examples3_errors_syntax5.py](https://py3.codeskulptor.org/#examples3_errors_syntax5.py)
- Example 6:`IndendationError`: unindent does not match any outer indentation -[https://py3.codeskulptor.org/#examples3_errors_syntax6.py](https://py3.codeskulptor.org/#examples3_errors_syntax6.py)
- Example 7:`SyntaxError`: bad input('=') -[https://py3.codeskulptor.org/#examples3_errors_syntax7.py](https://py3.codeskulptor.org/#examples3_errors_syntax7.py)

### NameError:*"What in the world is X?"*

A`NameError`is triggered when you use a variable, function name, etc. that has not been defined. A`NameError`is Python's way of saying "I've never heard of that before!" Common causes of this are misspellings or forgetting to declare variables entirely.
- Example 1:`NameError`: name 'some_variable_name' is not defined -[https://py3.codeskulptor.org/#examples3_errors_name1.py](https://py3.codeskulptor.org/#examples3_errors_name1.py)
- Example 2:`NameError`: name 'some_function_name' is not defined -[https://py3.codeskulptor.org/#examples3_errors_name2.py](https://py3.codeskulptor.org/#examples3_errors_name2.py)
- Example 3:`NameError`: name 'some_module_name' is not defined -[https://py3.codeskulptor.org/#examples3_errors_name3.py](https://py3.codeskulptor.org/#examples3_errors_name3.py)

### TypeError:*"You're trying to use some of those things in a way that wasn't intended."*

This error can come up when you're trying to do perform an operation on something, but the operation and the object just don't go together. One example is trying to multiply a dictionary and an integer - it just won't work. A helpful tool in debugging type errors is the Python`type()`function. Adding print statements like`print(type(my_variable))`might help you figure out what's going wrong.
- Example 1:`TypeError`: cannot concatenate 'str' and 'int' objects (or any other data type):[https://py3.codeskulptor.org/#examples3_errors_type1.py](https://py3.codeskulptor.org/#examples3_errors_type1.py)
- Example 2:`TypeError`: function_name() takes exactly x arguments (y given) -[https://py3.codeskulptor.org/#examples3_errors_type2.py](https://py3.codeskulptor.org/#examples3_errors_type2.py)
- Example 3:`TypeError`: 'int' object is not iterable (could be a different data type) -[https://py3.codeskulptor.org/#examples3_errors_type3.py](https://py3.codeskulptor.org/#examples3_errors_type3.py)
- Example 4:`TypeError`: 'int' object is not callable (could be a different data type) -[https://py3.codeskulptor.org/#examples3_errors_type4.py](https://py3.codeskulptor.org/#examples3_errors_type4.py)
- Example 5:`TypeError`: handler must be a function -[https://py3.codeskulptor.org/#examples3_errors_type5.py](https://py3.codeskulptor.org/#examples3_errors_type5.py)

### AttributeError:*"That object doesn't know how do to what you asked it to do"*

In Python, objects have "attributes" - things that they're aware of and/or know how to do. In Python, attributes are an object's properties and the methods defined by its class. An AttributeError will be thrown when you ask an object to do something or access something that isn't in its class definition.
- Example 1:`AttributeError`: '[Object X] has no attribute [attribute Y] -[https://py3.codeskulptor.org/#examples3_errors_attribute1.py](https://py3.codeskulptor.org/#examples3_errors_attribute1.py)
- Example 2: Same as Example 1, but with an explicit class definition -[https://py3.codeskulptor.org/#examples3_errors_attribute2.py](https://py3.codeskulptor.org/#examples3_errors_attribute2.py)
- Example 3:`AttributeError`: '[module] has no attribute [attribute Y] -[https://py3.codeskulptor.org/#examples3_errors_attribute3.py](https://py3.codeskulptor.org/#examples3_errors_attribute3.py)

### IndexError:*"That list/dictionary/tuple (etc.) doesn't have that many items in it."*

An`IndexError`happens when you try to access an index that doesn't actually exist. It's like telling someone to take 13 eggs out of a 12-egg carton - it won't work, because there are only 12 spaces. Printing out your index values, along with`len(the_list_in_question)`will help you in debugging these errors. Important to note: negative indices**are**possible in Python! See the video lecture on lists for more information.
- Example 1:`IndexError`: list index out of range -[https://py3.codeskulptor.org/#examples3_errors_index1.py](https://py3.codeskulptor.org/#examples3_errors_index1.py)

### TokenError:*"You probably forgot to close a bracket." **

Very simply, tokens are things that stand for other things - sort of like variables, except they're used at a more structural level. Some examples of tokens are EOF (End Of File) and EOL (End Of Line). These are the two most common ones you will come across in Python, but they are used everywhere in programming. The`TokenError`s that you will see in this course are usually solved by remembering to close your brackets. See the example for a more in-depth explanation of why this is.
- Example 1:`TokenError`: EOF in multi-line statement -[https://py3.codeskulptor.org/#examples3_errors_token1.py](https://py3.codeskulptor.org/#examples3_errors_token1.py)

*Not actually what it means - but a more detailed explanation is more appropriate for other courses, and this is the most common cause of`TokenError`s in this class.
### ValueError:*"There's something wrong with the value of one of those arguments (but the type is ok)."*

A`ValueError`is raised when a function receives an argument that looks ok on the surface (e.g., it receives a string, and it was expecting a string), but the value of that argument is unexpected (e.g., the function was only built to handle digits, and it received a letter 'a'). This type of error can be solved by checking the documentation for whatever function you're trying to use, and making sure that whatever you put inside the parentheses, the function was built to handle it.
- Example 1:`ValueError`: invalid literal for int() with base 10: ' ' -[https://py3.codeskulptor.org/#examples3_errors_value1.py](https://py3.codeskulptor.org/#examples3_errors_value1.py)

### IndentationError:*"Your code blocks aren't all indented to the proper levels."*

This is a type of`SyntaxError`. See Syntax Errors, example # 6.
### Miscellaneous Errors

These errors are either self-explanatory or not common in the level of programming done in this class.
- `OverflowError`- caused by trying to store, for example, a long inside an int. A long has too many bytes to fit inside the int data type
- `ZeroDivisionError`- you guessed it - you're trying to divide by zero somewhere
- `ImportError`- caused by trying to import a module that doesn't eimport mathxist. Check your spelling.
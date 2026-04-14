# COMP 310: Advanced Object-Oriented Programming - LLM Grading Guidance

## Course Context

COMP 310 (Advanced Object-Oriented Programming) at Rice University teaches
advanced OOP design patterns through a progressive Ballworld project in Java.
The course emphasizes:

- Design patterns: Union, Abstract Inheritance, Strategy, Command/Visitor,
  Interaction, Extended Visitor
- Model-View-Controller architecture
- Composition over inheritance
- Interface-based programming and separation of concerns
- The Visitor pattern as a fundamental tool for extensible, type-safe dispatch

## Grading Philosophy

This course values **design quality** over mere functionality. When grading
written responses and design documents, evaluate:

1. **Pattern understanding**: Does the student demonstrate comprehension of the
   design pattern's purpose, not just its mechanics? Can they explain *why* a
   pattern is used, not just *how*?

2. **Abstraction quality**: Are abstractions meaningful and well-bounded? Does
   the student identify invariant vs. variant aspects correctly?

3. **Separation of concerns**: Is the MVC architecture respected? Are model,
   view, and controller responsibilities clearly delineated?

4. **Extensibility**: Does the design allow new behaviors/types without modifying
   existing code (Open-Closed Principle)?

5. **Composition**: Does the student prefer composition over inheritance where
   appropriate (Strategy over subclassing)?

## Assignment-Specific Guidance

### ChatApp API Design (HW07)

This is the primary LLM-graded assignment. Evaluate the design document for:

**Architecture (25 points)**
- Clear separation of connection layer (discovery, room management) from
  communication layer (in-room messaging)
- Justified reasoning for the two-layer split
- Appropriate use of Remote vs. Serializable interfaces
- Clean package organization

**Interface Design (25 points)**
- Complete specification of required interfaces (IConnection, INamedConnection,
  IInitialConnection, IRoom, IMessageReceiver, INamedMessageReceiver)
- Well-defined method signatures with clear semantics
- Appropriate use of generics (DataPacket<T, S>)
- NULL object patterns and error handling interfaces

**Design Patterns (25 points)**
- **Visitor**: Correct use of DataPacket + Algorithm for type-keyed dispatch.
  Must explain how unknown message types are handled (request command from sender).
- **Strategy**: Clear articulation of how different routing strategies work.
- **Observer**: Room membership as observer registration/deregistration.

**Message Flow and Extensibility (25 points)**
- Complete message flow diagrams/descriptions for all required scenarios
- Clear protocol for handling unknown message types (key extensibility mechanism)
- Error handling for connection failures, rejected requests, invalid messages
- Demonstration that new message types can be added without modifying existing code

### Scoring Guidelines

- **90-100**: Comprehensive, well-structured design with excellent pattern
  application. Clear extensibility story. Professional-quality API documentation.
- **75-89**: Solid design with good pattern understanding. Minor gaps in
  completeness or extensibility explanation.
- **60-74**: Basic design present but with significant gaps. Pattern application
  is mechanical rather than principled.
- **40-59**: Incomplete design. Patterns are misapplied or missing. Poor
  separation of concerns.
- **0-39**: Minimal or no meaningful design work.

## Common Pitfalls to Watch For

- Confusing the connection layer with the communication layer
- Not explaining how unknown message types are handled (the core extensibility
  mechanism of the Visitor pattern in this context)
- Using inheritance where composition (Strategy pattern) is more appropriate
- Not separating remote (RMI stub) interfaces from serializable wrapper interfaces
- Missing error handling / exception message types
- Treating the Visitor pattern as simple method dispatch rather than understanding
  its role in enabling extensibility

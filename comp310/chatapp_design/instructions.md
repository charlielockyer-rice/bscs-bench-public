# ChatApp API Design - Multi-User Chat Application

## Overview

Design the network API for a multi-user chat application that allows
interoperability between independently developed chat clients. The API must
define shared interfaces in a `common` package that all implementations use to
communicate. Two implementations built against the same API should be able to
connect and chat with each other.

This is a **design-only** assignment. You do not write implementation code.
Instead, you design and document the API interfaces, message types, and
communication protocols in `writeup.md`.

## Design Patterns to Apply

### 1. Visitor Pattern for Message Processing

Messages are typed data packets processed by a visitor (algorithm). The system
must handle both known and unknown message types:

- **DataPacket**: A generic container `DataPacket<T, S>` where `T` is the message
  type and `S` is the sender type.
- **Visitor/Algorithm**: Processes data packets with type-keyed dispatch.
  Known message types have dedicated handlers; unknown types trigger a default
  case that requests the processing command from the sender.
- **Extensibility**: New message types can be added without modifying existing
  code. When a receiver gets an unknown message type, it asks the sender for
  the appropriate processing command (`ISendCmdMsg` / `IRequestCmdMsg`).

### 2. Strategy Pattern for Message Routing

Different rooms or channels can use different routing strategies:

- **Direct messaging**: Point-to-point between two users
- **Broadcast**: Message sent to all room members
- **Custom routing**: Game rooms, voting, etc. can define custom dispatch

### 3. Observer Pattern for Notifications

Room membership and message delivery use the Observer pattern:

- Joining a room registers the user as an observer
- Messages dispatched to all observers in the room
- Leaving a room unregisters the observer

## API Layers

The API must define two communication layers:

### Connection Layer (`common.connector`)

Handles initial discovery, connection establishment, and room management.
Operates between application-level entities (not yet in a chat room).

Required interfaces:
- **`IConnection`** (Remote): Receives connection-level messages. Method:
  `receiveMessage(ConnectionDataPacket<? extends IConnectionMsg> packet)`
- **`INamedConnection`** (Serializable): Wraps `IConnection` with a friendly
  name for display purposes
- **`IInitialConnection`** (Remote): Discovery interface for initial contact.
  Provides methods to get the user's `INamedConnection`
- **`IRoom`** (Serializable): Identifies a chat room by name and UUID
- **`ConnectionDataPacket<T>`**: Type-narrowed DataPacket for connection messages
- **`ConnectionDataPacketAlgo`**: Visitor for processing connection data packets

Connection message types (`common.connector.messageType`):
- `IConnectionMsg` - base marker interface
- `IInviteMsg` - invitation to join a room
- `IRequestJoinMsg` - request to join a room
- `IQuitMsg` - notification of disconnection
- `IRequestRoomsMsg` - request list of available rooms
- `ISendRoomsMsg` - response with room list
- `ISendConnectionsMsg` - share connection stubs for room members
- Exception types: `IConnectionExceptionMsg`, error/failure/rejection subtypes

### Communication Layer (`common.receiver`)

Handles message exchange within a chat room. Operates between room members.

Required interfaces:
- **`IMessageReceiver`** (Remote): Receives room-level messages. Method:
  `receiveMessage(MessageDataPacket<? extends ICommunicationMsg> packet)`
- **`INamedMessageReceiver`** (Serializable): Wraps `IMessageReceiver` with a
  friendly name
- **`MessageDataPacket<T>`**: Type-narrowed DataPacket for room messages
- **`MessageDataPacketAlgo`**: Visitor for processing room data packets

Communication message types (`common.receiver.messageType`):
- `ICommunicationMsg` - base marker interface
- `ITextMsg` - plain text message
- `IRequestCmdMsg` - request processing command for unknown message type
- `ISendCmdMsg` - response with processing command (an `AMessageDataPacketAlgoCmd`)
- Exception types: `ICommunicationExceptionMsg`, error/failure/rejection subtypes

### Visitor Commands

- **`AConnectionDataPacketAlgoCmd`**: Abstract command for processing connection
  data packets. Holds an `ICmd2ModelAdapter` for accessing model services.
- **`AMessageDataPacketAlgoCmd`**: Abstract command for processing room messages.
  Holds an `ICmd2ModelAdapter` for accessing model services (display text,
  show UI components, etc.).
- **`ICmd2ModelAdapter`**: Adapter giving commands access to the local model
  (e.g., display text, add UI component, access scroll pane).

## What to Produce

Write your design in `writeup.md` covering:

1. **API Architecture**: Explain the two-layer design (connection vs. communication)
   and justify why they are separate.

2. **Interface Specifications**: For each interface, describe:
   - Purpose and responsibility
   - Methods with signatures and semantics
   - How it fits into the overall design

3. **Message Flow**: Describe the sequence of messages for:
   - Initial connection between two users
   - Joining a chat room
   - Sending a text message in a room
   - Handling an unknown message type (requesting/sending commands)
   - Leaving a room / disconnecting

4. **Design Pattern Application**: Explain how each pattern is used:
   - Visitor: DataPacket processing with type-keyed dispatch
   - Strategy: Room-level message routing
   - Observer: Room membership and message delivery

5. **Extensibility**: How does the API support adding new message types,
   new room behaviors, and new UI components without modifying existing code?

6. **Error Handling**: How are connection failures, invalid messages, and
   rejected requests handled?

## Testing

This is an LLM-graded assignment. Submit your design:

```
Use the `grade` tool to run the grading evaluation.
```

The grading evaluates:
- Completeness of API specification
- Correct application of design patterns (Visitor, Strategy, Observer)
- Separation of concerns between connection and communication layers
- Extensibility for unknown message types
- Clarity of message flow descriptions
- Error handling design

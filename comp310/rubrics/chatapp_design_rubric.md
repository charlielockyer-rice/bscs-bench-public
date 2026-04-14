# ChatApp API Design Rubric

**Total Points: 100**

## 1. API Architecture (25 points)

### Two-Layer Design (10 points)
- **10**: Clear, well-justified separation of connection layer (discovery,
  initial contact, room management) from communication layer (in-room messaging).
  Explains why the layers are separate and what each handles.
- **7**: Both layers described but separation rationale is weak or unclear.
- **4**: Layers mentioned but poorly differentiated; responsibilities overlap.
- **0**: No layer separation or single monolithic design.

### Interface Organization (8 points)
- **8**: Clean package structure (common.connector, common.receiver). Appropriate
  use of Remote (for RMI stubs) vs. Serializable (for transferable wrappers).
  Named wrappers (INamedConnection, INamedMessageReceiver) explained.
- **5**: Basic organization present. Some confusion about Remote vs. Serializable.
- **2**: Minimal organization. Interfaces lumped together or misclassified.
- **0**: No meaningful interface organization.

### Data Packet Design (7 points)
- **7**: Generic DataPacket<T, S> design with type-narrowed variants
  (ConnectionDataPacket, MessageDataPacket). Explains sender typing and message
  typing. Shows how generics enable compile-time safety.
- **4**: Data packets described but generic design is incomplete or unclear.
- **2**: Basic message containers without proper generic typing.
- **0**: No data packet design.

## 2. Interface Specifications (25 points)

### Connection Interfaces (10 points)
- **10**: Complete specification of IConnection, INamedConnection,
  IInitialConnection, IRoom. Each interface has defined methods with clear
  signatures and semantics. IInitialConnection explains discovery mechanism.
- **7**: Most interfaces specified but missing 1-2 methods or unclear semantics.
- **4**: Partial specification. Major interfaces missing or underspecified.
- **0**: No connection interface specifications.

### Communication Interfaces (10 points)
- **10**: Complete specification of IMessageReceiver, INamedMessageReceiver,
  MessageDataPacket, MessageDataPacketAlgo. Method semantics clear.
- **7**: Most interfaces specified but gaps in detail.
- **4**: Partial specification with major omissions.
- **0**: No communication interface specifications.

### Message Types (5 points)
- **5**: All required message types defined for both layers:
  - Connection: IInviteMsg, IRequestJoinMsg, IQuitMsg, IRequestRoomsMsg,
    ISendRoomsMsg, ISendConnectionsMsg, exception types
  - Communication: ITextMsg, IRequestCmdMsg, ISendCmdMsg, exception types
- **3**: Most message types present but some missing or poorly defined.
- **1**: Few message types defined.
- **0**: No message type definitions.

## 3. Design Pattern Application (25 points)

### Visitor Pattern (12 points)
- **12**: Excellent explanation of DataPacket + Algorithm visitor dispatch.
  Correctly explains: known types have registered handlers; unknown types
  trigger default case that requests command from sender (IRequestCmdMsg /
  ISendCmdMsg). Shows how this enables adding new message types without
  modifying existing code. Describes AMessageDataPacketAlgoCmd and
  AConnectionDataPacketAlgoCmd as visitor commands with ICmd2ModelAdapter.
- **9**: Good visitor explanation but missing either unknown-type handling
  or command adapter explanation.
- **6**: Basic visitor pattern described but significant gaps in extensibility
  mechanism.
- **3**: Visitor pattern mentioned but misapplied or poorly explained.
- **0**: No visitor pattern application.

### Strategy Pattern (6 points)
- **6**: Clear articulation of Strategy pattern for message routing or room
  behavior. Shows how different rooms can have different dispatch strategies
  without subclassing. May include examples like game rooms, voting rooms.
- **4**: Strategy pattern described but application is limited or unclear.
- **2**: Strategy pattern mentioned but not meaningfully applied.
- **0**: No strategy pattern application.

### Observer Pattern (7 points)
- **7**: Clear explanation of Observer for room membership. Joining a room =
  registering as observer. Messages dispatched to all observers. Leaving =
  deregistering. Explains notification mechanism for room state changes.
- **5**: Observer pattern applied but explanation is incomplete.
- **2**: Observer pattern mentioned but poorly connected to room membership.
- **0**: No observer pattern application.

## 4. Message Flow and Extensibility (25 points)

### Message Flow Descriptions (12 points)
- **12**: Complete, step-by-step descriptions for all required scenarios:
  1. Initial connection between two users
  2. Joining a chat room
  3. Sending a text message
  4. Handling an unknown message type
  5. Leaving a room / disconnecting
  Each scenario clearly identifies which messages are sent, by whom, to whom,
  and what happens at each step.
- **9**: Most scenarios covered but 1-2 are missing or incomplete.
- **6**: Partial coverage. Major scenarios missing or lacking detail.
- **3**: Minimal message flow descriptions.
- **0**: No message flow descriptions.

### Extensibility (8 points)
- **8**: Clear demonstration that new message types, room behaviors, and UI
  components can be added without modifying existing code. Explains the
  IRequestCmdMsg/ISendCmdMsg protocol as the key extensibility mechanism.
  Shows how a receiver can dynamically learn to process new message types.
- **5**: Extensibility addressed but explanation is incomplete or overly abstract.
- **2**: Extensibility mentioned but no concrete mechanism described.
- **0**: No extensibility discussion.

### Error Handling (5 points)
- **5**: Comprehensive error handling design. Defines exception message types
  for both layers (connection and communication). Addresses connection failures,
  invalid messages, rejected requests, timeout handling.
- **3**: Basic error handling present but incomplete.
- **1**: Minimal error handling mentioned.
- **0**: No error handling design.

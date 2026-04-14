# ChatApp API Design

## 1. API Architecture

<!-- Explain the two-layer design (connection layer vs. communication layer) and why they are separate. -->

## 2. Interface Specifications

### Connection Layer

<!-- For each interface (IConnection, INamedConnection, IInitialConnection, IRoom, etc.), describe:
- Purpose and responsibility
- Methods with signatures and semantics
- How it fits into the overall design
-->

### Communication Layer

<!-- For each interface (IMessageReceiver, INamedMessageReceiver, etc.), describe:
- Purpose and responsibility
- Methods with signatures and semantics
- How it fits into the overall design
-->

### Message Types

<!-- Define all connection and communication message types. -->

### Visitor Commands

<!-- Describe AConnectionDataPacketAlgoCmd, AMessageDataPacketAlgoCmd, ICmd2ModelAdapter -->

## 3. Message Flow

### Initial Connection

<!-- Describe the sequence of messages when two users first connect. -->

### Joining a Chat Room

<!-- Describe how a user joins an existing chat room. -->

### Sending a Text Message

<!-- Describe how a text message flows from sender to all room members. -->

### Handling Unknown Message Types

<!-- Describe the IRequestCmdMsg / ISendCmdMsg protocol for unknown types. -->

### Leaving a Room / Disconnecting

<!-- Describe graceful disconnect and room leave sequences. -->

## 4. Design Pattern Application

### Visitor Pattern

<!-- Explain how DataPacket processing uses type-keyed visitor dispatch. -->

### Strategy Pattern

<!-- Explain how message routing uses the Strategy pattern. -->

### Observer Pattern

<!-- Explain how room membership and message delivery use Observer. -->

## 5. Extensibility

<!-- How does the API support new message types, room behaviors, and UI components? -->

## 6. Error Handling

<!-- How are connection failures, invalid messages, and rejections handled? -->

This repository demonstrates **publish–subscribe communication** using Java, the [Eclipse Paho MQTT client](https://eclipse.dev/paho/), and the public MQTT broker at `broker.hivemq.com`. It is a small software architecture example for CSC 3100.

## Learning objectives

After exploring the example, you should be able to:

- Explain the roles of a **publisher**, **subscriber**, **broker**, and **topic**.
- Describe how MQTT decouples message producers from consumers.
- Implement MQTT communication using the Eclipse Paho library.
- Recognize callbacks as an application of event-driven programming and the Observer concept.
- Read a UML class and connect its relationships to Java source code.

## Architecture

```text
Publisher ── publish(topic, message) ──▶ MQTT Broker
                                             │
                                             └── deliver to subscribed topics ──▶ Subscriber
```

The publisher and subscriber communicate **through the broker**, not directly with each other. The broker forwards messages according to topic subscriptions. In this demonstration, both clients run as separate threads within one Java application; they could also run in separate applications or on different computers.

## Project structure

```text
pom.xml
src/main/java/
├── Main.java
├── Publisher.java
└── Subscriber.java
```

| Class | Responsibility |
| --- | --- |
| `Main` | Entry point: configures the broker and topics, then starts publisher and subscriber threads. |
| `Publisher` | Implements `Runnable`; connects to the broker and publishes ten messages to each configured topic. |
| `Subscriber` | Implements `Runnable` and `MqttCallback`; subscribes to topics and prints received messages in `messageArrived(...)`. |

## Blueprint

The UML class diagram illustrates the structure of the MQTT application, including its classes, interfaces, attributes, methods, and relationships.

[View the UML Class Diagram](DIAGRAM.md)

The diagram follows the CSC 3100 UML conventions:

- **Yellow:** Application classes developed in this repository.
- **Blue:** Java SDK and external library classes and interfaces.
- **Solid arrows:** Associations.
- **Dashed lines with hollow triangles:** Interface realizations.


## Requirements

- **JDK 20** (the current `pom.xml` targets Java 20; update the compiler settings if you intend to use another version).
- **Apache Maven**.
- Internet access to `broker.hivemq.com` on TCP port **1883**.

### Maven dependency

The project already declares Eclipse Paho in `pom.xml`:

```xml
<dependency>
    <groupId>org.eclipse.paho</groupId>
    <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
    <version>1.2.5</version>
</dependency>
```

## Build and run

From the repository root, compile with Maven:

```bash
mvn compile
```

Run the `Main` entry point with Maven's Exec plugin:

```bash
mvn org.codehaus.mojo:exec-maven-plugin:3.5.1:java -Dexec.mainClass=Main
```

The first command downloads dependencies and compiles the code. The second runs the demonstration with Maven-managed dependencies on the classpath. Stop the application with **Ctrl+C**; the current subscriber loop otherwise keeps running.

> The current Java sources use the default package, so the entry point is `Main`, not a package-qualified class name.

## Broker and topics

- **Broker:** `tcp://broker.hivemq.com:1883`
- **Topic 1:** `example/position/x`
- **Topic 2:** `example/position/y`
- **QoS:** `2`

The topics carry the x and y coordinates of a simulated point. The publisher sends the same integer (`0` through `9`) to both topics once per second, so the point moves diagonally. The subscriber listens to both topics by default. The subscriber prints the topic, QoS, and message content as each message arrives.

Example output (the order of messages can vary):

```text
📥 Connected to broker: tcp://broker.hivemq.com:1883
📥 Subscribed to topic: example/position/x
📥 Subscribed to topic: example/position/y
↗️ Connected to broker: tcp://broker.hivemq.com:1883
↗️ published to example/position/x: 0
📥 Delivery :: [example/position/x : 2] :: 0
↗️ published to example/position/y: 0
📥 Delivery :: [example/position/y : 2] :: 0
```

Because publisher and subscriber start concurrently, the subscriber may not be ready before the first message is published. Missing the earliest messages is possible in the current example.

## Understanding the design

- **`Runnable`** allows `Publisher` and `Subscriber` to execute concurrently in threads started by `Main`.
- **`MqttCallback`** defines methods that Paho calls when messages arrive or a connection is lost.
- **`client.setCallback(this)`** registers the `Subscriber` object as the callback handler.
- **`messageArrived(String topic, MqttMessage message)`** is invoked when a subscribed message is delivered.
- **QoS 2** requests exactly-once delivery at the MQTT protocol level for the relevant delivery flow; it does not guarantee that a subscriber receives messages sent before its subscription is active.

## Try it yourself

1. In `Main`, pass only the x topic to `Subscriber` and leave both topics configured for `Publisher`. Predict which messages will appear in the subscriber output, then run the demo to check.
2. Change the publisher's QoS and compare the output.
3. Add another subscriber and observe how both subscribers receive publications.
4. Run publisher and subscriber as independent applications rather than threads in one application.
5. Update the UML diagram after changing the Java design.

## Public broker precautions

`broker.hivemq.com` is a public test service. **Never publish passwords, private data, or other sensitive information.** Other clients can publish to the same topics. For classroom experiments, replace the example topic names with unique names such as `example/<random-suffix>/position/x` and `example/<random-suffix>/position/y`.

If no messages appear, verify internet access, TCP port 1883, and broker availability. Also check whether the subscriber finished subscribing before the publisher sent its first message.

## Current limitations

This is an introductory demonstration, not a production-ready MQTT client. The subscriber runs until interrupted, the initial publisher/subscriber startup is not coordinated, and reconnection and graceful shutdown are not implemented. These are opportunities for subsequent refactoring and architectural discussion.

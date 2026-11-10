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
- **Topic 1:** `example/a1b2c3d4e5f6/position/x`
- **Topic 2:** `example/a1b2c3d4e5f6/position/y`
- **QoS:** `2`

The topics carry the x and y coordinates of a simulated point. The publisher sends the same integer (`0` through `9`) to both topics once per second, so the point moves diagonally. The subscriber listens to both topics by default. The subscriber prints the topic, QoS, and message content as each message arrives.

Each run generates a random topic prefix and prints both topic names before connecting. The suffix below is an example.

```text
Run topics: example/a1b2c3d4e5f6/position/x, example/a1b2c3d4e5f6/position/y
📥 Connected to broker: tcp://broker.hivemq.com:1883
📥 Subscribed to topic: example/a1b2c3d4e5f6/position/x
📥 Subscribed to topic: example/a1b2c3d4e5f6/position/y
↗️ Connected to broker: tcp://broker.hivemq.com:1883
↗️ published to example/a1b2c3d4e5f6/position/x: 0
📥 subscriber-1 :: [example/a1b2c3d4e5f6/position/x : 2] :: 0
↗️ published to example/a1b2c3d4e5f6/position/y: 0
📥 subscriber-1 :: [example/a1b2c3d4e5f6/position/y : 2] :: 0
```

The publisher starts only after the broker acknowledges the subscriber's subscriptions. If setup fails, the demo reports the failure and skips publishing.

## Follow one message

Follow the first x-coordinate value through the Java code and the broker.

1. **Subscribe.** In `Subscriber.run()`, the client subscribes to the run's x and y topics. `Main` waits for the broker to acknowledge both subscriptions before starting the publisher.
2. **Publish.** In `Publisher.run()`, the counter starts at zero. The publisher encodes the text `0` as UTF-8, puts those bytes in an `MqttMessage`, and calls `client.publish(topic, message)` with the x topic.
3. **Route.** The broker matches the x topic to the subscriber's subscription and forwards the message. The publisher only needs the topic name to send it.
4. **Receive.** Paho calls `Subscriber.messageArrived(topic, message)`. The callback decodes the payload bytes as UTF-8 and prints the subscriber name, topic, QoS, and value.

```text
📥 subscriber-1 :: [example/a1b2c3d4e5f6/position/x : 2] :: 0
```

Here, `position/x` identifies the coordinate, `2` is the MQTT quality-of-service level, and `0` is the coordinate value. The middle part of the topic changes on each run.

## Understanding the design

- **`Runnable`** allows `Publisher` and `Subscriber` to execute concurrently in threads started by `Main`.
- **`MqttCallback`** defines methods that Paho calls when messages arrive or a connection is lost.
- **`client.setCallback(this)`** registers the `Subscriber` object as the callback handler.
- **`messageArrived(String topic, MqttMessage message)`** is invoked when a subscribed message is delivered.
- **QoS 2** requests exactly-once delivery at the MQTT protocol level for the relevant delivery flow; it does not guarantee that a subscriber receives messages sent before its subscription is active.

## Try it yourself

1. In `Main`, pass only the x topic to `Subscriber` and leave both topics configured for `Publisher`. Predict which messages will appear in the subscriber output, then run the demo to check.
2. Keep the first subscriber listening to x only. Add a second `Subscriber` for the same x topic and start it in a thread named `subscriber-2`. Call `awaitSubscriptions()` on both subscribers before starting the publisher. Predict whether they will each receive every x value or split the values, then compare the output lines for the two subscribers.
3. Change the publisher's QoS and compare the output.
4. Run publisher and subscriber as independent applications rather than threads in one application.
5. Update the UML diagram after changing the Java design.

## Public broker precautions

`broker.hivemq.com` is a public test service. **Never publish passwords, private data, or other sensitive information.** Other clients can publish to the same topics. The random suffix helps keep classroom runs separate. Use the same printed topic name if you deliberately want another MQTT client to listen.

If no messages appear, verify internet access, TCP port 1883, and broker availability.

## Current limitations

This is an introductory demonstration, not a production-ready MQTT client. The subscriber runs until interrupted, and reconnection and graceful shutdown are not implemented. These are opportunities for subsequent refactoring and architectural discussion.

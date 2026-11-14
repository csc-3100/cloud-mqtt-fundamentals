# MQTT Fundamentals — Java

A minimal [Eclipse Paho](https://eclipse.dev/paho/) example of **publish–subscribe communication** through an MQTT broker. The companion article explains the concepts: [IoT Fundamentals: Streaming Data with MQTT in Java](https://medium.com/javarevisited/streaming-iot-sensor-data-with-java-and-mqtt-1355712e1233?source=friends_link&sk=6cb4b9327d8a60d1706289dc90afa281) (free access via Medium Friend Link).

## How it works

```text
PositionMessage → Publisher → MQTT Broker → Subscriber → PositionMessage
   Java object       bytes                          bytes       Java object
```

- `Main` configures the broker and topic, starts the subscriber, then starts the publisher.
- `Publisher` sends ten position updates as MQTT messages.
- `Subscriber` implements `MqttCallback` and receives each update in `messageArrived(...)`.
- `PositionMessage` holds `x` and `y`, encoding and decoding the UTF-8 payload bytes.

See the [UML class diagram](DIAGRAM.md).

## Run

Requires **Java 17+**, **Maven**, and an internet connection.

```bash
mvn compile exec:java
```

The example uses:

- **Broker:** `tcp://broker.hivemq.com:1883`
- **Topic:** `example/<random-id>/position` (printed when the application starts)
- **Payload:** `x,y` encoded as UTF-8 bytes, for example `3,3`
- **QoS:** 2

The subscriber connects and subscribes before the publisher begins. Both run in separate threads in the same application. Stop listening with **Ctrl+C**.

This is a public test broker: do not publish private or sensitive data. The example demonstrates basic messaging, not production-ready reconnection or security.

# MQTT Fundamentals — Java

A small Java example of **MQTT publish–subscribe**, with two ways to see the *same* position messages: the terminal and a Swing grid (Brokerverse). Both use [Eclipse Paho](https://eclipse.dev/paho/) and the same broker, topic, and message format.

For the detailed introduction, read [IoT Fundamentals: Streaming Data with MQTT in Java](https://medium.com/javarevisited/streaming-iot-sensor-data-with-java-and-mqtt-1355712e1233?source=friends_link&sk=6cb4b9327d8a60d1706289dc90afa281) (free-access Medium Friend Link). The article's code predates this unified example; the article will be aligned in a separate step.

## Run

Requires **Java 17+**, **Maven**, and internet access. In one terminal, start Brokerverse:

```bash
mvn compile exec:java -Dexec.mainClass=Brokerverse -Dexec.args="myroom alice"
```

In another terminal, run the console example in the **same room**:

```bash
mvn compile exec:java -Dexec.mainClass=Main -Dexec.args="myroom console1"
```

The console publishes ten positions and exits. Brokerverse displays incoming positions and publishes its own as you move the **arrow keys**. Run a second Brokerverse with the same room and a different player ID to watch both players move. Close the GUI window to disconnect.

## Design

- **`Main`** — console entry point.
- **`Brokerverse` / `WorldPanel`** — Swing entry point and visualization.
- **`Publisher`** — turns a `PositionMessage` into an MQTT publication.
- **`Subscriber`** — implements `MqttCallback` and converts incoming bytes into a `PositionMessage`.
- **`PositionMessage`** — immutable `playerId`, `x`, `y`; UTF-8 `toPayload()` and `fromPayload()`.
- **`MqttConfig`** — shared broker, QoS, and room-based topic configuration.

[UML class diagram](DIAGRAM.md)

## MQTT settings

- **Broker:** `tcp://broker.hivemq.com:1883`
- **Topic:** `csc3100/mqtt-fundamentals/<room>/position`
- **Payload:** UTF-8 text `playerId,x,y` (example: `alice,400,300`)
- **QoS:** 2

The default room is `demo` when no arguments are supplied, but using a distinctive room name helps avoid interference with other people on the public broker. Choose distinct player IDs for each instance.

This is an **educational demo**, not a secure or fault-tolerant service. Do not publish sensitive data to a public broker. Disconnect/reconnect, message retention, and production error handling are beyond its scope.

## Test

```bash
mvn test
```

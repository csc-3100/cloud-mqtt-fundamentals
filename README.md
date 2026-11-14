# MQTT Fundamentals — Java

A small [Eclipse Paho](https://eclipse.dev/paho/) example of **publish–subscribe communication**, with two ways to see the same position data: a console demonstration and an optional Swing GUI (Brokerverse).

## Example 1 — Console

The introductory application consists of just four Java classes:

- **`Main`** — selects the broker/topic and starts the two clients.
- **`Publisher`** — encodes a `Parcel` and sends it to the MQTT broker.
- **`Subscriber`** — implements Paho's `MqttCallback`; receives a `MqttMessage` and reconstructs a `Parcel`.
- **`Parcel`** — application data (`playerId`, `x`, `y`) with `toPayload()` and `fromPayload()`.

```text
Parcel → Publisher → MQTT broker → Subscriber → Parcel
         (encode)                      (decode)
```

With **Java 17+**, **Maven**, and internet access, run:

```bash
mvn compile exec:java -Dexec.mainClass=io.github.csc3100.mqtt.Main -Dexec.args="myroom console1"
```

The publisher sends ten positions; the subscriber prints them in its MQTT callback.

## Example 2 — Brokerverse GUI

The GUI lives in a **separate `brokerverse` package**, using exactly the same `Parcel`, `Publisher`, and `Subscriber` as the console example.

Start the GUI in one terminal:

```bash
mvn compile exec:java -Dexec.mainClass=io.github.csc3100.mqtt.brokerverse.Brokerverse -Dexec.args="myroom alice"
```

Run the console command above in another terminal, using the **same room**. Positions from the console should appear on the GUI grid. Use the **arrow keys** in Brokerverse to send its position updates, or run a second GUI with the same room and a different player ID. Close the window to disconnect.

## MQTT settings

- **Broker:** `tcp://broker.hivemq.com:1883`
- **Topic:** `csc3100/mqtt-fundamentals/<room>/position`
- **Payload:** UTF-8 `playerId,x,y` (for example, `alice,400,300`)
- **QoS:** 2

The default room is `demo`. Prefer a unique room to avoid collisions on this **public test broker**, and never send confidential data. This example is not production-ready MQTT infrastructure.

[UML class diagrams](DIAGRAM.md) · [Run unit tests](pom.xml)

## Article

For the detailed introduction, read [IoT Fundamentals: Streaming Data with MQTT in Java](https://medium.com/javarevisited/streaming-iot-sensor-data-with-java-and-mqtt-1355712e1233?source=friends_link&sk=6cb4b9327d8a60d1706289dc90afa281) (free-access Medium Friend Link).

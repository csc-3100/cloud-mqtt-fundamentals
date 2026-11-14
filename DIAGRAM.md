# UML class diagrams

## 1. MQTT fundamentals — console

The four application classes are shown in yellow. External Eclipse Paho types are blue.

```mermaid
classDiagram
    direction TB
    class Main {
        +BROKER_URL$ String
        +QOS$ int
        +topicForRoom(String)$ String
        +main(String[] args)$ void
    }
    class Parcel {
        -String playerId
        -int x
        -int y
        +Parcel(String, int, int)
        +getPlayerId() String
        +getX() int
        +getY() int
        +toPayload() byte[]
        +fromPayload(byte[])$ Parcel
    }
    class Publisher {
        -MqttClient client
        +connect() void
        +publish(Parcel) void
        +close() void
    }
    class Subscriber {
        -MqttClient client
        +connect() void
        +messageArrived(String, MqttMessage) void
        +connectionLost(Throwable) void
        +deliveryComplete(IMqttDeliveryToken) void
        +close() void
    }
    class MqttCallback {
        <<interface>>
        +messageArrived(String, MqttMessage) void
        +connectionLost(Throwable) void
        +deliveryComplete(IMqttDeliveryToken) void
    }
    class MqttClient {
        +connect() void
        +subscribe(String, int) void
        +publish(String, MqttMessage) void
    }

    Main --> Publisher
    Main --> Subscriber
    Publisher --> Parcel : encodes
    Subscriber --> Parcel : decodes
    Publisher --> MqttClient
    Subscriber --> MqttClient
    Subscriber ..|> MqttCallback

    style Main fill:#FFF2B3,stroke:#555555,color:#222222
    style Publisher fill:#FFF2B3,stroke:#555555,color:#222222
    style Subscriber fill:#FFF2B3,stroke:#555555,color:#222222
    style Parcel fill:#FFF2B3,stroke:#555555,color:#222222
    style MqttCallback fill:#DCEEFF,stroke:#555555,color:#222222
    style MqttClient fill:#DCEEFF,stroke:#555555,color:#222222
```

## 2. Brokerverse — graphical extension

The GUI classes reuse the same three communication/data classes; the MQTT protocol is unchanged.

```mermaid
classDiagram
    direction LR
    class Brokerverse {
        +main(String[] args)$ void
    }
    class WorldPanel {
        +receive(Parcel) void
        +publishCurrentPosition() void
    }
    class Publisher {
        +publish(Parcel) void
    }
    class Subscriber {
        +messageArrived(String, MqttMessage) void
    }
    class Parcel {
        +toPayload() byte[]
        +fromPayload(byte[])$ Parcel
    }

    Brokerverse --> WorldPanel
    Brokerverse --> Publisher
    Brokerverse --> Subscriber
    WorldPanel --> Parcel
    Publisher --> Parcel
    Subscriber --> Parcel

    style Brokerverse fill:#FFF2B3,stroke:#555555,color:#222222
    style WorldPanel fill:#FFF2B3,stroke:#555555,color:#222222
    style Publisher fill:#FFF2B3,stroke:#555555,color:#222222
    style Subscriber fill:#FFF2B3,stroke:#555555,color:#222222
    style Parcel fill:#FFF2B3,stroke:#555555,color:#222222
```

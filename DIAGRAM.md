
```mermaid
---
config:
  class:
    diagramPadding: 60
---
classDiagram
    direction TB

    class Main {
        -String BROKER_URL
        +main(String[] args)$ void
    }

    class Publisher {
        -String broker
        -String[] topics
        +Publisher(String brokerUrl, String[] topics)
        +run() void
    }

    class Subscriber {
        -String broker
        -String[] topics
        +Subscriber(String brokerUrl, String[] topics)
        +awaitSubscriptions() boolean
        +run() void
        +connectionLost(Throwable cause) void
        +messageArrived(String topic, MqttMessage message) void
        +deliveryComplete(IMqttDeliveryToken token) void
    }

    class Runnable {
        <<interface>>
        +run() void
    }

    class MqttCallback {
        <<interface>>
        +connectionLost(Throwable cause) void
        +messageArrived(String topic, MqttMessage message) void
        +deliveryComplete(IMqttDeliveryToken token) void
    }

    class MqttClient {
        +connect() void
        +setCallback(MqttCallback callback) void
        +subscribe(String topic, int qos) void
        +publish(String topic, MqttMessage message) void
        +disconnect() void
    }

    %% Associations
    Main --> Publisher
    Main --> Subscriber

    %% Runnable implemented by both classes
    Publisher ..|> Runnable
    Subscriber ..|> Runnable

    %% MQTT dependencies
    Publisher --> MqttClient
    Subscriber --> MqttClient

    %% MQTT callback interface
    Subscriber ..|> MqttCallback


    %% CSC 3100 UML colors

    style Main fill:#FFF2B3,stroke:#555555,color:#222222
    style Publisher fill:#FFF2B3,stroke:#555555,color:#222222
    style Subscriber fill:#FFF2B3,stroke:#555555,color:#222222

    style Runnable fill:#DCEEFF,stroke:#555555,color:#222222
    style MqttCallback fill:#DCEEFF,stroke:#555555,color:#222222
    style MqttClient fill:#DCEEFF,stroke:#555555,color:#222222
```



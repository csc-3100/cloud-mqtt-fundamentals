package io.github.csc3100.mqtt;

import java.util.UUID;

/**
 * Console example: publish ten positions and print incoming messages.
 *
 * Usage: Main [room] [playerId]
 */
public final class Main {
    public static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
    public static final int QOS = 2;
    public static final String DEFAULT_ROOM = "demo";

    public static String topicForRoom(String room) {
        if (room == null || !room.matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Room must contain 1-40 letters, numbers, '-' or '_'.");
        }
        return "csc3100/mqtt-fundamentals/" + room + "/position";
    }

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        String room = args.length > 0 ? args[0] : DEFAULT_ROOM;
        String playerId = args.length > 1 ? args[1] :
                "console-" + UUID.randomUUID().toString().substring(0, 8);
        String topic = topicForRoom(room);

        System.out.println("Broker: " + BROKER_URL);
        System.out.println("Topic: " + topic);

        try (Subscriber subscriber = new Subscriber(BROKER_URL, topic,
                position -> System.out.println("Received: " + position));
             Publisher publisher = new Publisher(BROKER_URL, topic)) {
            subscriber.connect(); // Subscribe before publishing.
            publisher.connect();

            for (int i = 0; i < 10; i++) {
                publisher.publish(new Parcel(playerId, 40 + i * 20, 60 + i * 20));
                Thread.sleep(1000);
            }
        }
    }
}

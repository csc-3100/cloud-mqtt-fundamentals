import java.util.UUID;

/**
 * Console example: publish ten positions and print incoming messages.
 *
 * Usage: Main [room] [playerId]
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        String room = args.length > 0 ? args[0] : MqttConfig.DEFAULT_ROOM;
        String playerId = args.length > 1 ? args[1] :
                "console-" + UUID.randomUUID().toString().substring(0, 8);
        String topic = MqttConfig.topicForRoom(room);

        System.out.println("Broker: " + MqttConfig.BROKER_URL);
        System.out.println("Topic: " + topic);

        try (Subscriber subscriber = new Subscriber(MqttConfig.BROKER_URL, topic,
                position -> System.out.println("Received: " + position));
             Publisher publisher = new Publisher(MqttConfig.BROKER_URL, topic)) {
            subscriber.connect(); // Subscribe before publishing.
            publisher.connect();

            for (int i = 0; i < 10; i++) {
                publisher.publish(new PositionMessage(playerId, 40 + i * 20, 60 + i * 20));
                Thread.sleep(1000);
            }
        }
    }
}

/**
 * Settings shared by the console and graphical MQTT examples.
 */
public final class MqttConfig {
    public static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
    public static final int QOS = 2;
    public static final String DEFAULT_ROOM = "demo";

    private MqttConfig() {
    }

    public static String topicForRoom(String room) {
        if (room == null || !room.matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Room must contain 1-40 letters, numbers, '-' or '_'.");
        }
        return "csc3100/mqtt-fundamentals/" + room + "/position";
    }
}

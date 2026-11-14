import java.nio.charset.StandardCharsets;

/**
 * A position update and its MQTT payload representation.
 *
 * MQTT transports bytes. This class converts between a Java object
 * and two comma-separated integers encoded as UTF-8 bytes.
 */
public final class PositionMessage {

    private final int x;
    private final int y;

    public PositionMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public byte[] toPayload() {
        return (x + "," + y).getBytes(StandardCharsets.UTF_8);
    }

    public static PositionMessage fromPayload(byte[] payload) {
        String text = new String(payload, StandardCharsets.UTF_8);
        String[] values = text.split(",", -1);
        if (values.length != 2) {
            throw new IllegalArgumentException("Expected two coordinates: x,y");
        }
        try {
            return new PositionMessage(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be integers: " + text, e);
        }
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }
}

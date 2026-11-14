package io.github.csc3100.mqtt;

import java.nio.charset.StandardCharsets;

/**
 * A parcel carrying a sender's position, encoded as an MQTT payload.
 *
 * Wire format: playerId,x,y encoded as UTF-8 (for example, alice,40,60).
 */
public final class Parcel {
    private final String playerId;
    private final int x;
    private final int y;

    public Parcel(String playerId, int x, int y) {
        if (playerId == null || !playerId.matches("[A-Za-z0-9_-]{1,32}")) {
            throw new IllegalArgumentException("Player ID must contain 1-32 letters, numbers, '-' or '_'.");
        }
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public byte[] toPayload() {
        return (playerId + "," + x + "," + y).getBytes(StandardCharsets.UTF_8);
    }

    public static Parcel fromPayload(byte[] payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null.");
        }
        String[] parts = new String(payload, StandardCharsets.UTF_8).split(",", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected playerId,x,y.");
        }
        try {
            return new Parcel(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be integers.", e);
        }
    }

    @Override
    public String toString() {
        return playerId + " at (" + x + ", " + y + ")";
    }
}

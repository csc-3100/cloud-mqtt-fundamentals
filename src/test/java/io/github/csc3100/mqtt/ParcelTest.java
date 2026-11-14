package io.github.csc3100.mqtt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ParcelTest {
    @Test
    void encodesPayloadAsUtf8() {
        Parcel position = new Parcel("alice", 40, 60);
        assertArrayEquals("alice,40,60".getBytes(StandardCharsets.UTF_8), position.toPayload());
    }

    @Test
    void decodesPayloadIntoPosition() {
        Parcel position =
                Parcel.fromPayload("bob,-20,300".getBytes(StandardCharsets.UTF_8));
        assertEquals("bob", position.getPlayerId());
        assertEquals(-20, position.getX());
        assertEquals(300, position.getY());
    }

    @Test
    void rejectsMalformedPayloads() {
        assertThrows(IllegalArgumentException.class,
                () -> Parcel.fromPayload("bob,10".getBytes(StandardCharsets.UTF_8)));
        assertThrows(IllegalArgumentException.class,
                () -> Parcel.fromPayload("bob,abc,20".getBytes(StandardCharsets.UTF_8)));
        assertThrows(IllegalArgumentException.class,
                () -> new Parcel("bad,id", 10, 20));
    }
}

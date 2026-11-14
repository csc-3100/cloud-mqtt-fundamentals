import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class PositionUpdateTest {
    @Test
    void encodesPayloadAsUtf8() {
        PositionUpdate position = new PositionUpdate("alice", 40, 60);
        assertArrayEquals("alice,40,60".getBytes(StandardCharsets.UTF_8), position.toPayload());
    }

    @Test
    void decodesPayloadIntoPosition() {
        PositionUpdate position =
                PositionUpdate.fromPayload("bob,-20,300".getBytes(StandardCharsets.UTF_8));
        assertEquals("bob", position.getPlayerId());
        assertEquals(-20, position.getX());
        assertEquals(300, position.getY());
    }

    @Test
    void rejectsMalformedPayloads() {
        assertThrows(IllegalArgumentException.class,
                () -> PositionUpdate.fromPayload("bob,10".getBytes(StandardCharsets.UTF_8)));
        assertThrows(IllegalArgumentException.class,
                () -> PositionUpdate.fromPayload("bob,abc,20".getBytes(StandardCharsets.UTF_8)));
        assertThrows(IllegalArgumentException.class,
                () -> new PositionUpdate("bad,id", 10, 20));
    }
}

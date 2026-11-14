import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class PositionMessageTest {
    @Test
    void encodesPayloadAsUtf8() {
        PositionMessage position = new PositionMessage("alice", 40, 60);
        assertArrayEquals("alice,40,60".getBytes(StandardCharsets.UTF_8), position.toPayload());
    }

    @Test
    void decodesPayloadIntoPosition() {
        PositionMessage position =
                PositionMessage.fromPayload("bob,-20,300".getBytes(StandardCharsets.UTF_8));
        assertEquals("bob", position.getPlayerId());
        assertEquals(-20, position.getX());
        assertEquals(300, position.getY());
    }

    @Test
    void rejectsMalformedPayloads() {
        assertThrows(IllegalArgumentException.class,
                () -> PositionMessage.fromPayload("bob,10".getBytes(StandardCharsets.UTF_8)));
        assertThrows(IllegalArgumentException.class,
                () -> PositionMessage.fromPayload("bob,abc,20".getBytes(StandardCharsets.UTF_8)));
        assertThrows(IllegalArgumentException.class,
                () -> new PositionMessage("bad,id", 10, 20));
    }
}

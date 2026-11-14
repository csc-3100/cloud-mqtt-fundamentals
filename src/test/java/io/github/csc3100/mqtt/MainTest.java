package io.github.csc3100.mqtt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void buildsRoomSpecificTopic() {
        assertEquals("csc3100/mqtt-fundamentals/class_1/position",
                Main.topicForRoom("class_1"));
    }

    @Test
    void rejectsWildcardsAndEmptyRooms() {
        assertThrows(IllegalArgumentException.class, () -> Main.topicForRoom(""));
        assertThrows(IllegalArgumentException.class, () -> Main.topicForRoom("room/#"));
    }
}

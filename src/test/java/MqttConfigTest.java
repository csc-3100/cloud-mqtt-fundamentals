import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MqttConfigTest {
    @Test
    void buildsRoomSpecificTopic() {
        assertEquals("csc3100/mqtt-fundamentals/class_1/position",
                MqttConfig.topicForRoom("class_1"));
    }

    @Test
    void rejectsWildcardsAndEmptyRooms() {
        assertThrows(IllegalArgumentException.class, () -> MqttConfig.topicForRoom(""));
        assertThrows(IllegalArgumentException.class, () -> MqttConfig.topicForRoom("room/#"));
    }
}

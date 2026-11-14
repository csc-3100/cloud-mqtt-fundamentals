import java.util.Objects;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Receives messages using Eclipse Paho's callback/listener interface.
 *
 * @author javiergs
 */
public final class Subscriber implements MqttCallback, AutoCloseable {
    private final String topic;
    private final MqttClient client;
    private final Consumer<PositionMessage> onPosition;

    public Subscriber(String brokerUrl, String topic, Consumer<PositionMessage> onPosition)
            throws MqttException {
        this.topic = topic;
        this.onPosition = Objects.requireNonNull(onPosition);
        this.client = new MqttClient(brokerUrl, MqttClient.generateClientId());
        this.client.setCallback(this);
    }

    public void connect() throws MqttException {
        client.connect();
        client.subscribe(topic, MqttConfig.QOS);
        System.out.println("Subscribed to: " + topic);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            onPosition.accept(PositionMessage.fromPayload(message.getPayload()));
        } catch (IllegalArgumentException e) {
            System.err.println("Ignored invalid position message: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("Subscriber connection lost: " +
                (cause == null ? "unknown cause" : cause.getMessage()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // The subscriber does not publish messages.
    }

    @Override
    public void close() throws MqttException {
        try {
            if (client.isConnected()) {
                client.disconnect();
            }
        } finally {
            client.close();
        }
    }
}

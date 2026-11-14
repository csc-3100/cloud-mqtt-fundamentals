import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Publishes position updates to a topic through an MQTT broker.
 *
 * @author javiergs
 * @version 2.0
 */
public class Publisher implements Runnable {

    private final String brokerUrl;
    private final String topic;

    public Publisher(String brokerUrl, String topic) {
        this.brokerUrl = brokerUrl;
        this.topic = topic;
    }

    @Override
    public void run() {
        MqttClient client = null;
        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            client.connect();
            System.out.println("Publisher connected.");

            for (int step = 0; step < 10; step++) {
                PositionMessage position = new PositionMessage(step, step);
                MqttMessage message = new MqttMessage(position.toPayload());
                message.setQos(2);

                client.publish(topic, message);
                System.out.println("Published: " + position);
                Thread.sleep(1000);
            }
        } catch (MqttException e) {
            System.err.println("Publisher MQTT error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (client != null && client.isConnected()) {
                try {
                    client.disconnect();
                } catch (MqttException e) {
                    System.err.println("Publisher disconnect error: " + e.getMessage());
                }
            }
        }
    }
}

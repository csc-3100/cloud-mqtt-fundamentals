import java.util.concurrent.CompletableFuture;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Listens for position messages using the MQTT callback interface.
 *
 * @author javiergs
 * @version 2.0
 */
public class Subscriber implements Runnable, MqttCallback {

    private final String brokerUrl;
    private final String topic;
    private final CompletableFuture<Boolean> subscriptionReady = new CompletableFuture<>();

    public Subscriber(String brokerUrl, String topic) {
        this.brokerUrl = brokerUrl;
        this.topic = topic;
    }

    public boolean awaitSubscription() {
        return subscriptionReady.join();
    }

    @Override
    public void run() {
        MqttClient client = null;
        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            client.setCallback(this);
            client.connect();
            client.subscribe(topic, 2);
            System.out.println("Subscriber listening on: " + topic);
            subscriptionReady.complete(true);

            // Keep the client alive so Paho can invoke messageArrived.
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
            }
        } catch (MqttException e) {
            System.err.println("Subscriber MQTT error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            subscriptionReady.complete(false);
            if (client != null && client.isConnected()) {
                try {
                    client.disconnect();
                } catch (MqttException e) {
                    System.err.println("Subscriber disconnect error: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("Subscriber connection lost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            PositionMessage position = PositionMessage.fromPayload(message.getPayload());
            System.out.println("Received (" + topic + ", QoS " + message.getQos() + "): " + position);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid position payload: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Required by MqttCallback; this client only subscribes.
    }
}

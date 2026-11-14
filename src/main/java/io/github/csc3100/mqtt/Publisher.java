package io.github.csc3100.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Sends position updates to a shared MQTT topic.
 *
 * @author javiergs
 */
public final class Publisher implements AutoCloseable {
    private final String topic;
    private final MqttClient client;

    public Publisher(String brokerUrl, String topic) throws MqttException {
        this.topic = topic;
        this.client = new MqttClient(brokerUrl, MqttClient.generateClientId());
    }

    public void connect() throws MqttException {
        client.connect();
    }

    public void publish(Parcel position) throws MqttException {
        MqttMessage message = new MqttMessage(position.toPayload());
        message.setQos(Main.QOS);
        client.publish(topic, message);
        System.out.println("Published: " + position);
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

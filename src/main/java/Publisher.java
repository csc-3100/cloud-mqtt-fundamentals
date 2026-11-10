import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * An MQTT client publishing to topics.
 *
 * @author javiergs
 * @version 2.0
 */
public class Publisher implements Runnable {
	
	private String broker;
	private String[] topics;
	
	public Publisher(String brokerUrl, String[] topics) {
		this.broker = brokerUrl;
		this.topics = topics;
	}
	
	@Override
	public void run() {
		MqttClient client = null;
		try {
			// 1. generate a unique clientID and connect to broker
			String clientId = MqttClient.generateClientId();
			client = new MqttClient(broker, clientId);
			client.connect();
			System.out.println("↗️ Connected to broker: " + broker);
			
			// publish messages periodically
			int counter = 0;
			while (counter < 10) {
				String content = Integer.toString(counter);
				MqttMessage message = new MqttMessage(content.getBytes(StandardCharsets.UTF_8));
				message.setQos(2);
				if (client.isConnected()) {
					for (String topic : topics) {
						client.publish(topic, message);
						System.out.println("↗️ published to " + topic + ": " + message);
						
					}
				}
				counter++;
				Thread.sleep(1000);
			}
		} catch (MqttException e) {
			System.out.println("↗️ MQTT error: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("↗️ Demo interrupted.");
			Thread.currentThread().interrupt();
		} finally {
			if (client != null && client.isConnected()) {
				try {
					client.disconnect();
					System.out.println("↗️ Disconnected from broker.");
				} catch (MqttException e) {
					System.err.println("↗️ Error disconnecting: " + e.getMessage());
				}
			}
		}
	}
	
}
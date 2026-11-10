import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * An MQTT client subscribing to topics.
 *
 * @author javiergs
 * @version 2.0
 */
public class Subscriber implements Runnable, MqttCallback {
	
	private String broker;
	private String[] topics;
	private final CompletableFuture<Boolean> subscriptionsReady = new CompletableFuture<>();
	private String subscriberName;
	
	public Subscriber(String brokerUrl, String[] topics) {
		this.broker = brokerUrl;
		this.topics = topics;
	}
	
	public boolean awaitSubscriptions() {
		return subscriptionsReady.join();
	}

	@Override
	public void run() {
		subscriberName = Thread.currentThread().getName();
		MqttClient client = null;
		try {
			// 1. generate a unique clientID and connect to broker
			String clientId = MqttClient.generateClientId();
			client = new MqttClient(broker, clientId);
			client.setCallback(this);
			client.connect();
			System.out.println("📥 Connected to broker: " + broker);
			// 2. subscribe to all configured topics with QoS=2
			for (String topic : topics) {
				client.subscribe(topic, 2);
				System.out.println("📥 Subscribed to topic: " + topic);
			}
			subscriptionsReady.complete(true);
			// 3. keep the client running to listen for messages
			while (true) {
				Thread.sleep(1000);
			}
		} catch (MqttException e) {
			System.out.println("📥 MQTT error: " + e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("📥 Demo interrupted.");
			Thread.currentThread().interrupt();
		} finally {
			subscriptionsReady.complete(false);
			if (client != null && client.isConnected()) {
				try {
					client.disconnect();
					System.out.println("📥 Disconnected from broker.");
				} catch (MqttException e) {
					System.err.println("📥 Error disconnecting: " + e.getMessage());
				}
			}
		}
	}
	
	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("📥 Connection to broker lost: " + cause.getMessage());
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message) {
		String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
		System.out.println("📥 Delivery :: " + "[" + topic + " : " + message.getQos() + "] :: " + payload);
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		try {
			System.out.println("📥 Delivery complete for: " + token.getMessageId());
		} catch (Exception e) {
			System.out.println("📥 Delivery complete, but failed to get message ID.");
		}
	}
	
}
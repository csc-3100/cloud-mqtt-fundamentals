/**
 * Entry point for the MQTT publish-subscribe demonstration.
 * It starts a publisher and a subscriber, both configured with the same topics.
 *
 * @author javiergs
 * @version 2.0
 */
public class Main {
	
	private final static String BROKER_URL = "tcp://broker.hivemq.com:1883";
	private final static String[] TOPICS = {
		"example/position/x",
		"example/position/y"
	};
	
	public static void main(String[] args) {
		// Start a subscriber thread
		Subscriber subscriber = new Subscriber(BROKER_URL, TOPICS);
		Thread threadSubscriber = new Thread(subscriber);
		threadSubscriber.start();
		// Start a publisher thread
		Publisher publisher = new Publisher(BROKER_URL, TOPICS);
		Thread threadPublisher = new Thread(publisher);
		threadPublisher.start();
	}
	
}
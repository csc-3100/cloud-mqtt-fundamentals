import java.util.UUID;

/**
 * Starts an MQTT subscriber, then publishes after its subscriptions are ready.
 */
public class Main {

	private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";

	public static void main(String[] args) {
		String runId = UUID.randomUUID().toString().substring(0, 12);
		String prefix = "example/" + runId + "/position/";
		String[] topics = {prefix + "x", prefix + "y"};
		System.out.println("Run topics: " + String.join(", ", topics));

		Subscriber subscriber = new Subscriber(BROKER_URL, topics);
		new Thread(subscriber, "subscriber-1").start();
		if (!subscriber.awaitSubscriptions()) {
			System.err.println("Subscriptions failed. Publisher was not started.");
			return;
		}

		Publisher publisher = new Publisher(BROKER_URL, topics);
		new Thread(publisher, "publisher").start();
	}
}

import java.util.UUID;

/**
 * Starts the subscriber before publishing position updates.
 */
public class Main {

    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";

    public static void main(String[] args) {
        String runId = UUID.randomUUID().toString().substring(0, 12);
        String topic = "example/" + runId + "/position";
        System.out.println("Broker: " + BROKER_URL);
        System.out.println("Topic: " + topic);

        Subscriber subscriber = new Subscriber(BROKER_URL, topic);
        new Thread(subscriber, "subscriber").start();

        if (!subscriber.awaitSubscription()) {
            System.err.println("Subscription failed; publisher will not start.");
            return;
        }

        Publisher publisher = new Publisher(BROKER_URL, topic);
        new Thread(publisher, "publisher").start();
    }
}

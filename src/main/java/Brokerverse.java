import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Swing example: arrow keys publish local positions; remote positions appear as red dots.
 *
 * Usage: Brokerverse [room] [playerId]
 */
public final class Brokerverse {
    private Brokerverse() {
    }

    public static void main(String[] args) throws Exception {
        String room = args.length > 0 ? args[0] : MqttConfig.DEFAULT_ROOM;
        String playerId = args.length > 1 ? args[1] :
                "gui-" + UUID.randomUUID().toString().substring(0, 8);
        String topic = MqttConfig.topicForRoom(room);
        System.out.println("Broker: " + MqttConfig.BROKER_URL);
        System.out.println("Topic: " + topic);

        CountDownLatch windowClosed = new CountDownLatch(1);
        AtomicReference<WorldPanel> panelRef = new AtomicReference<>();
        ExecutorService outgoing = Executors.newSingleThreadExecutor();

        try (Publisher publisher = new Publisher(MqttConfig.BROKER_URL, topic);
             Subscriber subscriber = new Subscriber(MqttConfig.BROKER_URL, topic, position -> {
                 WorldPanel panel = panelRef.get();
                 if (panel != null) {
                     SwingUtilities.invokeLater(() -> panel.receive(position));
                 }
             })) {
            // Keep network operations off the Swing event-dispatch thread.
            publisher.connect();
            subscriber.connect();

            SwingUtilities.invokeAndWait(() -> {
                WorldPanel panel = new WorldPanel(playerId, position ->
                        outgoing.execute(() -> {
                            try {
                                publisher.publish(position);
                            } catch (Exception e) {
                                System.err.println("Publish failed: " + e.getMessage());
                            }
                        }));
                panelRef.set(panel);

                JFrame window = new JFrame("Brokerverse — " + room + " — " + playerId);
                window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                window.add(panel);
                window.pack();
                window.setLocationByPlatform(true);
                window.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        windowClosed.countDown();
                    }
                });
                window.setVisible(true);

                // Announce the initial position as well as later arrow-key moves.
                panel.publishCurrentPosition();
            });

            windowClosed.await();
            // Flush queued position updates before closing the MQTT publisher.
            outgoing.shutdown();
            if (!outgoing.awaitTermination(2, TimeUnit.SECONDS)) {
                outgoing.shutdownNow();
            }
        } finally {
            outgoing.shutdownNow();
        }
    }
}

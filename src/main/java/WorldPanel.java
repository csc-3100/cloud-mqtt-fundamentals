import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * Visualizes local (blue) and remote (red) positions.
 * Swing callbacks and model updates run on the event-dispatch thread.
 */
public final class WorldPanel extends JPanel {
    private static final int STEP = 20;
    private static final int DOT = 20;

    private final Map<String, PositionMessage> otherPlayers = new HashMap<>();
    private final Consumer<PositionMessage> onMove;
    private PositionMessage me;

    public WorldPanel(String playerId, Consumer<PositionMessage> onMove) {
        this.me = new PositionMessage(playerId, 400, 300);
        this.onMove = onMove;
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(172, 248, 199));

        bind("UP", 0, -STEP);
        bind("DOWN", 0, STEP);
        bind("LEFT", -STEP, 0);
        bind("RIGHT", STEP, 0);
    }

    private void bind(String key, int dx, int dy) {
        String action = "move-" + key;
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), action);
        getActionMap().put(action, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                move(dx, dy);
            }
        });
    }

    private void move(int dx, int dy) {
        int x = Math.max(0, Math.min(getWidth() - DOT, me.getX() + dx));
        int y = Math.max(0, Math.min(getHeight() - DOT, me.getY() + dy));
        if (x == me.getX() && y == me.getY()) {
            return;
        }
        me = new PositionMessage(me.getPlayerId(), x, y);
        repaint();
        onMove.accept(me);
    }

    public void publishCurrentPosition() {
        onMove.accept(me);
    }

    public void receive(PositionMessage position) {
        if (!position.getPlayerId().equals(me.getPlayerId())) {
            otherPlayers.put(position.getPlayerId(), position);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < getWidth(); x += STEP) {
            for (int y = 0; y < getHeight(); y += STEP) {
                graphics.drawRect(x, y, STEP, STEP);
            }
        }
        for (PositionMessage player : otherPlayers.values()) {
            drawPosition(graphics, player, Color.RED);
        }
        drawPosition(graphics, me, Color.BLUE);
    }

    private void drawPosition(Graphics graphics, PositionMessage position, Color color) {
        graphics.setColor(color);
        graphics.fillOval(position.getX(), position.getY(), DOT, DOT);
        graphics.setColor(Color.BLACK);
        graphics.drawString(position.getPlayerId(), position.getX(), position.getY() - 3);
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Random;

public class GamePanel extends JPanel {

    private int currentPlayer;

    // Striker positions
    private double p1X = 175;
    private double p1Y = 80;

    private double p2X = 175;
    private double p2Y = 570;

    // Velocities
    private double p1VX = 0;
    private double p1VY = 0;

    private double p2VX = 0;
    private double p2VY = 0;

    private boolean strikerSelected = false;

    private int dragX;
    private int dragY;

    private final int STRIKER_SIZE = 50;
    private final int HALF = STRIKER_SIZE / 2;
    private final int PANEL_WIDTH = 400;
    private final int PANEL_HEIGHT = 700;

    // Active line (only one visible at a time)
    private int activeStartX = -1;
    private int activeStartY = -1;
    private int activeEndX = -1;
    private int activeEndY = -1;

    // Whose line is currently active (the player who must now be crossed)
    // 0 = no active line yet
    private int activeLineOwner = 0;

    // Current shot start position (set at launch)
    private int shotStartX;
    private int shotStartY;

    private boolean p1WasMoving = false;
    private boolean p2WasMoving = false;

    // Bounce tracking for the current shot
    private boolean p1Bounced = false;
    private boolean p2Bounced = false;

    private int p1LastBounceX;
    private int p1LastBounceY;

    private int p2LastBounceX;
    private int p2LastBounceY;

    // Previous-frame position, used to build the movement segment
    // for collision checking against the active line
    private double p1PrevX = p1X;
    private double p1PrevY = p1Y;

    private double p2PrevX = p2X;
    private double p2PrevY = p2Y;

    // Game over state
    private boolean gameOver = false;
    private int winner = 0;

    public GamePanel() {

        setBackground(Color.WHITE);

        Random random = new Random();
        currentPlayer = random.nextInt(2) + 1;

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (gameOver) {
                    return;
                }

                int mouseX = e.getX();
                int mouseY = e.getY();

                if (currentPlayer == 1 &&
                        isInsideStriker(mouseX, mouseY,
                                (int) p1X, (int) p1Y)) {

                    strikerSelected = true;
                }

                else if (currentPlayer == 2 &&
                        isInsideStriker(mouseX, mouseY,
                                (int) p2X, (int) p2Y)) {

                    strikerSelected = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (gameOver) {
                    restartGame();
                    repaint();
                    return;
                }

                if (strikerSelected) {

                    if (currentPlayer == 1) {

                        shotStartX = (int) p1X + HALF;
                        shotStartY = (int) p1Y + HALF;

                        p1VX = ((p1X + HALF) - dragX) * 0.2;
                        p1VY = ((p1Y + HALF) - dragY) * 0.2;

                        p1WasMoving = true;
                        p1Bounced = false;

                        p1PrevX = p1X;
                        p1PrevY = p1Y;

                    } else {

                        shotStartX = (int) p2X + HALF;
                        shotStartY = (int) p2Y + HALF;

                        p2VX = ((p2X + HALF) - dragX) * 0.2;
                        p2VY = ((p2Y + HALF) - dragY) * 0.2;

                        p2WasMoving = true;
                        p2Bounced = false;

                        p2PrevX = p2X;
                        p2PrevY = p2Y;
                    }
                }

                strikerSelected = false;
            }
        });

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (gameOver) {
                    return;
                }

                if (strikerSelected) {

                    dragX = e.getX();
                    dragY = e.getY();

                    repaint();
                }
            }
        });

        new Timer(16, e -> {

            if (gameOver) {
                return;
            }

            // ---------- PLAYER 1 ----------
            if (p1WasMoving) {

                p1PrevX = p1X;
                p1PrevY = p1Y;

                p1X += p1VX;
                p1Y += p1VY;

                // Bounce Player 1
                if (p1X <= 0) {
                    p1X = 0;
                    p1VX = -p1VX;

                    p1Bounced = true;
                    p1LastBounceX = HALF;
                    p1LastBounceY = (int) p1Y + HALF;
                }

                if (p1X >= PANEL_WIDTH - STRIKER_SIZE) {
                    p1X = PANEL_WIDTH - STRIKER_SIZE;
                    p1VX = -p1VX;

                    p1Bounced = true;
                    p1LastBounceX = PANEL_WIDTH - HALF;
                    p1LastBounceY = (int) p1Y + HALF;
                }

                if (p1Y <= 0) {
                    p1Y = 0;
                    p1VY = -p1VY;

                    p1Bounced = true;
                    p1LastBounceX = (int) p1X + HALF;
                    p1LastBounceY = HALF;
                }

                if (p1Y >= PANEL_HEIGHT - STRIKER_SIZE) {
                    p1Y = PANEL_HEIGHT - STRIKER_SIZE;
                    p1VY = -p1VY;

                    p1Bounced = true;
                    p1LastBounceX = (int) p1X + HALF;
                    p1LastBounceY = PANEL_HEIGHT - HALF;
                }

                // Check collision against active line (only matters if
                // Player 2 owns the active line, i.e. Player 1 must cross it)
                if (activeLineOwner == 2) {

                    boolean crossed = Line2D.linesIntersect(
                            p1PrevX + HALF, p1PrevY + HALF,
                            p1X + HALF, p1Y + HALF,
                            activeStartX, activeStartY,
                            activeEndX, activeEndY
                    );

                    if (crossed) {
                        winner = 1;
                        gameOver = true;
                        p1VX = 0;
                        p1VY = 0;
                        p1WasMoving = false;
                    }
                }

                // Striker collision: if moving Player 1 touches the
                // stationary Player 2, Player 1 is eliminated and
                // Player 2 wins instantly.
                if (!gameOver && strikersOverlap(p1X, p1Y, p2X, p2Y)) {

                    winner = 2;
                    gameOver = true;
                    p1VX = 0;
                    p1VY = 0;
                    p1WasMoving = false;
                }

                // Friction
                p1VX *= 0.98;
                p1VY *= 0.98;

                if (Math.abs(p1VX) < 0.1) p1VX = 0;
                if (Math.abs(p1VY) < 0.1) p1VY = 0;

                // Player 1 stopped (and didn't just win)
                if (!gameOver && p1WasMoving && p1VX == 0 && p1VY == 0) {

                    if (p1Bounced) {
                        activeStartX = p1LastBounceX;
                        activeStartY = p1LastBounceY;
                    } else {
                        activeStartX = shotStartX;
                        activeStartY = shotStartY;
                    }

                    activeEndX = (int) p1X + HALF;
                    activeEndY = (int) p1Y + HALF;

                    activeLineOwner = 1;

                    p1WasMoving = false;

                    // Switch to Player 2
                    currentPlayer = 2;
                }
            }

            // ---------- PLAYER 2 ----------
            if (!gameOver && p2WasMoving) {

                p2PrevX = p2X;
                p2PrevY = p2Y;

                p2X += p2VX;
                p2Y += p2VY;

                // Bounce Player 2
                if (p2X <= 0) {
                    p2X = 0;
                    p2VX = -p2VX;

                    p2Bounced = true;
                    p2LastBounceX = HALF;
                    p2LastBounceY = (int) p2Y + HALF;
                }

                if (p2X >= PANEL_WIDTH - STRIKER_SIZE) {
                    p2X = PANEL_WIDTH - STRIKER_SIZE;
                    p2VX = -p2VX;

                    p2Bounced = true;
                    p2LastBounceX = PANEL_WIDTH - HALF;
                    p2LastBounceY = (int) p2Y + HALF;
                }

                if (p2Y <= 0) {
                    p2Y = 0;
                    p2VY = -p2VY;

                    p2Bounced = true;
                    p2LastBounceX = (int) p2X + HALF;
                    p2LastBounceY = HALF;
                }

                if (p2Y >= PANEL_HEIGHT - STRIKER_SIZE) {
                    p2Y = PANEL_HEIGHT - STRIKER_SIZE;
                    p2VY = -p2VY;

                    p2Bounced = true;
                    p2LastBounceX = (int) p2X + HALF;
                    p2LastBounceY = PANEL_HEIGHT - HALF;
                }

                // Check collision against active line (only matters if
                // Player 1 owns the active line, i.e. Player 2 must cross it)
                if (activeLineOwner == 1) {

                    boolean crossed = Line2D.linesIntersect(
                            p2PrevX + HALF, p2PrevY + HALF,
                            p2X + HALF, p2Y + HALF,
                            activeStartX, activeStartY,
                            activeEndX, activeEndY
                    );

                    if (crossed) {
                        winner = 2;
                        gameOver = true;
                        p2VX = 0;
                        p2VY = 0;
                        p2WasMoving = false;
                    }
                }

                // Striker collision: if moving Player 2 touches the
                // stationary Player 1, Player 2 is eliminated and
                // Player 1 wins instantly.
                if (!gameOver && strikersOverlap(p2X, p2Y, p1X, p1Y)) {

                    winner = 1;
                    gameOver = true;
                    p2VX = 0;
                    p2VY = 0;
                    p2WasMoving = false;
                }

                // Friction
                p2VX *= 0.98;
                p2VY *= 0.98;

                if (Math.abs(p2VX) < 0.1) p2VX = 0;
                if (Math.abs(p2VY) < 0.1) p2VY = 0;

                // Player 2 stopped (and didn't just win)
                if (!gameOver && p2WasMoving && p2VX == 0 && p2VY == 0) {

                    if (p2Bounced) {
                        activeStartX = p2LastBounceX;
                        activeStartY = p2LastBounceY;
                    } else {
                        activeStartX = shotStartX;
                        activeStartY = shotStartY;
                    }

                    activeEndX = (int) p2X + HALF;
                    activeEndY = (int) p2Y + HALF;

                    activeLineOwner = 2;

                    p2WasMoving = false;

                    // Switch to Player 1
                    currentPlayer = 1;
                }
            }

            repaint();

        }).start();
    }

    private void restartGame() {

        p1X = 175;
        p1Y = 80;

        p2X = 175;
        p2Y = 570;

        p1VX = 0;
        p1VY = 0;

        p2VX = 0;
        p2VY = 0;

        p1WasMoving = false;
        p2WasMoving = false;

        p1Bounced = false;
        p2Bounced = false;

        activeStartX = -1;
        activeStartY = -1;
        activeEndX = -1;
        activeEndY = -1;
        activeLineOwner = 0;

        strikerSelected = false;

        gameOver = false;
        winner = 0;

        Random random = new Random();
        currentPlayer = random.nextInt(2) + 1;
    }

    private boolean isInsideStriker(int mouseX,
                                    int mouseY,
                                    int strikerX,
                                    int strikerY) {

        return mouseX >= strikerX &&
                mouseX <= strikerX + STRIKER_SIZE &&
                mouseY >= strikerY &&
                mouseY <= strikerY + STRIKER_SIZE;
    }

    // Returns true if the two strikers' circular bodies overlap.
    // Both strikers have the same radius (HALF == STRIKER_SIZE / 2),
    // so they touch when the distance between centers <= STRIKER_SIZE.
    private boolean strikersOverlap(double ax, double ay,
                                    double bx, double by) {

        double centerAx = ax + HALF;
        double centerAy = ay + HALF;

        double centerBx = bx + HALF;
        double centerBy = by + HALF;

        double dx = centerAx - centerBx;
        double dy = centerAy - centerBy;

        double distanceSquared = dx * dx + dy * dy;

        double collideDistance = STRIKER_SIZE;

        return distanceSquared <= collideDistance * collideDistance;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(3));

        // Active line
        if (activeLineOwner != 0) {

            g2.setColor(Color.BLACK);

            g2.drawLine(
                    activeStartX,
                    activeStartY,
                    activeEndX,
                    activeEndY
            );
        }

        // Player 1
        g2.setColor(Color.RED);
        g2.fillOval((int) p1X,
                (int) p1Y,
                STRIKER_SIZE,
                STRIKER_SIZE);

        // Player 2
        g2.setColor(Color.BLUE);
        g2.fillOval((int) p2X,
                (int) p2Y,
                STRIKER_SIZE,
                STRIKER_SIZE);

        // Aim line
        if (strikerSelected && !gameOver) {

            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.GRAY);

            if (currentPlayer == 1) {

                g2.drawLine(
                        (int) p1X + HALF,
                        (int) p1Y + HALF,
                        dragX,
                        dragY
                );

            } else {

                g2.drawLine(
                        (int) p2X + HALF,
                        (int) p2Y + HALF,
                        dragX,
                        dragY
                );
            }
        }

        // Turn text / Game over text
        g2.setColor(Color.BLACK);

        if (!gameOver) {

            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Player " + currentPlayer + "'s Turn",
                    110,
                    350);

        } else {

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            String winText = "PLAYER " + winner + " WINS!";
            int textWidth = g2.getFontMetrics().stringWidth(winText);
            g2.drawString(winText, (PANEL_WIDTH - textWidth) / 2, 330);

            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            String restartText = "Tap anywhere to restart";
            int restartWidth = g2.getFontMetrics().stringWidth(restartText);
            g2.drawString(restartText, (PANEL_WIDTH - restartWidth) / 2, 365);
        }
    }
}
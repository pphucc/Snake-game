import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // Constants
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_HEIGHT * SCREEN_WIDTH) / UNIT_SIZE;
    static int DELAY = 100;

    // Game states and options
    enum GameState {
        MENU, PLAYING, PAUSED, GAME_OVER
    }

    enum Difficulty {
        EASY, MEDIUM, HARD
    }

    // Game variables
    private int[] x = new int[GAME_UNITS];
    private int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private boolean special = false;
    private Timer timer;
    private Random random;
    private GameState gameState;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        gameState = GameState.MENU;
    }

    public void startGame() {
        resetGame();
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void resetGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        x[0] = 0;
        y[0] = 0;
    }

    public void newApple() {
        List<Integer> availablePositionsX = new ArrayList<Integer>();
        List<Integer> availablePositionsY = new ArrayList<Integer>();

        for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
            for (int j = 0; j < SCREEN_HEIGHT / UNIT_SIZE; j++) {
                boolean isSnakeBody = false;
                for (int k = 0; k < bodyParts; k++) {
                    if (x[k] == i * UNIT_SIZE && y[k] == j * UNIT_SIZE) {
                        isSnakeBody = true;
                        break;
                    }
                }
                if (!isSnakeBody) {
                    availablePositionsX.add(i * UNIT_SIZE);
                    availablePositionsY.add(j * UNIT_SIZE);
                }
            }
        }

        int availablePositionsCount = availablePositionsX.size();
        if (availablePositionsCount > 0) {
            int randomIndex = random.nextInt(availablePositionsCount);
            appleX = availablePositionsX.get(randomIndex);
            appleY = availablePositionsY.get(randomIndex);
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            if (special) {
                bodyParts += 10;
                applesEaten += 10;
            } else {
                bodyParts++;
                applesEaten++;
            }
            newApple();
        }
    }

    public void checkCollision() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i] && y[0] == y[i])) {
                running = false;
                gameState = GameState.GAME_OVER;
            }
        }

        // if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
        // running = false;
        // gameState = GameState.GAME_OVER;
        // }

        if (x[0] < 0) {
            x[0] = SCREEN_WIDTH - UNIT_SIZE;
        }
        if (x[0] >= SCREEN_WIDTH) {
            x[0] = 0;
        }
        if (y[0] < 0) {
            y[0] = SCREEN_HEIGHT - UNIT_SIZE;
        }
        if (y[0] >= SCREEN_HEIGHT) {
            y[0] = 0;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == GameState.MENU) {
            drawMenu(g);
        } else if (gameState == GameState.PLAYING || gameState == GameState.PAUSED) {
            drawGame(g);
        } else if (gameState == GameState.GAME_OVER) {
            drawGameOver(g);
        }
    }

    public void drawMenu(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 80));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Snake Game", (SCREEN_WIDTH - metrics.stringWidth("Snake Game")) / 2, SCREEN_HEIGHT / 2 - 100);

        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Choose Difficulty:", (SCREEN_WIDTH - metrics.stringWidth("Choose Difficulty:")) / 2,
                SCREEN_HEIGHT / 2);

        g.setFont(new Font("Ink Free", Font.BOLD, 20));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Press 1 for Easy", (SCREEN_WIDTH - metrics.stringWidth("Press 1 for Easy")) / 2,
                SCREEN_HEIGHT / 2 + 50);
        g.drawString("Press 2 for Medium", (SCREEN_WIDTH - metrics.stringWidth("Press 2 for Medium")) / 2,
                SCREEN_HEIGHT / 2 + 80);
        g.drawString("Press 3 for Hard", (SCREEN_WIDTH - metrics.stringWidth("Press 3 for Hard")) / 2,
                SCREEN_HEIGHT / 2 + 110);
        g.drawString("Press 0 for Super Hard", (SCREEN_WIDTH - metrics.stringWidth("Press 0 for Super Hard")) / 2,
                SCREEN_HEIGHT / 2 + 140);
        g.drawString("Press Enter to Start", (SCREEN_WIDTH - metrics.stringWidth("Press Enter to Start")) / 2,
                SCREEN_HEIGHT / 2 + 200);
    }

    public void drawGame(Graphics g) {
        // Draw game components
        // for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
        // g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
        // g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
        // }

        // // draw apple
        g.setColor(Color.red);
        if (applesEaten % 4 == 0 && applesEaten > 0) {
            special = true;
            g.setColor(Color.white);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        } else {
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            special = false;
        }

        // draw snake
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }

        // Draw game stats
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                g.getFont().getSize());

        if (gameState == GameState.PAUSED) {
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            metrics = getFontMetrics(g.getFont());
            g.drawString("Paused", (SCREEN_WIDTH - metrics.stringWidth("Paused")) / 2, SCREEN_HEIGHT / 2);
            g.drawString("Press Enter to Resume", (SCREEN_WIDTH - metrics.stringWidth("Press Enter to Resume")) / 2,
                    SCREEN_HEIGHT / 2 + 50);
            g.drawString("Press M to Return to Menu",
                    (SCREEN_WIDTH - metrics.stringWidth("Press M to Return to Menu")) / 2,
                    SCREEN_HEIGHT / 2 + 100);
        }
    }

    public void drawGameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2 - 10);

        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                SCREEN_HEIGHT / 2 + 50);
        g.drawString("Press Enter to Restart", (SCREEN_WIDTH - metrics.stringWidth("Press Enter to Restart")) / 2,
                SCREEN_HEIGHT / 2 + 100);
        g.drawString("Press M to Return to Menu", (SCREEN_WIDTH - metrics.stringWidth("Press M to Return to Menu")) / 2,
                SCREEN_HEIGHT / 2 + 150);
    }

    public void pauseGame() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            timer.stop();
            repaint();
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            timer.start();
        }
    }

    public void returnToMenu() {
        gameState = GameState.MENU;
        resetGame();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    if (gameState == GameState.MENU) {
                        startGame();
                        gameState = GameState.PLAYING;
                    } else if (gameState == GameState.PLAYING || gameState == GameState.PAUSED) {
                        pauseGame();
                    } else if (gameState == GameState.GAME_OVER) {
                        gameState = GameState.MENU;
                    }
                    break;
                case KeyEvent.VK_1:
                    if (gameState == GameState.MENU) {
                        DELAY = 100;
                        startGame();
                        gameState = GameState.PLAYING;
                    }
                    break;
                case KeyEvent.VK_2:
                    if (gameState == GameState.MENU) {
                        DELAY = 50;
                        startGame();
                        gameState = GameState.PLAYING;
                    }
                    break;
                case KeyEvent.VK_3:
                    if (gameState == GameState.MENU) {
                        DELAY = 10;
                        startGame();
                        gameState = GameState.PLAYING;
                    }
                    break;
                case KeyEvent.VK_0:
                    if (gameState == GameState.MENU) {
                        DELAY = 200;
                        startGame();
                        gameState = GameState.PLAYING;
                    }
                    break;
                case KeyEvent.VK_M:
                    if (gameState == GameState.GAME_OVER || gameState == GameState.PAUSED) {
                        returnToMenu();
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (gameState == GameState.PLAYING && direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (gameState == GameState.PLAYING && direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (gameState == GameState.PLAYING && direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (gameState == GameState.PLAYING && direction != 'L') {
                        direction = 'R';
                    }
                    break;
            }
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

public class TetrisGame {
    final int WIDTH = 10, HEIGHT = 20;
    int[][] gameBoard = new int[HEIGHT][WIDTH];
    Queue<Block> blockQueue = new LinkedList<>();
    int score = 0;
    long startTime;
    boolean gameOver = false;

    class Block {
        int shape[][];
        Color color;
        int x, y;

        public Block(int[][] shape, Color color) {
            this.shape = shape;
            this.color = color;
            this.x = WIDTH / 2 - shape[0].length / 2;
            this.y = 0;
        }

        public void rotate() {
            int n = shape.length, m = shape[0].length;
            int[][] rotatedShape = new int[m][n];

            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++)
                    rotatedShape[j][n - 1 - i] = shape[i][j];

            if (!collides(x, y, rotatedShape))
                shape = rotatedShape;
        }
    }

    JFrame frame;
    JPanel panel;
    JLabel scoreLabel, timeLabel;
    Timer timer;

    public void init() {
        frame = new JFrame("Tetris Game");
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        panel.setPreferredSize(new Dimension(300, 600));
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        scoreLabel = new JLabel("Score: 0");
        timeLabel = new JLabel("Time: 0s");
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(scoreLabel);
        topPanel.add(timeLabel);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        generateBlock();
        startTime = System.currentTimeMillis();
        startGameLoop();
    }

    public void drawBoard(Graphics g) {
        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++)
                if (gameBoard[i][j] != 0) {
                    g.setColor(Color.GRAY);
                    g.fillRect(j * 30, i * 30, 30, 30);
                }

        Block currentBlock = blockQueue.peek();
        if (currentBlock != null) {
            g.setColor(currentBlock.color);
            for (int i = 0; i < currentBlock.shape.length; i++)
                for (int j = 0; j < currentBlock.shape[i].length; j++)
                    if (currentBlock.shape[i][j] == 1)
                        g.fillRect((currentBlock.x + j) * 30, (currentBlock.y + i) * 30, 30, 30);
        }
    }

    public void generateBlock() {
        int[][][] shapes = {
            {{1, 1, 1}, {0, 1, 0}}, {{1, 1}, {1, 1}}, {{1, 1, 0}, {0, 1, 1}}, {{0, 1, 1}, {1, 1, 0}}, {{1, 1, 1, 1}}
        };
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN};

        Random rand = new Random();
        int shapeIndex = rand.nextInt(shapes.length);
        Block newBlock = new Block(shapes[shapeIndex], colors[shapeIndex]);
        blockQueue.add(newBlock);

        if (collides(newBlock.x, newBlock.y, newBlock.shape)) {
            gameOver = true;
            timer.stop();
            JOptionPane.showMessageDialog(frame, "Game Over! Final Score: " + score);
            System.exit(0);
        }
    }

    public void startGameLoop() {
        timer = new Timer(500, e -> {
            if (!gameOver) {
                moveBlockDown();
                timeLabel.setText("Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s");
                panel.repaint();
            }
        });
        timer.start();
    }

    public void moveBlockDown() {
        Block currentBlock = blockQueue.peek();
        if (currentBlock != null) {
            if (canMoveDown(currentBlock))
                currentBlock.y++;
            else {
                placeBlock(currentBlock);
                generateBlock();
            }
        }
    }

    public boolean canMoveDown(Block block) {
        return !collides(block.x, block.y + 1, block.shape);
    }

    public boolean collides(int newX, int newY, int[][] shape) {
        for (int i = 0; i < shape.length; i++)
            for (int j = 0; j < shape[i].length; j++)
                if (shape[i][j] == 1 && (newY + i >= HEIGHT || newX + j < 0 || newX + j >= WIDTH || gameBoard[newY + i][newX + j] != 0))
                    return true;
        return false;
    }

    public void placeBlock(Block block) {
        for (int i = 0; i < block.shape.length; i++)
            for (int j = 0; j < block.shape[i].length; j++)
                if (block.shape[i][j] == 1)
                    gameBoard[block.y + i][block.x + j] = 1;

        blockQueue.poll();
        checkRows();
    }

    public void checkRows() {
        for (int i = HEIGHT - 1; i >= 0; i--) {
            boolean fullRow = true;
            for (int j = 0; j < WIDTH; j++)
                if (gameBoard[i][j] == 0)
                    fullRow = false;

            if (fullRow) {
                score += 100;
                scoreLabel.setText("Score: " + score);

                for (int k = i; k > 0; k--)
                    System.arraycopy(gameBoard[k - 1], 0, gameBoard[k], 0, WIDTH);

                Arrays.fill(gameBoard[0], 0);
                i++;
            }
        }
    }

    public void handleKeyPress(KeyEvent e) {
        Block currentBlock = blockQueue.peek();
        if (currentBlock == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (!collides(currentBlock.x - 1, currentBlock.y, currentBlock.shape))
                    currentBlock.x--;
                break;
            case KeyEvent.VK_RIGHT:
                if (!collides(currentBlock.x + 1, currentBlock.y, currentBlock.shape))
                    currentBlock.x++;
                break;
            case KeyEvent.VK_DOWN:
                moveBlockDown();
                break;
            case KeyEvent.VK_SPACE:
                currentBlock.rotate();
                break;
            case KeyEvent.VK_ENTER:
                while (canMoveDown(currentBlock))
                    currentBlock.y++;
                placeBlock(currentBlock);
                generateBlock();
                break;
        }
        panel.repaint();
    }

    public static void main(String[] args) {
        new TetrisGame().init();
    }
}

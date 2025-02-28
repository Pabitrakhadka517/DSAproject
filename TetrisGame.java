/*
 * Step-by-Step Algorithm for Tetris Game
 * 
 * 1. Initialize game board, block queue, and active block.
 * 2. Start a game loop using a timer that moves blocks down periodically.
 * 3. Generate a new block and set its initial position.
 * 4. Handle user inputs:
 *      - Left arrow: Move block left.
 *      - Right arrow: Move block right.
 *      - Down arrow: Move block down.
 *      - Up arrow: Rotate block.
 * 5. Move the block down automatically every timer tick:
 *      - If it can move down, move it.
 *      - Otherwise, place the block on the board.
 *      - Check if any row is full, then clear it and update the score.
 *      - Generate a new block and reset position.
 *      - If new block placement is not possible, set game over.
 * 6. Draw the game board, blocks, and grid lines.
 * 7. End the game if no space is left for a new block.
 */


 
import javax.swing.*; // Import Swing components for GUI
import java.awt.*;   // Import AWT for graphics rendering
import java.awt.event.*;   // Import event handling classes
import java.util.*;     // Import utility classes like Queue and Ra
import java.util.Queue;

public class TetrisGame extends JPanel {
    final int WIDTH = 10, HEIGHT = 20; // Game board size
    final int BLOCK_SIZE = 30; // Size of each block
    private int[][] board = new int[HEIGHT][WIDTH]; // Stack for game state
    private Queue<int[][]> blockQueue = new LinkedList<>(); // Queue for falling blocks
    private int[][] currentBlock; // Current active block
    private int blockX = WIDTH / 2 - 1, blockY = 0; // Block position
    private boolean gameOver = false;
    private int score = 0;
    private javax.swing.Timer timer; // FIX: Explicitly use javax.swing.Timer

    // Shapes of Tetris blocks
    private final int[][][] SHAPES = {
            {{1, 1, 1, 1}}, // Line
            {{1, 1}, {1, 1}}, // Square
            {{0, 1, 0}, {1, 1, 1}}, // T-Shape
            {{1, 1, 0}, {0, 1, 1}}, // S-Shape
            {{0, 1, 1}, {1, 1, 0}}, // Z-Shape
            {{1, 0, 0}, {1, 1, 1}}, // L-Shape
            {{0, 0, 1}, {1, 1, 1}} // J-Shape
    };


    // Constructor to set up game panel
    public TetrisGame() {
        // Set panel size
        setPreferredSize(new Dimension(WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE));
        // Set background color
        setBackground(Color.WHITE);
        // Enable keyboard input
        setFocusable(true);

        generateBlock();  // Generate the first block
        timer = new javax.swing.Timer(500, e -> gameLoop()); // FIXED: javax.swing.Timer
        timer.start();  // Start the game loop


        // Add keyboard controls
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (gameOver) return;  // Ignore input if game is over
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> moveLeft();   // Move left
                    case KeyEvent.VK_RIGHT -> moveRight();  // Move Right
                    case KeyEvent.VK_DOWN -> moveDown();     // Move down
                    case KeyEvent.VK_UP -> rotateBlock();    // Rotate
                }
                repaint();   // Refresh display
            }
        });
    }

    // Game loop function
    private void gameLoop() {
        if (gameOver) {
            timer.stop();  // Stop the game
            JOptionPane.showMessageDialog(this, "Game Over! Score: " + score);   // Show final score
            return;
        }

        moveDown();    // Move block down each tick
        repaint();     // Refresh display
    }


    // Generate a random block and add it to the queue
    private void generateBlock() {
        currentBlock = SHAPES[new Random().nextInt(SHAPES.length)];
        blockQueue.add(currentBlock);
    }

     // Move block left if possible
    private void moveLeft() {
        if (canMove(blockX - 1, blockY)) blockX--;
    }

     // Move block right if possible
    private void moveRight() {
        if (canMove(blockX + 1, blockY)) blockX++;
    }

     // Move block down or place it if it can't move further
    private void moveDown() {
        if (canMove(blockX, blockY + 1)) {
            blockY++;
        } else {
            placeBlock();   // Place block in board
            checkRows();    // Check for full rows
            generateBlock();   // Create a new block
            blockX = WIDTH / 2 - 1;   // Reset block position
            blockY = 0;
            if (!canMove(blockX, blockY)) gameOver = true;    // End game if new block can't fit
        }
    }

     // Rotate the current block
    private void rotateBlock() {
        int rows = currentBlock.length, cols = currentBlock[0].length;
        int[][] rotated = new int[cols][rows];

        // Rotate block by 90 degrees
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                rotated[x][rows - y - 1] = currentBlock[y][x];
            }
        }

        if (canMove(blockX, blockY, rotated)) currentBlock = rotated;  // Apply rotation if valid
    }


    // Check if block can move to a new position
    private boolean canMove(int newX, int newY) {
        return canMove(newX, newY, currentBlock);
    }

    private boolean canMove(int newX, int newY, int[][] block) {
        for (int y = 0; y < block.length; y++) {
            for (int x = 0; x < block[y].length; x++) {
                if (block[y][x] == 1) {
                    int newXPos = newX + x, newYPos = newY + y;
                    if (newXPos < 0 || newXPos >= WIDTH || newYPos >= HEIGHT || board[newYPos][newXPos] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Place the block on the game board
    private void placeBlock() {
        for (int y = 0; y < currentBlock.length; y++) {
            for (int x = 0; x < currentBlock[y].length; x++) {
                if (currentBlock[y][x] == 1) {
                    board[blockY + y][blockX + x] = 1;
                }
            }
        }
    }

     // Check for and clear full rows
    private void checkRows() {
        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean fullRow = true;
            for (int x = 0; x < WIDTH; x++) {
                if (board[y][x] == 0) {
                    fullRow = false;
                    break;
                }
            }
            if (fullRow) {
                score += 10;
                for (int newY = y; newY > 0; newY--) {
                    System.arraycopy(board[newY - 1], 0, board[newY], 0, WIDTH);
                }
                Arrays.fill(board[0], 0);
                y++;
            }
        }
    }

    // Render the game graphics
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GRAY);

        for (int x = 0; x <= WIDTH; x++) {
            g.drawLine(x * BLOCK_SIZE, 0, x * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
        }
        for (int y = 0; y <= HEIGHT; y++) {
            g.drawLine(0, y * BLOCK_SIZE, WIDTH * BLOCK_SIZE, y * BLOCK_SIZE);
        }

        g.setColor(Color.BLUE);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (board[y][x] == 1) {
                    g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        g.setColor(Color.RED);
        for (int y = 0; y < currentBlock.length; y++) {
            for (int x = 0; x < currentBlock[y].length; x++) {
                if (currentBlock[y][x] == 1) {
                    g.fillRect((blockX + x) * BLOCK_SIZE, (blockY + y) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game");
        TetrisGame game = new TetrisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

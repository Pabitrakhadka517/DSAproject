/*
Algorithm:

1. Initialize the game grid (WIDTH x HEIGHT) and set up the block size.
2. Define all possible Tetris block shapes in a 3D array (SHAPES).
3. Set initial values for the current block, next block, score, and timers.
4. Set up a game loop using a timer to periodically move the block down.
5. Generate the next block and add it to a block queue.
6. Start a time tracking timer to keep track of elapsed time.
7. Add key listeners for controlling the blocks (left, right, down, rotate, power-ups).
8. Inside the game loop:
   - Check if the current block can move down; if not, place it in the grid and generate a new block.
   - Check if any rows are complete and clear them.
   - Update the score for every row cleared.
   - Adjust the game speed as the score increases.
9. Implement functions to:
   - Move the block left, right, or down.
   - Rotate the block (clockwise).
   - Check for block collisions with walls or other blocks.
   - Place the block in the game grid once it reaches the bottom.
   - Clear completed rows.
   - Activate the slow time power-up and handle its effect on game speed.
10. Paint the grid, placed blocks, current block, next block preview, score, and time elapsed on the screen.
11. End the game if the block cannot be placed at the top of the grid (game over).
12. Display the score and time when the game ends.
*/



import javax.swing.*; // Importing Swing components for GUI
import java.awt.*; // Importing AWT components for graphical interface
import java.awt.event.*; // Importing event handling components
import java.util.*; // Importing utility classes like Random and LinkedList

public class TetrisGame extends JPanel {
    final int WIDTH = 10, HEIGHT = 20; // Set the width and height of the game grid
    final int BLOCK_SIZE = 30; // Set the size of each block (30px by 30px)
    
    private int[][] board = new int[HEIGHT][WIDTH]; // The game board represented as a 2D array
    private int[][] currentBlock; // The current block in play
    private int[][] nextBlock; // The next block that will appear
    
    private int blockX = WIDTH / 2 - 1, blockY = 0; // Starting position of the current block
    private boolean gameOver = false; // Flag to check if the game is over
    private int score = 0; // Initializing the score to 0
    private int timeElapsed = 0; // Time elapsed in seconds
    private javax.swing.Timer gameTimer; // Timer to control the game's speed
    private javax.swing.Timer timeTimer; // Timer to track elapsed time

    private Queue<int[][]> blockQueue = new LinkedList<>(); // Queue to hold upcoming blocks
    private boolean slowTimeActive = false; // Flag to check if slow time power-up is active

    // Array containing possible block shapes (each shape is a 2D array)
    private final int[][][] SHAPES = {
            {{1, 1, 1, 1}}, // Line shape
            {{1, 1}, {1, 1}}, // Square shape
            {{0, 1, 0}, {1, 1, 1}}, // T-Shape
            {{1, 1, 0}, {0, 1, 1}}, // S-Shape
            {{0, 1, 1}, {1, 1, 0}}, // Z-Shape
            {{1, 0, 0}, {1, 1, 1}}, // L-Shape
            {{0, 0, 1}, {1, 1, 1}}  // J-Shape
    };

    public TetrisGame() {
        // Set the preferred size of the game panel
        setPreferredSize(new Dimension(WIDTH * BLOCK_SIZE + 200, HEIGHT * BLOCK_SIZE));
        setBackground(Color.WHITE); // Set the background color of the panel
        setFocusable(true); // Make the panel focusable to capture key events

        // Initialize blocks
        generateNextBlock(); // Generate the next block
        currentBlock = nextBlock; // Set the current block to the next block
        generateNextBlock(); // Generate the second next block

        // Timer to control the game's speed (500ms interval)
        gameTimer = new javax.swing.Timer(500, e -> gameLoop());
        gameTimer.start();

        // Timer to track time elapsed and update the display (1000ms interval)
        timeTimer = new javax.swing.Timer(1000, e -> {
            if (!gameOver) {
                timeElapsed++; // Increment time elapsed by 1 second
                repaint(); // Redraw the game screen
            }
        });
        timeTimer.start();

        // Key listener to handle user input (movement and rotations)
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (gameOver) return; // If the game is over, ignore key presses
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> moveLeft(); // Move block left
                    case KeyEvent.VK_RIGHT -> moveRight(); // Move block right
                    case KeyEvent.VK_DOWN -> moveDown(); // Move block down
                    case KeyEvent.VK_UP -> rotateBlock(); // Rotate the block
                    case KeyEvent.VK_SPACE -> activateSlowTime();  // Power-up (slow time)
                }
                repaint(); // Redraw the game screen after movement or action
            }
        });
    }

    private void gameLoop() {
        if (gameOver) {
            gameTimer.stop(); // Stop the game timer when game is over
            JOptionPane.showMessageDialog(this, "Game Over! Score: " + score + "\nTime: " + timeElapsed + "s");
            return;
        }
        moveDown(); // Move the block down every game loop
        repaint(); // Redraw the game screen
    }

    private void generateNextBlock() {
        // Add random shapes to the blockQueue until it has 5 blocks
        while (blockQueue.size() < 5) {
            blockQueue.add(SHAPES[new Random().nextInt(SHAPES.length)]); 
        }
        nextBlock = blockQueue.peek(); // Set the next block to the first block in the queue
    }

    private void generateBlock() {
        currentBlock = blockQueue.poll(); // Get the next block from the queue
        generateNextBlock();  // Generate a new upcoming block
        blockX = WIDTH / 2 - 1; // Reset block position to the middle of the grid
        blockY = 0; // Set the block's vertical position to the top of the grid

        if (!canMove(blockX, blockY)) {
            gameOver = true; // End the game if the new block can't be placed
        }
    }

    private void moveLeft() {
        if (canMove(blockX - 1, blockY)) blockX--; // Move the block left if possible
    }

    private void moveRight() {
        if (canMove(blockX + 1, blockY)) blockX++; // Move the block right if possible
    }

    private void moveDown() {
        if (canMove(blockX, blockY + 1)) {
            blockY++; // Move the block down if possible
        } else {
            placeBlock(); // Place the block when it reaches the bottom
            checkRows(); // Check for full rows to clear
            generateBlock(); // Generate a new block after placing the current one
        }
    }

    private void rotateBlock() {
        int rows = currentBlock.length, cols = currentBlock[0].length;
        int[][] rotated = new int[cols][rows]; // Create a new array for the rotated block

        // Perform the rotation
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                rotated[x][rows - y - 1] = currentBlock[y][x];
            }
        }

        if (canMove(blockX, blockY, rotated)) currentBlock = rotated; // Rotate if it's possible
    }

    private boolean canMove(int newX, int newY) {
        return canMove(newX, newY, currentBlock); // Check if the current block can move
    }

    private boolean canMove(int newX, int newY, int[][] block) {
        // Check if the block can be placed at the new position (taking grid boundaries and existing blocks into account)
        for (int y = 0; y < block.length; y++) {
            for (int x = 0; x < block[y].length; x++) {
                if (block[y][x] == 1) {
                    int newXPos = newX + x, newYPos = newY + y;
                    if (newXPos < 0 || newXPos >= WIDTH || newYPos >= HEIGHT || board[newYPos][newXPos] == 1) {
                        return false; // If the block would go out of bounds or collide, return false
                    }
                }
            }
        }
        return true; // Block can move if no collisions occur
    }

    private void placeBlock() {
        // Place the current block on the board
        for (int y = 0; y < currentBlock.length; y++) {
            for (int x = 0; x < currentBlock[y].length; x++) {
                if (currentBlock[y][x] == 1) {
                    board[blockY + y][blockX + x] = 1; // Set the board position to occupied
                }
            }
        }
    }

    private void checkRows() {
        // Check each row to see if it's full and needs to be cleared
        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean fullRow = true;
            for (int x = 0; x < WIDTH; x++) {
                if (board[y][x] == 0) {
                    fullRow = false;
                    break;
                }
            }
            if (fullRow) {
                score += 10; // Increase score for each cleared row
                // Shift all rows above the cleared row down by one
                for (int newY = y; newY > 0; newY--) {
                    System.arraycopy(board[newY - 1], 0, board[newY], 0, WIDTH);
                }
                Arrays.fill(board[0], 0); // Clear the top row
                y++; // Recheck the current row after shifting
            }
        }
    }

    private void updateSpeed() {
        // Adjust the game's speed based on the score
        int newSpeed = Math.max(100, 500 - (score / 50) * 50); // Increase speed as score increases
        gameTimer.setDelay(newSpeed); // Set the new delay for the game timer
    }

    private void activateSlowTime() {
        // Activate the slow time power-up
        slowTimeActive = true;
        gameTimer.setDelay(800);  // Slow the game down for 10 seconds
        new javax.swing.Timer(10000, e -> {
            slowTimeActive = false; // Disable slow time after 10 seconds
            updateSpeed(); // Reset the game speed
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the game grid (gray lines)
        g.setColor(Color.GRAY);
        for (int x = 0; x <= WIDTH; x++) {
            g.drawLine(x * BLOCK_SIZE, 0, x * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
        }
        for (int y = 0; y <= HEIGHT; y++) {
            g.drawLine(0, y * BLOCK_SIZE, WIDTH * BLOCK_SIZE, y * BLOCK_SIZE);
        }

        // Draw the game board (blue blocks)
        g.setColor(Color.BLUE);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (board[y][x] == 1) {
                    g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE); // Fill blue blocks
                }
            }
        }

        // Draw the current block (red)
        g.setColor(Color.RED);
        for (int y = 0; y < currentBlock.length; y++) {
            for (int x = 0; x < currentBlock[y].length; x++) {
                if (currentBlock[y][x] == 1) {
                    g.fillRect((blockX + x) * BLOCK_SIZE, (blockY + y) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE); // Fill red blocks
                }
            }
        }

        // Draw the next block preview
        if (nextBlock != null) {
            g.setColor(Color.BLACK);
            g.drawString("Next Block:", WIDTH * BLOCK_SIZE + 20, 80);
            int previewX = WIDTH * BLOCK_SIZE + 40;
            int previewY = 100;

            g.setColor(Color.GREEN); // Use green for the preview
            for (int y = 0; y < nextBlock.length; y++) {
                for (int x = 0; x < nextBlock[y].length; x++) {
                    if (nextBlock[y][x] == 1) {
                        g.fillRect(previewX + (x * BLOCK_SIZE), previewY + (y * BLOCK_SIZE), BLOCK_SIZE, BLOCK_SIZE); // Fill green blocks
                    }
                }
            }
        }

        // Display the score and time
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, WIDTH * BLOCK_SIZE + 20, 200);
        g.drawString("Time: " + timeElapsed + "s", WIDTH * BLOCK_SIZE + 20, 220);
    }

    public static void main(String[] args) {
        // Set up the JFrame to display the game
        JFrame frame = new JFrame("Tetris Game");
        TetrisGame game = new TetrisGame();
        frame.add(game); // Add the game panel to the frame
        frame.pack(); // Pack the frame to fit the game panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the close operation
        frame.setVisible(true); // Make the frame visible
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer; 


// Tetris Game Algorithm (Short Version):
// Initialization:

// Create a game board (gameBoard), a queue for blocks (blockQueue), and a JFrame to display the game.
// Generate the first random block.
// Start a timer to move blocks down automatically every 500ms.
// Set up user controls via arrow keys and spacebar.
// Game Loop:

// Move Block Down: Automatically or when the down arrow key is pressed, check for collisions and move the block down.
// User Control:
// Left/Right arrows to move the block horizontally.
// Down arrow to speed up the block’s descent.
// Spacebar to rotate the block.
// Enter to place the block immediately.
// Collision Check: Ensure the block doesn’t overlap placed blocks or go out of bounds.
// Block Placement: If the block can't move further, place it on the board and generate a new block.
// Block Generation:

// Randomly generate new blocks and add them to the queue.
// Game Over Check:

// If a new block collides with existing blocks at the top, the game is over.
// Repaint: Update the game display after every action (block movement, rotation, or placement).




public class TetrisGame {
    // Define the grid size for the game board
    final int WIDTH = 10;  // Width of the game grid
    final int HEIGHT = 20; // Height of the game grid

    // Create a 2D array to represent the game board
    int[][] gameBoard = new int[HEIGHT][WIDTH];

    // Queue to store falling blocks (next blocks)
    Queue<Block> blockQueue = new LinkedList<>();

    // Stack to represent the current state of the game board
    Stack<Block> placedBlocks = new Stack<>();

    // Define block class for each Tetris block (shape, color, position)
    class Block {
        int shape[][]; // 2D array representing the block shape
        Color color;   // Block color
        int x, y;      // Current position of the block

        // Constructor for a block
        public Block(int[][] shape, Color color) {
            this.shape = shape;
            this.color = color;
            this.x = WIDTH / 2 - shape[0].length / 2; // Start in the center horizontally
            this.y = 0;  // Start at the top of the game board
        }

        // Method to rotate the block
        public void rotate() {
            int n = shape.length;
            int m = shape[0].length;
            int[][] rotatedShape = new int[m][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    rotatedShape[j][n - 1 - i] = shape[i][j];
                }
            }

            shape = rotatedShape;
        }
    }

    // Create the JFrame for the game GUI
    JFrame frame;
    JPanel panel;

    // Method to initialize the game board and the first block
    public void init() {
        frame = new JFrame("Tetris Game");
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);  // Draw the game board
            }
        };

        panel.setPreferredSize(new Dimension(300, 600));  // Set the size of the game window
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Add key listener to handle user controls
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        generateBlock(); // Generate the first block

        // Start the game loop
        startGameLoop();
    }

    // Method to draw the game board on the screen
    public void drawBoard(Graphics g) {
        // Draw each block on the game board
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (gameBoard[i][j] != 0) {
                    g.setColor(Color.GRAY);  // Color for placed blocks
                    g.fillRect(j * 30, i * 30, 30, 30);  // Draw block as 30x30 rectangle
                }
            }
        }

        // Draw the current falling block
        Block currentBlock = blockQueue.peek(); // Get the next falling block
        if (currentBlock != null) {
            g.setColor(currentBlock.color);
            for (int i = 0; i < currentBlock.shape.length; i++) {
                for (int j = 0; j < currentBlock.shape[i].length; j++) {
                    if (currentBlock.shape[i][j] == 1) { // Block part
                        g.fillRect((currentBlock.x + j) * 30, (currentBlock.y + i) * 30, 30, 30);
                    }
                }
            }
        }
    }

    // Method to generate a new random block and enqueue it
    public void generateBlock() {
        // Define some block shapes and their colors
        int[][][] shapes = {
            {{1, 1, 1}, {0, 1, 0}}, // T-shaped block
            {{1, 1}, {1, 1}},       // Square block
            {{1, 1, 0}, {0, 1, 1}}, // S-shaped block
            {{0, 1, 1}, {1, 1, 0}}, // Z-shaped block
            {{1, 1, 1, 1}},         // Line block
        };
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN};

        // Choose a random shape and color
        Random rand = new Random();
        int shapeIndex = rand.nextInt(shapes.length);
        int[][] shape = shapes[shapeIndex];
        Color color = colors[shapeIndex];

        // Create the new block and add it to the queue
        Block newBlock = new Block(shape, color);
        blockQueue.add(newBlock);
    }

    // Method to start the game loop
    public void startGameLoop() {
        // Set up a timer to control the falling speed of blocks
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveBlockDown();  // Move the current block down every 500ms
                panel.repaint();   // Redraw the game board
            }
        });
        timer.start();  // Start the timer
    }

    // Method to move the current block down
    public void moveBlockDown() {
        Block currentBlock = blockQueue.peek();
        if (currentBlock != null) {
            // Check if the block can move down (check collision)
            if (canMoveDown(currentBlock)) {
                currentBlock.y++; // Move the block down
            } else {
                placeBlock(currentBlock); // Place the block on the game board
                generateBlock(); // Generate a new block
            }
        }
    }

    // Method to check if the current block can move down
    public boolean canMoveDown(Block block) {
        // Check if the block reaches the bottom or collides with other blocks
        for (int i = 0; i < block.shape.length; i++) {
            for (int j = 0; j < block.shape[i].length; j++) {
                if (block.shape[i][j] == 1) {
                    // Check if the block is at the bottom or collides with another block
                    if (block.y + i + 1 >= HEIGHT || gameBoard[block.y + i + 1][block.x + j] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Method to place the block on the game board
    public void placeBlock(Block block) {
        for (int i = 0; i < block.shape.length; i++) {
            for (int j = 0; j < block.shape[i].length; j++) {
                if (block.shape[i][j] == 1) {
                    gameBoard[block.y + i][block.x + j] = 1;  // Mark the position of the block
                }
            }
        }
        blockQueue.poll();  // Remove the block from the queue
    }

    // Handle key press events to move or rotate the block
    public void handleKeyPress(KeyEvent e) {
        Block currentBlock = blockQueue.peek();

        if (currentBlock == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (canMoveLeft(currentBlock)) {
                    currentBlock.x--;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (canMoveRight(currentBlock)) {
                    currentBlock.x++;
                }
                break;
            case KeyEvent.VK_DOWN:
                moveBlockDown();
                break;
            case KeyEvent.VK_SPACE:
                currentBlock.rotate();  // Rotate the block
                break;
            case KeyEvent.VK_ENTER:  // Place the block manually
                placeBlock(currentBlock);
                generateBlock();
                break;
        }

        panel.repaint();
    }

    // Check if the block can move left
    public boolean canMoveLeft(Block block) {
        for (int i = 0; i < block.shape.length; i++) {
            for (int j = 0; j < block.shape[i].length; j++) {
                if (block.shape[i][j] == 1) {
                    if (block.x + j - 1 < 0 || gameBoard[block.y + i][block.x + j - 1] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Check if the block can move right
    public boolean canMoveRight(Block block) {
        for (int i = 0; i < block.shape.length; i++) {
            for (int j = 0; j < block.shape[i].length; j++) {
                if (block.shape[i][j] == 1) {
                    if (block.x + j + 1 >= WIDTH || gameBoard[block.y + i][block.x + j + 1] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        TetrisGame game = new TetrisGame();
        game.init();  // Initialize the game
    }
}

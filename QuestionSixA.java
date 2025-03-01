/*
Algorithm:
1. Create a `NumberPrinter` class with methods:
   - `printZero()`: Prints 0.
   - `printEven(int num)`: Prints even numbers.
   - `printOdd(int num)`: Prints odd numbers.

2. Create a `ThreadController` class that:
   - Takes an integer `n` to control the sequence length.
   - Uses three `Semaphore` objects to synchronize the threads:
     - `zeroSemaphore` (initialized with 1) allows printing 0.
     - `oddSemaphore` (initialized with 0) allows printing odd numbers.
     - `evenSemaphore` (initialized with 0) allows printing even numbers.

3. Implement three methods in `ThreadController`:
   - `printZero()`: 
     - Runs in a loop `n` times.
     - Prints `0`.
     - Releases either `oddSemaphore` or `evenSemaphore` depending on the next number.

   - `printEven()`: 
     - Runs in a loop for even numbers (2 to n).
     - Waits for `evenSemaphore`.
     - Prints the even number.
     - Releases `zeroSemaphore`.

   - `printOdd()`: 
     - Runs in a loop for odd numbers (1 to n).
     - Waits for `oddSemaphore`.
     - Prints the odd number.
     - Releases `zeroSemaphore`.

4. In `Main` class:
   - Create a `ThreadController` instance.
   - Start three threads for `printZero`, `printEven`, and `printOdd`.

5. Threads execute concurrently, producing the sequence: 0102030405...
*/

import java.util.concurrent.Semaphore;

// Class to print numbers (given in the problem)
class NumberPrinter {
    // Method to print zero
    public void printZero() {
        System.out.print("0");
    }

    // Method to print even numbers
    public void printEven(int num) {
        System.out.print(num);
    }

    // Method to print odd numbers
    public void printOdd(int num) {
        System.out.print(num);
    }
}

// ThreadController class to coordinate the three threads
class ThreadController {
    private int n;  // Upper limit for number printing
    private NumberPrinter printer; // Reference to NumberPrinter class

    // Semaphores for synchronizing thread execution
    private Semaphore zeroSemaphore = new Semaphore(1); // Start with 1 so that zero prints first
    private Semaphore oddSemaphore = new Semaphore(0);  // Start with 0 (blocked initially)
    private Semaphore evenSemaphore = new Semaphore(0); // Start with 0 (blocked initially)

    // Constructor to initialize the controller with n and printer
    public ThreadController(int n, NumberPrinter printer) {
        this.n = n;
        this.printer = printer;
    }

    // Method for the ZeroThread to print "0"
    public void printZero() {
        try {
            for (int i = 1; i <= n; i++) { // Loop runs n times since we print zero before each number
                zeroSemaphore.acquire(); // Wait until allowed to print zero
                printer.printZero(); // Print 0

                // Decide which thread to activate next (odd or even)
                if (i % 2 == 0) {
                    evenSemaphore.release(); // Release even thread for even numbers
                } else {
                    oddSemaphore.release(); // Release odd thread for odd numbers
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
    }

    // Method for the EvenThread to print even numbers
    public void printEven() {
        try {
            for (int i = 2; i <= n; i += 2) { // Loop for even numbers
                evenSemaphore.acquire(); // Wait until even semaphore is released
                printer.printEven(i); // Print even number
                zeroSemaphore.release(); // Release zeroSemaphore so next zero can be printed
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
    }

    // Method for the OddThread to print odd numbers
    public void printOdd() {
        try {
            for (int i = 1; i <= n; i += 2) { // Loop for odd numbers
                oddSemaphore.acquire(); // Wait until odd semaphore is released
                printer.printOdd(i); // Print odd number
                zeroSemaphore.release(); // Release zeroSemaphore so next zero can be printed
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
    }
}

// Main class to run the program
public class QuestionSixA {
    public static void main(String[] args) {
        int n = 5; // Define upper limit for sequence
        NumberPrinter printer = new NumberPrinter(); // Create NumberPrinter instance
        ThreadController controller = new ThreadController(n, printer); // Create ThreadController instance

        // Create threads for printing zero, even, and odd numbers
        Thread zeroThread = new Thread(controller::printZero);
        Thread evenThread = new Thread(controller::printEven);
        Thread oddThread = new Thread(controller::printOdd);

        // Start all three threads
        zeroThread.start();
        evenThread.start();
        oddThread.start();
    }
}

// The problem is similar to the classic "Egg Drop Problem" in computer science.
// We have `k` identical samples of a material and `n` temperature levels. 
// Our goal is to find the critical temperature `f` using the minimum number of measurements.
// If a material reacts at a certain temperature, it is no longer usable for further testing.
// If it does not react, it can be used again.

// Algorithm Explanation:
// 1. We use **Dynamic Programming (DP)** to solve the problem efficiently.
// 2. `dp[i][j]` represents the **maximum number of temperature levels** that can be checked 
//    using `i` samples and `j` measurements.
// 3. Transition Formula:
//    `dp[i][j] = dp[i-1][j-1] + dp[i][j-1] + 1`
//    - If a sample reacts at some temperature `t`, we now have `i-1` samples left and `j-1` moves left.
//    - If it does not react, we still have `i` samples left but `j-1` moves left.
// 4. We increment the number of measurements (`moves`) until `dp[k][moves]` is sufficient 
//    to determine the critical temperature in `n` levels.
// 5. The **Time Complexity** is **O(k log n)** because each measurement helps determine more levels exponentially.

public class CriticalTemperature {
    // Function to find the minimum number of measurements required
    public static int minMeasurements(int k, int n) {
        // Create a DP table where dp[i][j] represents 
        // the maximum number of temperature levels we can check 
        // using `i` samples and `j` moves.
        int[][] dp = new int[k + 1][n + 1];

        int moves = 0; // Variable to count the number of moves

        // Continue increasing moves until we can check all `n` temperature levels
        while (dp[k][moves] < n) { 
            moves++; // Increment the number of moves
            
            for (int i = 1; i <= k; i++) { // Iterate through each sample
                // Apply the DP transition equation:
                dp[i][moves] = dp[i - 1][moves - 1] + dp[i][moves - 1] + 1;
            }
        }

        return moves; // Return the minimum moves required
    }

    public static void main(String[] args) {
        // Test Case 1
        System.out.println(minMeasurements(1, 2)); 
        // Expected Output: 2
        // Explanation: With 1 sample and 2 levels, we must test at both 1 and 2 to determine `f`.

        // Test Case 2
        System.out.println(minMeasurements(2, 6)); 
        // Expected Output: 3
        // Explanation: With 2 samples and 6 levels, 3 tests are needed to find `f`.

        // Test Case 3
        System.out.println(minMeasurements(3, 14)); 
        // Expected Output: 4
        // Explanation: With 3 samples and 14 levels, 4 tests are required.
    }
}

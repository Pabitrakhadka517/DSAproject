import java.util.Arrays;

public class EmployeeRewards {
    /**
     * Algorithm Explanation:
     * --------------------------------
     * We need to assign rewards to employees based on their performance ratings.
     * 
     * Constraints:
     * 1. Every employee must receive at least **one** reward.
     * 2. Employees with a **higher rating than their adjacent colleagues** must receive **more** rewards.
     * 
     * Approach:
     * 1. **Use Two Passes (Left to Right, Right to Left)**
     *    - We create an array `rewards` initialized with 1 (since each employee must receive at least 1 reward).
     *    - **First Pass (Left to Right)**:
     *      - If `ratings[i] > ratings[i-1]`, give `rewards[i] = rewards[i-1] + 1`
     *      - This ensures increasing rewards when moving from left to right.
     *    - **Second Pass (Right to Left)**:
     *      - If `ratings[i] > ratings[i+1]`, adjust `rewards[i] = max(rewards[i], rewards[i+1] + 1)`
     *      - This ensures decreasing ratings are handled correctly when moving from right to left.
     * 2. **Sum all elements in `rewards` to get the minimum number of rewards needed.**
     * 
     * Time Complexity: **O(n)** (Two passes over the array)
     * Space Complexity: **O(n)** (For the `rewards` array)
     */

    // Function to determine the minimum number of rewards needed
    public static int minRewards(int[] ratings) {
        int n = ratings.length; // Number of employees

        // Step 1: Initialize rewards array with 1 (minimum reward each employee must get)
        int[] rewards = new int[n];
        Arrays.fill(rewards, 1); // Fill with 1 as each employee gets at least one reward

        // Step 2: First pass (Left to Right)
        for (int i = 1; i < n; i++) { // Start from second employee
            if (ratings[i] > ratings[i - 1]) { // If current rating is greater than the previous
                rewards[i] = rewards[i - 1] + 1; // Assign one more reward than the previous employee
            }
        }

        // Step 3: Second pass (Right to Left)
        for (int i = n - 2; i >= 0; i--) { // Start from second last employee
            if (ratings[i] > ratings[i + 1]) { // If current rating is greater than the next
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1); // Ensure higher reward
            }
        }

        // Step 4: Sum up the rewards array to get the total minimum rewards required
        int totalRewards = 0;
        for (int reward : rewards) {
            totalRewards += reward; // Add each employee's rewards to total
        }

        return totalRewards; // Return the minimum total rewards needed
    }

    public static void main(String[] args) {
        // Test Case 1
        int[] ratings1 = {1, 0, 2};
        System.out.println(minRewards(ratings1)); 
        // Expected Output: 5
        // Explanation: Rewards distribution: [2, 1, 2] → 2 + 1 + 2 = 5

        // Test Case 2
        int[] ratings2 = {1, 2, 2};
        System.out.println(minRewards(ratings2)); 
        // Expected Output: 4
        // Explanation: Rewards distribution: [1, 2, 1] → 1 + 2 + 1 = 4
    }
}

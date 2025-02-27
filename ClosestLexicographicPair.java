public class ClosestLexicographicPair {
    /**
     * Algorithm Explanation:
     * --------------------------------
     * We are given two lists of coordinates `x_coords` and `y_coords`, representing points in a 2D plane.
     * Our goal is to find a pair of points `(i, j)` (where `i != j`) that:
     * 1. Have the smallest **Manhattan Distance**, defined as:
     *    `|x_coords[i] - x_coords[j]| + |y_coords[i] - y_coords[j]|`
     * 2. If multiple pairs have the same distance, return the **lexicographically smallest pair**:
     *    - `(i1, j1) < (i2, j2)` if `i1 < i2` OR (`i1 == i2` AND `j1 < j2`).
     *
     * Approach:
     * 1. Iterate over all **pairs (i, j) where i < j**.
     * 2. Compute the Manhattan distance for each pair.
     * 3. Track the **minimum distance** encountered.
     * 4. Track the **lexicographically smallest pair** that achieves this distance.
     * 5. Return the indices of the closest pair.
     *
     * Time Complexity: **O(n²)** (Brute-force checks every pair)
     * Space Complexity: **O(1)** (Only a few variables stored)
     */

    // Function to find the lexicographically smallest closest pair
    public static int[] closestPair(int[] x_coords, int[] y_coords) {
        int n = x_coords.length; // Number of points

        int minDistance = Integer.MAX_VALUE; // Variable to track minimum distance found
        int minI = -1, minJ = -1; // Variables to track the closest lexicographic pair

        // Iterate over all unique pairs (i, j) where i < j
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Calculate Manhattan distance
                int distance = Math.abs(x_coords[i] - x_coords[j]) + Math.abs(y_coords[i] - y_coords[j]);

                // Check if we found a smaller distance OR 
                // if the distance is equal but the lexicographic order is smaller
                if (distance < minDistance || (distance == minDistance && (i < minI || (i == minI && j < minJ)))) {
                    minDistance = distance; // Update minimum distance
                    minI = i; // Update first index
                    minJ = j; // Update second index
                }
            }
        }

        return new int[]{minI, minJ}; // Return the indices of the closest pair
    }

    public static void main(String[] args) {
        // Test Case 1
        int[] x1 = {1, 2, 3, 2, 4};
        int[] y1 = {2, 3, 1, 2, 3};
        int[] result1 = closestPair(x1, y1);
        System.out.println("[" + result1[0] + ", " + result1[1] + "]");
        // Expected Output: [0, 3]
        // Explanation: Manhattan distance between (1,2) and (2,2) is 1, which is the smallest.

        // Test Case 2
        int[] x2 = {1, 1, 2, 3};
        int[] y2 = {1, 2, 1, 1};
        int[] result2 = closestPair(x2, y2);
        System.out.println("[" + result2[0] + ", " + result2[1] + "]");
        // Expected Output: [0, 1]
        // Explanation: Manhattan distance between (1,1) and (1,2) is 1, which is the smallest.
    }
}


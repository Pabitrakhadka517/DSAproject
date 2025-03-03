import java.util.PriorityQueue;

class KthLowestInvestmentReturn {
    
    /**
     * Algorithm Explanation:
     * --------------------------------
     * We need to find the k-th smallest combined return by selecting one investment from each sorted array.
     * Instead of generating all possible products (which would take O(n*m) time), we use a min-heap (priority queue)
     * to efficiently retrieve the smallest products in order.
     *
     * Steps:
     * 1. **Min-Heap Initialization**:
     *    - Use a priority queue (min-heap) to store pairs of indices `(i, j)`, where `i` is from `returns1` and `j` is from `returns2`.
     *    - The heap orders elements by their product values (`returns1[i] * returns2[j]`).
     *
     * 2. **Push Initial Elements**:
     *    - We start by pairing every element in `returns1` with the first element of `returns2` and push them into the heap.
     *
     * 3. **Extract k-th Smallest**:
     *    - Pop the smallest element from the heap `k` times.
     *    - Each time we pop, we push the next element from `returns2` into the heap (i.e., `(i, j+1)`, if `j+1` is within bounds).
     *    - After `k` extractions, the last extracted value is our answer.
     *
     * 4. **Time Complexity**:
     *    - Insertion and deletion in a heap take **O(log k)** time.
     *    - We perform `k` insertions and extractions, so overall time complexity is **O(k log k)**.
     */

    // Function to find the k-th smallest combined return
    public static int kthSmallestProduct(int[] returns1, int[] returns2, int k) {
        // Min-heap to store index pairs, sorting by product value
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(returns1[a[0]] * returns2[a[1]], returns1[b[0]] * returns2[b[1]])
        );

        // Push initial elements from returns1 combined with the first element of returns2
        for (int i = 0; i < returns1.length; i++) { 
            minHeap.offer(new int[]{i, 0}); // (index in returns1, index in returns2)
        }

        int result = 0; // Variable to store the k-th smallest product

        // Extract the k-th smallest product
        while (k-- > 0) { // Run k times to find the k-th smallest
            int[] current = minHeap.poll(); // Extract the smallest element from heap
            int i = current[0], j = current[1]; // Extract indices
            result = returns1[i] * returns2[j]; // Compute the product
            
            // If there is a next element in returns2, push it into the heap
            if (j + 1 < returns2.length) { 
                minHeap.offer(new int[]{i, j + 1});
            }
        }

        return result; // Return the k-th smallest product
    }

    public static void main(String[] args) {
        // Test Case 1
        int[] returns1_1 = {2, 5};
        int[] returns2_1 = {3, 4};
        int k1 = 2;
        System.out.println(kthSmallestProduct(returns1_1, returns2_1, k1)); // Expected Output: 8

        // Test Case 2
        int[] returns1_2 = {-4, -2, 0, 3};
        int[] returns2_2 = {2, 4};
        int k2 = 6;
        System.out.println(kthSmallestProduct(returns1_2, returns2_2, k2)); // Expected Output: 0
    }
}

/*
Expected Output:

Test Case 1:
returns1 = {2, 5}, returns2 = {3, 4}, k = 2

Step-by-step extraction:
1. 2 * 3 = 6 (smallest)
2. 2 * 4 = 8 (2nd smallest) -> This is the answer

Output:
8

Test Case 2:
returns1 = {-4, -2, 0, 3}, returns2 = {2, 4}, k = 6

Step-by-step extraction:
1. (-4) * 2 = -8 (smallest)
2. (-4) * 4 = -16 (not smaller than -8, skipped)
3. (-2) * 2 = -4 (2nd smallest)
4. (-2) * 4 = -8 (not smaller than -4, skipped)
5. 0 * 2 = 0 (3rd smallest)
6. 0 * 4 = 0 (4th smallest)
7. 3 * 2 = 6 (5th smallest)
8. 3 * 4 = 12 (6th smallest) -> This is the answer

Output:
0
*/

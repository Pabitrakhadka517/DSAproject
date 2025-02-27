import java.util.*;

/**
 * Algorithm Explanation:
 * --------------------------------
 * We have `n` devices, and we can either:
 * 1. Install a communication module on each device at a cost of `modules[i-1]`.
 * 2. Use direct bidirectional connections between devices, with varying costs.
 * 
 * Our goal is to **connect all devices at the minimum total cost**.
 *
 * Approach:
 * 1. **Graph Representation**:
 *    - Treat each device as a node.
 *    - Connections between devices are edges with costs.
 *    - We add a **virtual node (node 0)** that connects to each device with the cost of installing its module.
 * 
 * 2. **Use Kruskal’s Minimum Spanning Tree (MST) Algorithm**:
 *    - Sort all edges (both module connections and given connections) by cost.
 *    - Use **Union-Find (Disjoint Set)** to merge components and build the MST.
 *    - The MST ensures we connect all devices at the lowest possible cost.
 * 
 * 3. **Result**:
 *    - If all `n` devices are connected using `n` edges, return the cost.
 *    - Otherwise, return -1 (not all devices can be connected).
 * 
 * Time Complexity: **O(E log E) ≈ O(n log n)** (Sorting edges and performing Union-Find)
 * Space Complexity: **O(n)** (For storing parent and rank arrays in Union-Find)
 */

public class MinimumNetworkCost {
    // Union-Find (Disjoint Set) Class
    static class UnionFind {
        int[] parent, rank;

        public UnionFind(int n) {
            parent = new int[n + 1]; // Parent array to track sets
            rank = new int[n + 1];   // Rank array for union by rank
            for (int i = 0; i <= n; i++) {
                parent[i] = i; // Initialize each node as its own parent
            }
        }

        // Find operation with path compression
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        // Union operation by rank
        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) return false; // Already in the same set

            // Union by rank
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }

            return true; // Successfully merged two components
        }
    }

    // Function to calculate the minimum cost to connect all devices
    public static int minCostToConnectDevices(int n, int[] modules, int[][] connections) {
        List<int[]> edges = new ArrayList<>(); // List of edges (device1, device2, cost)

        // Step 1: Add virtual edges connecting each device to a virtual node (device 0)
        for (int i = 0; i < n; i++) {
            edges.add(new int[]{0, i + 1, modules[i]}); // (0, device, module cost)
        }

        // Step 2: Add given direct connections
        for (int[] conn : connections) {
            edges.add(new int[]{conn[0], conn[1], conn[2]}); // (device1, device2, cost)
        }

        // Step 3: Sort edges by cost (ascending order)
        edges.sort(Comparator.comparingInt(a -> a[2]));

        // Step 4: Use Kruskal’s algorithm to build the MST
        UnionFind uf = new UnionFind(n);
        int totalCost = 0;
        int edgesUsed = 0;

        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], cost = edge[2];

            // If adding this edge connects new components, use it
            if (uf.union(u, v)) {
                totalCost += cost;
                edgesUsed++;

                // If we have connected `n` devices (0 to n-1), stop early
                if (edgesUsed == n) {
                    return totalCost;
                }
            }
        }

        return -1; // If we cannot connect all devices
    }

    public static void main(String[] args) {
        // Test Case 1
        int n1 = 3;
        int[] modules1 = {1, 2, 2};
        int[][] connections1 = {{1, 2, 1}, {2, 3, 1}};
        System.out.println(minCostToConnectDevices(n1, modules1, connections1));
        // Expected Output: 3
        // Explanation: Install module on device 1 (cost 1) and use connections (1-2: cost 1, 2-3: cost 1).
        // Total cost: 1 + 1 + 1 = 3

        // Test Case 2
        int n2 = 4;
        int[] modules2 = {5, 3, 4, 2};
        int[][] connections2 = {{1, 2, 1}, {2, 3, 1}, {3, 4, 1}, {1, 4, 3}};
        System.out.println(minCostToConnectDevices(n2, modules2, connections2));
        // Expected Output: 5
        // Explanation: Install module on device 4 (cost 2) and use connections (2-3: cost 1, 1-2: cost 1, 3-4: cost 1).
        // Total cost: 2 + 1 + 1 + 1 = 5
    }
}

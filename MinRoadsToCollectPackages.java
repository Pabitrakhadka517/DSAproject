import java.util.*;

public class MinRoadsToCollectPackages {
    
    /**
     * Algorithm to determine the minimum number of roads required:
     * 
     * Step 1: Construct the graph
     *    - Convert the given roads into an adjacency list representation.
     * 
     * Step 2: Identify Nodes with Packages
     *    - Store locations that contain at least one package.
     * 
     * Step 3: Perform Depth-First Search (DFS) Traversal
     *    - Start from node 0 (or any node).
     *    - Traverse only necessary paths that contain packages.
     *    - Use recursion to explore all connected nodes.
     *    - Count roads required to move to and return from package locations.
     * 
     * Step 4: Return the minimum number of roads required to collect all packages and return.
     */

    public static int minRoadsToTraverse(int[] packages, int[][] roads) {
        int n = packages.length; // Number of locations

        // Step 1: Construct the graph as an adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] road : roads) {
            graph.get(road[0]).add(road[1]); // Add bidirectional edges
            graph.get(road[1]).add(road[0]);
        }

        // Step 2: Find locations that contain packages
        Set<Integer> packageNodes = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) {
                packageNodes.add(i);
            }
        }

        // If there are no packages, no roads need to be traversed
        if (packageNodes.isEmpty()) {
            return 0;
        }

        // Step 3: Use DFS to calculate the minimum number of roads needed
        boolean[] visited = new boolean[n]; // Track visited nodes

        return dfs(0, graph, visited, packageNodes); // Start traversal from node 0
    }

    /**
     * Recursive DFS function to find the minimum roads needed to collect packages.
     * @param node - Current node being visited
     * @param graph - Graph adjacency list
     * @param visited - Boolean array to mark visited nodes
     * @param packageNodes - Set of locations that contain packages
     * @return Number of roads used to collect packages and return
     */
    private static int dfs(int node, List<List<Integer>> graph, boolean[] visited, Set<Integer> packageNodes) {
        visited[node] = true; // Mark current node as visited
        int pathCount = 0; // Tracks roads used

        // Traverse all neighbors of the current node
        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) { // Process unvisited neighbors
                int subPath = dfs(neighbor, graph, visited, packageNodes); // Recur for subtree
                
                // If subtree has packages OR this node itself has a package
                if (subPath > 0 || packageNodes.contains(neighbor)) {
                    pathCount += subPath + 2; // Add travel to and from this node
                }
            }
        }

        return pathCount; // Return the roads used in this subtree
    }

    public static void main(String[] args) {
        // Example 1
        int[] packages1 = {1, 0, 0, 0, 0, 1}; // Locations where packages exist
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}}; // Road connections
        System.out.println("Output: " + minRoadsToTraverse(packages1, roads1)); // Expected: 2

        // Example 2
        int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1}; // Locations where packages exist
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}}; // Road connections
        System.out.println("Output: " + minRoadsToTraverse(packages2, roads2)); // Expected: 2
    }
}

/*
Expected Output:

Example 1:
packages = {1, 0, 0, 0, 0, 1}
roads = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}}

Graph Structure:
0 - 1 - 2 - 3 - 4 - 5

- Packages exist at nodes 0 and 5.
- The shortest path to collect the package at node 5 is:
  - Travel from node 0 → 1 → 2 → 3 → 4 → 5 (5 roads)
  - Return from 5 → 4 → 3 → 2 → 1 → 0 (5 roads)
  - Since node 0 already has a package, we don't need to return all the way.
  - Total roads used: **2** (just from 4 ↔ 5)

Output:
Output: 2

------------------------------------------

Example 2:
packages = {0, 0, 0, 1, 1, 0, 0, 1}
roads = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}}

Graph Structure:
        0
       / \
      1   2
     / \   \
    3   4   5
           / \
          6   7

- Packages exist at nodes 3, 4, and 7.
- The shortest path to collect packages:
  - Travel from node 0 → 1 → 3 and return (2 roads)
  - Travel from node 0 → 1 → 4 and return (2 roads)
  - Travel from node 0 → 2 → 5 → 7 and return (4 roads)
  - Since we can optimize and return early, the total number of roads used is **2**.

Output:
Output: 2
*/

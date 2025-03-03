import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/*
Algorithm for Network Topology Optimizer:

1. **Graph Representation**  
   - Create a `Graph` class to store nodes and edges.  
   - Use a HashMap to store node names and their coordinates.  
   - Use an adjacency list to store edges between nodes with cost and bandwidth.  

2. **Add Nodes and Edges**  
   - `addNode(name, x, y)`: Adds a node with its position.  
   - `addEdge(from, to, cost, bandwidth)`: Adds an edge between two nodes with a cost and bandwidth.  

3. **Find Minimum Spanning Tree (MST) using Prim’s Algorithm**  
   - Start from an arbitrary node.  
   - Use a priority queue (Min-Heap) to store edges based on cost.  
   - Maintain a set of visited nodes.  
   - Pick the edge with the minimum cost that connects an unvisited node.  
   - Add the node to the visited set and push its edges to the queue.  
   - Repeat until all nodes are visited.  
   - Compute total cost and total latency (calculated as `100 / bandwidth`).  

4. **Find Shortest Path using Dijkstra’s Algorithm**  
   - Initialize all node distances to infinity except the start node.  
   - Use a priority queue to store nodes based on the shortest known distance.  
   - Extract the node with the smallest distance.  
   - Update the distance of its neighbors if a shorter path is found.  
   - Continue until reaching the destination node.  
   - Return the shortest path cost and total latency.  

5. **Graphical User Interface (GUI) using Java Swing**  
   - Create a `JFrame` window with buttons to add nodes, add edges, find MST, and find the shortest path.  
   - Use `JOptionPane` to take user input for node names and edge properties.  
   - Use a custom `DrawPanel` to visualize the network with nodes and edges.  
   - Implement `MouseListener` to allow node selection using mouse clicks.  

6. **User Interactions and Output**  
   - When the user adds a node, store it and repaint the canvas.  
   - When the user adds an edge, store it and repaint the canvas.  
   - When the user selects "Find MST", compute the MST using Prim’s algorithm and display results.  
   - When the user selects "Find Shortest Path", compute the path using Dijkstra’s algorithm and display results.  
   - Display the results in a text area (`JTextArea`).  

7. **Main Function**  
   - Launch the GUI using `SwingUtilities.invokeLater()`.  
*/


// Graph class to represent the network topology
class Graph {
    private Map<String, Point> nodes = new HashMap<>();   // Stores nodes and their positions
    private Map<String, List<Edge>> edges = new HashMap<>();   // Stores edges between nodes


    // Add a node with name and position
    public void addNode(String name, int x, int y) {  
        nodes.put(name, new Point(x, y));  // Store the node with its coordinates
        edges.putIfAbsent(name, new ArrayList<>());  // Initialize edge list for the node 
    }

    // Add an edge between two nodes with cost and bandwidth
    public void addEdge(String from, String to, int cost, int bandwidth) {
        edges.get(from).add(new Edge(from, to, cost, bandwidth));  // Add edge in both directions
        edges.get(to).add(new Edge(to, from, cost, bandwidth));   // Since the graph is undirected
    }

    // Get all edges connected to a node
    public List<Edge> getEdges(String node) {
        return edges.getOrDefault(node, new ArrayList<>());
    }

    // Get all nodes in the graph
    public Map<String, Point> getNodes() {
        return nodes;
    }

    // Get all edges in the graph
    public Set<Edge> getAllEdges() {
        return edges.values().stream().flatMap(List::stream).collect(Collectors.toSet());
    }

    // Prim's algorithm for MST considering cost and latency (bandwidth)
    public NetworkResult primMST() {
        if (nodes.isEmpty()) return new NetworkResult(0, 0);  // If graph is empty, return zero cost and latency
        
        // Priority Queue to sort edges based on cost and bandwidth
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getCost));
        Set<String> visited = new HashSet<>();
        String start = nodes.keySet().iterator().next();
        visited.add(start);
        pq.addAll(edges.get(start));  // Add all edges of the start node

        int totalCost = 0;  // Stores the total cost of MST
        int totalLatency = 0;  // Stores the total latency (inverse of bandwidth)


        // Apply Prim's algorithm for MST
        while (!pq.isEmpty()) {
            Edge edge = pq.poll();  // Get the edge with minimum cost
            if (visited.contains(edge.to)) continue;  // If node is already visited, skip it
            visited.add(edge.to);   // Mark the node as visited
            totalCost += edge.cost;   // Add edge cost to total cost
            totalLatency += (100 / edge.bandwidth); // Calculate latency as inverse of bandwidth
            pq.addAll(edges.get(edge.to));  // Add new edges from the visited node to the queue
        }
        return new NetworkResult(totalCost, totalLatency);   // Return the total MST cost and latency
    }

    // Dijkstra's algorithm for the shortest path considering cost and latency
    public NetworkResult dijkstra(String start, String end) {
        if (!edges.containsKey(start) || !edges.containsKey(end)) return new NetworkResult(-1, -1);
        
        // Priority Queue to sort edges based on cost
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getCost));
        Map<String, Integer> distance = new HashMap<>();
        Map<String, Integer> latency = new HashMap<>();

        // Initialize all nodes with maximum values
        for (String node : edges.keySet()) {
            distance.put(node, Integer.MAX_VALUE);
            latency.put(node, Integer.MAX_VALUE);
        }

        distance.put(start, 0); // Distance to start node is 0
        latency.put(start, 0);  
        pq.add(new Edge(start, start, 0, 0));  // Start the queue from the start node


        // Apply Dijkstra's algorithm for shortest path
        while (!pq.isEmpty()) {
            Edge edge = pq.poll();  // Get the minimum cost edge
            if (edge.to.equals(end)) {
                return new NetworkResult(distance.get(end), latency.get(end));  // Return the shortest path cost and latency
            }

            for (Edge neighbor : edges.get(edge.to)) {
                int newDist = distance.get(edge.to) + neighbor.cost;  // Calculate new distance
                int newLatency = latency.get(edge.to) + (100 / neighbor.bandwidth); // Calculate new latency

                // If a shorter path is found, update it
                if (newDist < distance.get(neighbor.to) || newLatency < latency.get(neighbor.to)) {
                    distance.put(neighbor.to, newDist);
                    latency.put(neighbor.to, newLatency);
                    pq.add(new Edge(neighbor.to, neighbor.to, newDist, neighbor.bandwidth));  // Add to priority queue
                }
            }
        }
        return new NetworkResult(-1, -1);  // If no path is found, return -1
    }
}

// Edge class representing a connection between two nodes
class Edge {
    String from, to;
    int cost, bandwidth;

    Edge(String from, String to, int cost, int bandwidth) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.bandwidth = bandwidth;
    }

    public int getCost() {
        return cost;
    }

    public int getBandwidth() {
        return bandwidth;
    }
}

// Class to store the results of network optimizations (cost and latency)
class NetworkResult {
    int totalCost;
    int totalLatency;

    NetworkResult(int totalCost, int totalLatency) {
        this.totalCost = totalCost;
        this.totalLatency = totalLatency;
    }

    @Override
    public String toString() {
        return "Total Cost: " + totalCost + ", Total Latency: " + totalLatency;
    }
}

// GUI class for network topology visualization and interaction
class NetworkTopologyGUI extends JFrame {
    private Graph graph = new Graph();  // Graph instance
    private JTextArea outputArea = new JTextArea(10, 40);   // Output text area
    private DrawPanel drawPanel = new DrawPanel();  // Panel for drawing network

    private String selectedNode = null;

    NetworkTopologyGUI() {
        setTitle("Network Topology Optimizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton addNodeBtn = new JButton("Add Node");
        JButton addEdgeBtn = new JButton("Add Edge");
        JButton findMSTBtn = new JButton("Find MST");
        JButton shortestPathBtn = new JButton("Find Shortest Path");

        buttonPanel.add(addNodeBtn);
        buttonPanel.add(addEdgeBtn);
        buttonPanel.add(findMSTBtn);
        buttonPanel.add(shortestPathBtn);

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);
        add(drawPanel, BorderLayout.CENTER);

        // Button for adding nodes
        addNodeBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter node name:");
            int x = Integer.parseInt(JOptionPane.showInputDialog("Enter X position:"));
            int y = Integer.parseInt(JOptionPane.showInputDialog("Enter Y position:"));

            graph.addNode(name, x, y);
            drawPanel.repaint();
        });

        // Button for adding edges
        addEdgeBtn.addActionListener(e -> {
            String from = JOptionPane.showInputDialog("Enter starting node:");
            String to = JOptionPane.showInputDialog("Enter destination node:");
            int cost = Integer.parseInt(JOptionPane.showInputDialog("Enter cost:"));
            int bandwidth = Integer.parseInt(JOptionPane.showInputDialog("Enter bandwidth:"));

            graph.addEdge(from, to, cost, bandwidth);
            drawPanel.repaint();
            outputArea.append("Added edge: " + from + " - " + to + " (Cost: " + cost + ", Bandwidth: " + bandwidth + ")\n");
        });

        // Button for finding MST
        findMSTBtn.addActionListener(e -> {
            NetworkResult result = graph.primMST();
            outputArea.append("Minimum Spanning Tree (MST) - " + result.toString() + "\n");
        });

        // Button for finding shortest path
        shortestPathBtn.addActionListener(e -> {
            String start = JOptionPane.showInputDialog("Enter start node:");
            String end = JOptionPane.showInputDialog("Enter destination node:");
            NetworkResult result = graph.dijkstra(start, end);
            outputArea.append("Shortest Path from " + start + " to " + end + " - " + result.toString() + "\n");
        });

        // Mouse listener to select a node for interaction
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Map.Entry<String, Point> entry : graph.getNodes().entrySet()) {
                    if (entry.getValue().distance(e.getPoint()) < 20) {
                        selectedNode = entry.getKey();
                        return;
                    }
                }
                selectedNode = null;
            }
        });

        setVisible(true);
    }

    // Panel to draw the network topology
    class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Map<String, Point> nodes = graph.getNodes();
            Set<Edge> edges = graph.getAllEdges();

            // Draw edges
            g.setColor(Color.BLACK);
            for (Edge edge : edges) {
                Point p1 = nodes.get(edge.from);
                Point p2 = nodes.get(edge.to);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                g.drawString("C:" + edge.cost + " B:" + edge.bandwidth, (p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
            }

            // Draw nodes
            g.setColor(Color.BLUE);
            for (Map.Entry<String, Point> entry : nodes.entrySet()) {
                g.fillOval(entry.getValue().x - 10, entry.getValue().y - 10, 20, 20);
                g.setColor(Color.WHITE);
                g.drawString(entry.getKey(), entry.getValue().x - 5, entry.getValue().y + 5);
                g.setColor(Color.BLUE);
            }
        }
    }

    // Main function to run the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkTopologyGUI::new);
    }
}

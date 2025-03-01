import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// Graph class to represent the network
class Graph {
    private Map<String, Point> nodes = new HashMap<>();
    private Map<String, List<Edge>> edges = new HashMap<>();

    // Add a node with name and position
    public void addNode(String name, int x, int y) {
        nodes.put(name, new Point(x, y));
        edges.putIfAbsent(name, new ArrayList<>());
    }

    // Add an edge between two nodes with cost and bandwidth
    public void addEdge(String from, String to, int cost, int bandwidth) {
        edges.get(from).add(new Edge(from, to, cost, bandwidth));
        edges.get(to).add(new Edge(to, from, cost, bandwidth));
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
        if (nodes.isEmpty()) return new NetworkResult(0, 0);
        
        // Priority Queue to sort edges based on cost and bandwidth
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getCost));
        Set<String> visited = new HashSet<>();
        String start = nodes.keySet().iterator().next();
        visited.add(start);
        pq.addAll(edges.get(start));

        int totalCost = 0;
        int totalLatency = 0;

        // Apply Prim's algorithm for MST
        while (!pq.isEmpty()) {
            Edge edge = pq.poll();
            if (visited.contains(edge.to)) continue;
            visited.add(edge.to);
            totalCost += edge.cost;
            totalLatency += (100 / edge.bandwidth); // Calculate latency as inverse of bandwidth
            pq.addAll(edges.get(edge.to));
        }
        return new NetworkResult(totalCost, totalLatency);
    }

    // Dijkstra's algorithm for the shortest path considering cost and latency
    public NetworkResult dijkstra(String start, String end) {
        if (!edges.containsKey(start) || !edges.containsKey(end)) return new NetworkResult(-1, -1);
        
        // Priority Queue to sort edges based on cost
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getCost));
        Map<String, Integer> distance = new HashMap<>();
        Map<String, Integer> latency = new HashMap<>();
        for (String node : edges.keySet()) {
            distance.put(node, Integer.MAX_VALUE);
            latency.put(node, Integer.MAX_VALUE);
        }

        distance.put(start, 0);
        latency.put(start, 0);
        pq.add(new Edge(start, start, 0, 0));

        // Apply Dijkstra's algorithm for shortest path
        while (!pq.isEmpty()) {
            Edge edge = pq.poll();
            if (edge.to.equals(end)) {
                return new NetworkResult(distance.get(end), latency.get(end));
            }

            for (Edge neighbor : edges.get(edge.to)) {
                int newDist = distance.get(edge.to) + neighbor.cost;
                int newLatency = latency.get(edge.to) + (100 / neighbor.bandwidth); // Latency based on bandwidth

                if (newDist < distance.get(neighbor.to) || newLatency < latency.get(neighbor.to)) {
                    distance.put(neighbor.to, newDist);
                    latency.put(neighbor.to, newLatency);
                    pq.add(new Edge(neighbor.to, neighbor.to, newDist, neighbor.bandwidth));
                }
            }
        }
        return new NetworkResult(-1, -1);
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
    private Graph graph = new Graph();
    private JTextArea outputArea = new JTextArea(10, 40);
    private DrawPanel drawPanel = new DrawPanel();

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

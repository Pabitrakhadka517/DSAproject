import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;


/**
 * Network Topology Optimizer
 * Allows users to create network graphs, find MST (cost minimization), and calculate shortest paths.
 */
class Graph {
    // Map to store nodes and their edges
    private Map<String, List<Edge>> edges = new HashMap<>();

    // Method to add an edge to the graph
    public void addEdge(String from, String to, int cost, int bandwidth) {
        edges.putIfAbsent(from, new ArrayList<>());
        edges.putIfAbsent(to, new ArrayList<>());

        Edge edge = new Edge(from, to, cost, bandwidth);
        edges.get(from).add(edge);
        edges.get(to).add(new Edge(to, from, cost, bandwidth)); // Undirected graph
    }

    // Prim's Algorithm to find the Minimum Spanning Tree (MST)
    public int primMST() {
        if (edges.isEmpty()) return 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.cost));
        Set<String> visited = new HashSet<>();
        String start = edges.keySet().iterator().next();
        visited.add(start);
        pq.addAll(edges.get(start));

        int totalCost = 0;

        while (!pq.isEmpty()) {
            Edge edge = pq.poll();
            if (visited.contains(edge.to)) continue;

            visited.add(edge.to);
            totalCost += edge.cost;
            pq.addAll(edges.get(edge.to));
        }
        return totalCost;
    }

    // Dijkstra's Algorithm to find the shortest path between two nodes
    public int dijkstra(String start, String end) {
        if (!edges.containsKey(start) || !edges.containsKey(end)) return -1;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.cost));
        Map<String, Integer> distance = new HashMap<>();
        for (String node : edges.keySet()) distance.put(node, Integer.MAX_VALUE);
        distance.put(start, 0);
        pq.add(new Edge(start, start, 0, 0));

        while (!pq.isEmpty()) {
            Edge edge = pq.poll();
            if (edge.to.equals(end)) return distance.get(end);

            for (Edge neighbor : edges.get(edge.to)) {
                int newDist = distance.get(edge.to) + neighbor.cost;
                if (newDist < distance.get(neighbor.to)) {
                    distance.put(neighbor.to, newDist);
                    pq.add(new Edge(neighbor.to, neighbor.to, newDist, neighbor.bandwidth));
                }
            }
        }
        return -1; // No path found
    }
}

// Edge class representing network connections
class Edge {
    String from, to;
    int cost, bandwidth;

    Edge(String from, String to, int cost, int bandwidth) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.bandwidth = bandwidth;
    }
}

// GUI for Network Topology Optimization
class NetworkTopologyGUI extends JFrame {
    private Graph graph = new Graph();
    private JTextArea outputArea = new JTextArea(10, 30);

    NetworkTopologyGUI() {
        setTitle("Network Topology Optimizer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton addEdgeBtn = new JButton("Add Edge");
        JButton findMSTBtn = new JButton("Find MST");
        JButton shortestPathBtn = new JButton("Find Shortest Path");

        buttonPanel.add(addEdgeBtn);
        buttonPanel.add(findMSTBtn);
        buttonPanel.add(shortestPathBtn);

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Event listener for adding an edge
        addEdgeBtn.addActionListener(e -> {
            String from = JOptionPane.showInputDialog("Enter starting node:");
            String to = JOptionPane.showInputDialog("Enter destination node:");
            int cost = Integer.parseInt(JOptionPane.showInputDialog("Enter cost:"));
            int bandwidth = Integer.parseInt(JOptionPane.showInputDialog("Enter bandwidth:"));

            graph.addEdge(from, to, cost, bandwidth);
            outputArea.append("Added edge: " + from + " - " + to + " (Cost: " + cost + ", Bandwidth: " + bandwidth + ")\n");
        });

        // Event listener for finding MST
        findMSTBtn.addActionListener(e -> {
            int totalCost = graph.primMST();
            outputArea.append("Minimum Network Cost (MST): " + totalCost + "\n");
        });

        // Event listener for finding shortest path
        shortestPathBtn.addActionListener(e -> {
            String start = JOptionPane.showInputDialog("Enter start node:");
            String end = JOptionPane.showInputDialog("Enter destination node:");

            int shortestDistance = graph.dijkstra(start, end);
            outputArea.append("Shortest Path from " + start + " to " + end + " is: " + shortestDistance + "\n");
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkTopologyGUI::new);
    }
}

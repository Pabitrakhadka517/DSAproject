import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class Graph {
    private Map<String, Point> nodes = new HashMap<>();
    private Map<String, List<Edge>> edges = new HashMap<>();

    public void addNode(String name, int x, int y) {
        nodes.put(name, new Point(x, y));
        edges.putIfAbsent(name, new ArrayList<>());
    }

    public void addEdge(String from, String to, int cost, int bandwidth) {
        edges.get(from).add(new Edge(from, to, cost, bandwidth));
        edges.get(to).add(new Edge(to, from, cost, bandwidth));
    }

    public List<Edge> getEdges(String node) {
        return edges.getOrDefault(node, new ArrayList<>());
    }

    public Map<String, Point> getNodes() {
        return nodes;
    }

    public Set<Edge> getAllEdges() {
        return edges.values().stream().flatMap(List::stream).collect(Collectors.toSet());
    }

    public int primMST() {
        if (nodes.isEmpty()) return 0;
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.cost));
        Set<String> visited = new HashSet<>();
        String start = nodes.keySet().iterator().next();
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
        return -1;
    }
}

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

        addNodeBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter node name:");
            int x = Integer.parseInt(JOptionPane.showInputDialog("Enter X position:"));
            int y = Integer.parseInt(JOptionPane.showInputDialog("Enter Y position:"));

            graph.addNode(name, x, y);
            drawPanel.repaint();
        });

        addEdgeBtn.addActionListener(e -> {
            String from = JOptionPane.showInputDialog("Enter starting node:");
            String to = JOptionPane.showInputDialog("Enter destination node:");
            int cost = Integer.parseInt(JOptionPane.showInputDialog("Enter cost:"));
            int bandwidth = Integer.parseInt(JOptionPane.showInputDialog("Enter bandwidth:"));

            graph.addEdge(from, to, cost, bandwidth);
            drawPanel.repaint();
            outputArea.append("Added edge: " + from + " - " + to + " (Cost: " + cost + ", Bandwidth: " + bandwidth + ")\n");
        });

        findMSTBtn.addActionListener(e -> {
            int totalCost = graph.primMST();
            outputArea.append("Minimum Network Cost (MST): " + totalCost + "\n");
        });

        shortestPathBtn.addActionListener(e -> {
            String start = JOptionPane.showInputDialog("Enter start node:");
            String end = JOptionPane.showInputDialog("Enter destination node:");
            int shortestDistance = graph.dijkstra(start, end);
            outputArea.append("Shortest Path from " + start + " to " + end + " is: " + shortestDistance + "\n");
        });

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

    class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Map<String, Point> nodes = graph.getNodes();
            Set<Edge> edges = graph.getAllEdges();

            g.setColor(Color.BLACK);
            for (Edge edge : edges) {
                Point p1 = nodes.get(edge.from);
                Point p2 = nodes.get(edge.to);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                g.drawString("C:" + edge.cost + " B:" + edge.bandwidth, (p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
            }

            g.setColor(Color.BLUE);
            for (Map.Entry<String, Point> entry : nodes.entrySet()) {
                g.fillOval(entry.getValue().x - 10, entry.getValue().y - 10, 20, 20);
                g.setColor(Color.WHITE);
                g.drawString(entry.getKey(), entry.getValue().x - 5, entry.getValue().y + 5);
                g.setColor(Color.BLUE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkTopologyGUI::new);
    }
}

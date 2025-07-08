package dev.feixiong.shortestpath;

import java.util.*;

public class AdjMatrix {
    public static class Edge {
        String srcName;
        String dstName;
        double weight;
        int src;
        int dst;
        Edge(String srcName, String dstName, double weight) {
            this.srcName = srcName;
            this.dstName = dstName;
            this.weight = weight;
        }
    }
    public static class Node implements Comparable<Node> {
        int index;
        double distance;
        String name;
        Node prev;
        Node(int index, String name, double distance) {
            this.index = index;
            this.name = name;
            this.distance = distance;
            this.prev = null;
        }
        Node(int index, String name, double distance, Node prev) {
            this.index = index;
            this.name = name;
            this.distance = distance;
            this.prev = prev;
        }
        public int compareTo(Node oth) {
            if (distance < oth.distance)
                return -1;
            else if (distance == oth.distance)
                return 0;
            else
                return 1;
        }
        boolean equals(Node oth) {
            return name.equals(oth.name);
        }
        public int hashCode() {
            return name.hashCode();
        }
        public String toString() {
            return "[" + index + "," + name + "," + distance + "]";
        }
    }

    int size;
    double[][] dist;
    double[] shortest;
    List<Edge> edges = new ArrayList<>();
    List<String> names;
    Map<String, Integer> nameIndex;
    Map<Integer, String> indexName;
    int index(String name) {
        return nameIndex.get(name);
    }

    void init() {
        names = new ArrayList<String>();
        nameIndex = new HashMap<>();
        indexName = new HashMap<>();
    }

    void addEdge(String srcName, String dstName, double weight) {
        edges.add(new Edge(srcName, dstName, weight));
    }

    void seekShortestPath(String srcName, String dstName) {
        // go through the edges and generate an index for each name based on the order the name is seen
        init();

        int index = 0;

        for (Edge e: edges) {
            if (nameIndex.get(e.srcName) == null) {
                nameIndex.put(e.srcName, index);
                indexName.put(index, e.srcName);
                ++ index;
            }
            e.src = nameIndex.get(e.srcName);
            if (nameIndex.get(e.dstName) == null) {
                nameIndex.put(e.dstName, index);
                indexName.put(index, e.dstName);
                ++ index;
            }
            e.dst = nameIndex.get(e.dstName);
        }

        dist = new double[nameIndex.size()][nameIndex.size()];
        for (int i=0; i<index; i++) {
            for (int j=0; j<index; j++) {
                dist[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        for (Edge e: edges) {
            dist[e.src][e.dst] = e.weight;
        }

        PriorityQueue<Node> candidate = new PriorityQueue<>();

        int root = nameIndex.get(srcName);
        int target = nameIndex.get(dstName);
        candidate.add(new Node(root, srcName, 0, null));

        HashMap<String, Double> candidateHM = new HashMap<>();   // introduced for fast searching
        candidateHM.put(srcName, (double)0);
        LinkedHashSet<String> seq = new LinkedHashSet<>();
        while (!candidate.isEmpty()) {
            Node curr = candidate.poll();
            seq.add(curr.name);
            System.out.println("=>(" + curr.index + "," + curr.name + "," + curr.distance + ")");
            if (dstName.equals(curr.name)) {
                System.out.println(seq.toString());
                while (curr != null) {
                    System.out.println("[" + curr.name + "," + curr.distance + "]<=");
                    curr = curr.prev;
                }
                break;
            }
            for (int i=0; i<dist.length; i++) {
                double d = dist[curr.index][i];
                if  (d != Double.POSITIVE_INFINITY) {
                    String name = indexName.get(i);
                    if (!seq.contains(name)) {
                        Double canDist = candidateHM.get(name);
                        if (canDist != null) {
                            if (canDist > (curr.distance + d)) {
                                candidate.remove(new Node(i, name, canDist));
                                candidate.add(new Node(i, name, curr.distance + d, curr));
                                candidateHM.put(name, curr.distance + d);
                            }
                        } else {
                            candidate.add(new Node(i, name, curr.distance + d, curr));
                            candidateHM.put(name, curr.distance + d);
                        }
                    } else {
                        // already in output, skip
                    }
                }
            }
        }
        return;
    }

    public static void main(String[] args) {
        AdjMatrix adjMatrix = setupAdjMatrix();
        adjMatrix.seekShortestPath("NYC", "BNA");
    }

    public static AdjMatrix setupAdjMatrix() {
        AdjMatrix adjMatrix = new AdjMatrix();
        adjMatrix.addEdge("NYC", "BOS", 215);
        adjMatrix.addEdge("NYC", "PHI", 95);
        adjMatrix.addEdge("PHI", "DC", 135);
        adjMatrix.addEdge("DC", "CLT", 300);
        adjMatrix.addEdge("CLT", "ATL", 245);
        adjMatrix.addEdge("ATL", "BNA", 250);
        adjMatrix.addEdge("BNA", "CHI", 470);
        adjMatrix.addEdge("NYC", "DC", 190);
        adjMatrix.addEdge("BOS", "CHI", 980);
        adjMatrix.addEdge("PHI", "BNA", 700);
        adjMatrix.addEdge("CLT", "BNA", 300);
        adjMatrix.addEdge("ATL", "CHI", 715);
        return adjMatrix;
    }
}

package labs.lab1;

public class Link {

    private final Node node1;
    private final Node node2;
    private final String label;

    public Link (Node node1, Node node2, String label) {
        this.node1 = node1;
        this.node2 = node2;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }
}


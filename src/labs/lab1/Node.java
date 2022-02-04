package labs.lab1;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String symbolName; // for example "S"
    private String vertexLabel; // for example "q0 (S)"
    private String edgeLabel;
    private final List<Node> adjacentNodes = new ArrayList<>();

    public Node () {
    }

    public Node (String name) {
        this.symbolName = name;
    }

    public Node (String name, String vertexLabel) {
        this.symbolName = name;
        this.vertexLabel = vertexLabel;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public String getVertexLabel() {
        return vertexLabel;
    }

    public List<Node> getAdjacentNodes() {
        return adjacentNodes;
    }

    public String getEdgeLabel() {
        return edgeLabel;
    }

    public void setSymbolName(String name) {
        this.symbolName = name;
    }

    public void setSymbolName(Character name) {
        this.symbolName = String.valueOf(name);
    }

    public void setEdgeLabel(String edgeLabel) {
        this.edgeLabel = edgeLabel;
    }

    public void setVertexLabel(int stateIndex) {
        this.vertexLabel = "q" + stateIndex + " (" + symbolName + ")";
    }


}

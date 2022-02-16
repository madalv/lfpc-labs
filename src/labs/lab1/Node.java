package labs.lab1;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String symbolName; // for example "S"
    private String vertexLabel; // for example "q0 (S)"
    private final List<Node> adjacentNodes = new ArrayList<>();

    public Node () {
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

    public void setSymbolName(String name) {
        this.symbolName = name;
    }

    public void setSymbolName(Character name) {
        this.symbolName = String.valueOf(name);
    }

    public void setVertexLabel(int stateIndex) {
        this.vertexLabel = "q" + stateIndex + " (" + symbolName + ")";
    }
}

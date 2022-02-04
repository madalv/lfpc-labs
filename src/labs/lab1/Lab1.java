package labs.lab1;

import com.brunomnsilva.smartgraph.graph.*;
import com.brunomnsilva.smartgraph.graphview.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.*;
import java.util.*;
import java.util.stream.IntStream;

/* visualize FA using JavaFX and JavaFXSmartGraph (https://github.com/brunomnsilva/JavaFXSmartGraph)*/

public class Lab1 extends Application {

    private final List<Node> nodesToTrack = new ArrayList<>();
    private final List<Link> links = new ArrayList<>();
    private final Digraph<String, String> digraph = new DigraphEdgeList<>(); // visual graph

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage ignored) {
        processProductionsIntoGraph();
        setJavaFXStage();
        //check if word accepted by automata
        System.out.println(checkIfStringAcceptedByFA(getNodeBySymbolName("S"), 0, "bbabbbb"));
    }

    private void processProductionsIntoGraph() {
        String outputText = """
                On the first line, input the number of non-terminal symbols.
                Input productions, one on each line, in the form 'A->aA':
                Enter 'end' to stop.""";
        System.out.println(outputText);
        Scanner scanner = new Scanner(System.in);
        int nrOfNonTerminalSymbols = Integer.parseInt(scanner.nextLine());
        int nrOfSpaces = 0;

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            Node Vn1 = new Node(), Vn2 = new Node();
            StringBuilder Vt = new StringBuilder();

            if (line.equals("end")) break;

            extractSymbolsFromProduction(line, Vn1, Vn2, Vt);

            // add nodes to node list and to visual graph
            addNodeIfNeeded(Vn1, nrOfNonTerminalSymbols);
            addNodeIfNeeded(Vn2, nrOfNonTerminalSymbols);

            // add link to link list and to visual graph
            addLink(getNodeBySymbolName(Vn1.getSymbolName()), getNodeBySymbolName(Vn2.getSymbolName()), Vt, ++nrOfSpaces);
        }
    }

    private void extractSymbolsFromProduction(String line, Node Vn1, Node Vn2, StringBuilder Vt) {
        Character lastChar = line.charAt(line.length() - 1);

        Vn1.setSymbolName(line.substring(0, 1));

        if (Character.isUpperCase(lastChar)) {
            Vn2.setSymbolName(lastChar);
            Vt.append(line.charAt(line.length() - 2));
        } else {
            Vn2.setSymbolName("end");
            Vt.append(lastChar);
        }
    }

    private void addLink(Node n1, Node n2, StringBuilder Vt, int nrOfSpaces) {
        links.add(new Link(n1, n2, Vt.toString()));
        n1.getAdjacentNodes().add(n2);
        // JavaFXSmartGraph won't allow 2 edge labels to be equal, so I pad them out with spaces
        Vt.append(" ".repeat(nrOfSpaces));
        digraph.insertEdge(n1.getVertexLabel(), n2.getVertexLabel(), Vt.toString());
    }

    private boolean checkIfStringAcceptedByFA (Node n, int wordIndex, String word) {
        boolean result = false;

        for (Node w: n.getAdjacentNodes()) {
            List<Link> linkList = getLinks(n, w);
            for (Link link : linkList) {

                if (checkIfLastCharFitsTerminalSymbol(w, wordIndex, word, link)) return true;
                if (checkIfIndexInBoundsAndCharFitsCurrSymbol(wordIndex, word, link)) {
                    // analyze next production, else backtrack if you get back from recursion with a false result
                    result = checkIfStringAcceptedByFA(w, ++wordIndex, word);
                    --wordIndex;
                    if (result) return true;
                }
            }
        }
        return result;
    }

    private boolean checkIfIndexInBoundsAndCharFitsCurrSymbol(int wordIndex, String word, Link link) {
        String symbol = String.valueOf(word.charAt(wordIndex));
        return wordIndex + 1 < word.length() && link.getLabel().equals(symbol);
    }

    private boolean checkIfLastCharFitsTerminalSymbol(Node w, int wordIndex, String word, Link link) {
        boolean wIsEndState = w.getAdjacentNodes().isEmpty();
        String symbol = String.valueOf(word.charAt(wordIndex));
        if (wIsEndState) {
            return wordIndex == word.length() - 1 && symbol.equals(link.getLabel());
        }
        return false;
    }

    private List<Link> getLinks(Node n, Node w) {

        return links.stream().filter(li -> li.getNode1().equals(n) && li.getNode2().equals(w)).toList();
    }

    private void setJavaFXStage() {

        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(digraph, new SmartRandomPlacementStrategy());
        Scene scene = new Scene(graphView, 1024, 768);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Finite Automaton Visualization");
        stage.setScene(scene);
        stage.show();
        graphView.init();
    }

    private int getNodeIndexBySymbolName(String name) {
        OptionalInt nodeIndex = IntStream.range(0, nodesToTrack.size()).filter(n -> nodesToTrack.get(n).getSymbolName().equals(name)).findFirst();

        if (nodeIndex.isPresent()) {
            return nodeIndex.getAsInt();
        } else return -1;
    }

    private Node getNodeBySymbolName(String name) {
        Optional<Node> node = nodesToTrack.stream().filter(n -> n.getSymbolName().equals(name)).findFirst();

        return node.orElse(null);
    }

    private void setNodeLabel (Node v, int nrOfNonTerminals) {
        boolean nodeIsEndState = v.getSymbolName().equals("end");
        if(nodeIsEndState) v.setVertexLabel(nrOfNonTerminals);
        else v.setVertexLabel(getNodeIndexBySymbolName(v.getSymbolName()));
    }

    private void addNodeIfNeeded(Node v, int nrOfNonTerminals) {
        boolean nodeDoesNotExist = nodesToTrack.stream().noneMatch(s -> s.getSymbolName().equals(v.getSymbolName()));

        if (nodeDoesNotExist) {
            nodesToTrack.add(v);
            setNodeLabel(v, nrOfNonTerminals);
            digraph.insertVertex(v.getVertexLabel());
        }
        else setNodeLabel(v, nrOfNonTerminals);
    }
}

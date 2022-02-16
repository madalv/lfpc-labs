package labs.lab2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FiniteAutomaton {
    private final HashMap<String, HashMap<String, String>> rows;
    private final String[] alphabet;
    private final String endState;

    public HashMap<String, HashMap<String, String>> getTransitions() {
        return rows;
    }
    public String getEndState() { return endState; }

    // constructors
    public FiniteAutomaton (HashMap<String, HashMap<String, String>> t, String[] a, String es) {
        rows = t;
        alphabet = a;
        endState = es;
    }
    public FiniteAutomaton (String text) {
        String[] lines = text.split("[\\r\\n]+");
        String[] states = lines[2].trim().split(" ");
        alphabet = lines[0].trim().split(" ");
        endState = lines[1].trim();
        rows = new HashMap<>();

        for (String state : states)
            rows.put(state, (HashMap<String, String>) Stream.of(alphabet)
                    .collect(Collectors.toMap(a -> a, a -> "")));

        for (int i = 3; i < lines.length; i++) {
            String[] line = lines[i].trim().split(" ");
            String state = rows.get(line[0]).get(line[1]) + line[2];
            rows.get(line[0]).replace(line[1], state);
        }
    }

    // transform into dfa
    public FiniteAutomaton transformIntoDFA() {
        HashMap<String, HashMap<String, String>> dfaRows = new HashMap<>();
        dfaRows.put("0", rows.get("0"));
        printTransitions(rows);
        printTransitions(dfaRows);

        while(true) {
            HashMap<String, HashMap<String, String>> newRows = generateNewRows(dfaRows);
            if (newRows.isEmpty()) break;
            dfaRows.putAll(newRows);
            printTransitions(dfaRows);
        }
        return new FiniteAutomaton(dfaRows, alphabet, endState);
    }

    private HashMap<String, HashMap<String, String>> generateNewRows(HashMap<String, HashMap<String, String>> dfaRows) {
        HashMap<String, HashMap<String, String>> newRows = new HashMap<>();
        dfaRows.forEach((state, transitions) -> transitions.values().forEach((currState) -> {
            if (checkIfNewState(dfaRows, currState)) {
                newRows.put(currState, new HashMap<>());
                for (String transVar : alphabet)
                    newRows.get(currState).put(transVar, generateNewState(transVar, currState));
            }}));
        return newRows;
    }
    private String sortNewState(StringBuilder generatedString) {
        SortedSet<String> sortedGeneratedState = new TreeSet<>(Arrays.asList(generatedString.toString().split("")));
        return sortedGeneratedState.stream().reduce("", String::concat);
    }
    private boolean checkIfNewState(HashMap<String, HashMap<String, String>> rows, String currState) {
        return !rows.containsKey(currState) && !Objects.equals(currState, "");
    }
    private String generateNewState(String transVar, String currState) {
        StringBuilder generatedString = new StringBuilder();
        String[] nestedStates = currState.split("");

        for (String nState : nestedStates)
            generatedString.append(rows.get(nState).get(transVar));

        return sortNewState(generatedString);
    }
    private void printTransitions(HashMap<String, HashMap<String, String>> rows) {
        rows.forEach((state, transitions) -> {
            if (state.equals("0")) System.out.printf("\n%-5s", ">" + state);
            else if (state.contains(endState)) System.out.printf("\n%-5s", "*" + state);
            else System.out.printf("\n%-5s", state);
            transitions.forEach((transVar, transState) -> System.out.printf("| %s : %-5s  ", transVar, transState));
        });
        System.out.print("\n");
    }
}

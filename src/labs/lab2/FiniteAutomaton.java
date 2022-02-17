package labs.lab2;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

// work on abstraction

public class FiniteAutomaton {
    private final LinkedHashMap<String, LinkedHashMap<String, String>> rows;
    private final String[] alphabet;
    private final String endState;

    // constructors
    public FiniteAutomaton (LinkedHashMap<String, LinkedHashMap<String, String>> t, String[] a, String es) {
        rows = t;
        alphabet = a;
        endState = es;
    }

    public FiniteAutomaton (String text) {
        String[] lines = text.split("[\\r\\n]+");
        String[] states = lines[2].trim().split(" ");
        alphabet = lines[0].trim().split(" ");
        endState = lines[1].trim();
        rows = new LinkedHashMap<>();

        for (String state : states)
            rows.put(state, Arrays.stream(alphabet)
                    .collect(Collectors.toMap(a -> a, a-> "", (a1, a2) -> a1, LinkedHashMap::new)));

        for (int i = 3; i < lines.length; i++) {
            String[] line = lines[i].trim().split(" ");
            String state = rows.get(line[0]).get(line[1]) + line[2];
            rows.get(line[0]).replace(line[1], state);
        }
    }

    private boolean checkIfNFA() {
        AtomicBoolean result = new AtomicBoolean(false);
        rows.forEach((state, transitions) -> transitions.values().forEach((currState) -> {
            if (checkIfNewState(rows, currState)) result.set(true);
        }));
        return result.get();
    }

    // transform into dfa
    public FiniteAutomaton convertToDFA() {
        if (!checkIfNFA()) {
            System.out.println("Current FA is already deterministic.");
            return this;
        }
        LinkedHashMap<String, LinkedHashMap<String, String>> dfaRows = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedHashMap<String, String>> previousNewRows = new LinkedHashMap<>();
        dfaRows.put("0", rows.get("0"));
        previousNewRows.put("0", rows.get("0"));
        printTransitions(rows);
        printTransitions(dfaRows);

        while(true) {
            LinkedHashMap<String, LinkedHashMap<String, String>> generatedNewRows = generateNewRows(previousNewRows, dfaRows);
            if (generatedNewRows.isEmpty()) break;
            dfaRows.putAll(generatedNewRows);
            previousNewRows = generatedNewRows;
            printTransitions(dfaRows);
        }
        return new FiniteAutomaton(dfaRows, alphabet, endState);
    }

    private LinkedHashMap<String, LinkedHashMap<String, String>> generateNewRows(LinkedHashMap<String, LinkedHashMap<String, String>> tempRows, LinkedHashMap<String, LinkedHashMap<String, String>> dfaRows) {
        LinkedHashMap<String, LinkedHashMap<String, String>> newRows = new LinkedHashMap<>();
        tempRows.forEach((state, transitions) -> transitions.values().forEach((currState) -> {
            if (checkIfNewState(dfaRows, currState)) {
                newRows.put(currState, new LinkedHashMap<>());
                for (String transVar : alphabet)
                    newRows.get(currState).put(transVar, generateNewState(transVar, currState));
            }}));
        return newRows;
    }

    private String sortNewState(StringBuilder generatedString) {
        SortedSet<String> sortedGeneratedState = new TreeSet<>(Arrays.asList(generatedString.toString().split("")));
        return sortedGeneratedState.stream().reduce("", String::concat);
    }

    private boolean checkIfNewState(LinkedHashMap<String, LinkedHashMap<String, String>> rows, String currState) {
        return !rows.containsKey(currState) && !Objects.equals(currState, "");
    }

    private String generateNewState(String transVar, String currState) {
        StringBuilder generatedString = new StringBuilder();
        String[] nestedStates = currState.split("");

        for (String nState : nestedStates)
            generatedString.append(rows.get(nState).get(transVar));

        return sortNewState(generatedString);
    }

    private void printTransitions(LinkedHashMap<String, LinkedHashMap<String, String>> rows) {
        rows.forEach((state, transitions) -> {
            if (state.equals("0")) System.out.printf("\n%-5s", ">" + state);
            else if (state.contains(endState)) System.out.printf("\n%-5s", "*" + state);
            else System.out.printf("\n%-5s", state);
            transitions.forEach((transVar, transState) -> System.out.printf("| %s : %-5s  ", transVar, transState));
        });
        System.out.print("\n");
    }
}

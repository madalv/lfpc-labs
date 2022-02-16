package labs.lab2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FiniteAutomaton {
    private final HashMap<String, HashMap<String, String>> transitions;
    private final String[] alphabet;
    private final String endState;

    public HashMap<String, HashMap<String, String>> getTransitions() {
        return transitions;
    }

    // constructors
    public FiniteAutomaton (HashMap<String, HashMap<String, String>> t, String[] a, String es) {
        transitions = t;
        alphabet = a;
        endState = es;
    }
    public FiniteAutomaton (String text) {
        String[] lines = text.split("[\\r\\n]+");
        String[] states = lines[2].trim().split(" ");
        alphabet = lines[0].trim().split(" ");
        endState = lines[1].trim();
        transitions = new HashMap<>();

        for (String state : states)
            transitions.put(state, (HashMap<String, String>) Stream.of(alphabet)
                    .collect(Collectors.toMap(a -> a, a -> "")));

        for (int i = 3; i < lines.length; i++) {
            String[] line = lines[i].trim().split(" ");
            String state = transitions.get(line[0]).get(line[1]) + line[2];
            transitions.get(line[0]).replace(line[1], state);
        }
    }

    // transform into dfa
    public FiniteAutomaton transformIntoDFA() {
        HashMap<String, HashMap<String, String>> dfaTransitions = new HashMap<>();
        dfaTransitions.put("0", transitions.get("0"));
        printTransitions(dfaTransitions);

        while(true) {
            HashMap<String, HashMap<String, String>> newTransitions = new HashMap<>();

            dfaTransitions.forEach((key, map) -> map.values().forEach((currState) -> {
                if (checkIfNewState(dfaTransitions, currState)) {
                    newTransitions.put(currState, new HashMap<>());
                    for (String transVar : alphabet)
                        newTransitions.get(currState).put(transVar, generateNewState(transVar, currState));
                }}));

            if (newTransitions.isEmpty()) break;
            dfaTransitions.putAll(newTransitions);
            printTransitions(dfaTransitions);
        }
        return new FiniteAutomaton(dfaTransitions, alphabet, endState);
    }

    private String sortNewState(StringBuilder generatedString) {
        SortedSet<String> sortedGeneratedState = new TreeSet<>(Arrays.asList(generatedString.toString().split("")));
        return sortedGeneratedState.stream().reduce("", String::concat);
    }
    private boolean checkIfNewState(HashMap<String, HashMap<String, String>> t, String currState) {
        return !t.containsKey(currState) && !Objects.equals(currState, "");
    }
    private String generateNewState(String transVar, String currState) {
        StringBuilder generatedString = new StringBuilder();
        String[] nestedStates = currState.split("");

        for (String nState : nestedStates) {
            generatedString.append(transitions.get(nState).get(transVar));
        }
        return sortNewState(generatedString);
    }
    public void printTransitions(HashMap<String, HashMap<String, String>> transitions) {
        transitions.forEach((state, map) -> {
            if (state.equals("0")) System.out.printf("\n%-5s", ">" + state);
            else if (state.contains(endState)) System.out.printf("\n%-5s", "*" + state);
            else System.out.printf("\n%-5s", state);
            map.forEach((transVar, transState) -> System.out.printf("| %-5s : %-5s  ", transVar, transState));
        });
        System.out.print("\n");
    }
}

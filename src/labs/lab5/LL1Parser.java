package labs.lab5;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import labs.lab4.Grammar;

import java.util.*;

public class LL1Parser {
    private final HashMap<String, Set<String>> firstTable;
    private final HashMap<String, Set<String>> followTable;
    private final HashMap<String, Row> parseTable;
    private final Grammar grammar;
    private final String epsilon = "*";
    private final String endOfInput = "$";


    public LL1Parser(Grammar g) {
        firstTable = new HashMap<>();
        followTable = new HashMap<>();
        parseTable = new HashMap<>();

        grammar = g;
        //grammar.prepareForParsing();

        for (String N : grammar.Vn) {
            buildFirst(N);
            buildFollow(N);
            parseTable.put(N, new Row(grammar.Vt));
        }

        buildParseTable();

        System.out.println("GRAMMAR: "); grammar.printP();
        System.out.println("FIRST TABLE: "); printTable(firstTable);
        System.out.println("FOLLOW TABLE: ") ; printTable(followTable);
        System.out.println("PARSE TABLE: "); printParseTable();
    }

    private void buildParseTable() {
        grammar.P.forEach((N, RHSList) -> {
            for (String RHS : RHSList) {
                Map<String, String> row = parseTable.get(N).row;
                if (RHS.equals(epsilon))
                    for (String t : followTable.get(N)) row.replace(t, RHS);
                else
                    for (String t : buildFirstProduction(RHS)) {
                        if (!parseTable.get(N).row.get(t).equals(""))
                            System.out.println("!!!NOT LL(1) GRAMMAR!!! \n" +
                                    "For terminal " + t + " exist multiple transitions in the parse table: " +
                                    N + " -> " + parseTable.get(N).row.get(t) + " | " + RHS
                            );
                        row.replace(t, RHS);
                    }
            }
        });
    }


    private Set<String> buildFirst (String N) {
        if (firstTable.containsKey(N)) return firstTable.get(N); // if row for current N was created, exit

        firstTable.put(N, new HashSet<>()); // else initialize
        Set<String> productionsOfN = grammar.P.get(N);

        productionsOfN.forEach(p -> firstTable.get(N).addAll(buildFirstProduction(p)));
        return firstTable.get(N);
    }

    private Set<String> buildFirstProduction (String p) {
        Set<String> tempTerminals = new HashSet<>();
        if (p.equals(epsilon)) tempTerminals.add(epsilon); // if N derives into empty directly, add it
        else for (int i = 0; i < p.length(); i++) {
            String currSymbol = String.valueOf(p.charAt(i));
            if (grammar.Vt.contains(currSymbol)) {
                tempTerminals.add(currSymbol);
                break;
            }
            Set<String> firstOfPS = buildFirst(currSymbol);
            // if no epsilon in symbol's first table add set to N's first
            if (!firstOfPS.contains(epsilon)) {
                tempTerminals.addAll(firstOfPS);
                break;
            }
            else {
                // 1) add all to N's first -- except epsilon
                // 2) check next symbol
                // 3) if all symbols derive into epsilon, add epsilon to current N first
                tempTerminals.addAll(firstOfPS);
                if (i != p.length() - 1) tempTerminals.remove(epsilon);
            }
        }
        return tempTerminals;
    }

    private Set<String> buildFollow (String N) {
        if (followTable.containsKey(N)) return followTable.get(N);
        followTable.put(N, new HashSet<>()); // else initialize
        if (N.equals(grammar.S)) followTable.get(N).add(endOfInput); // add $ to S

        grammar.P.forEach((A, RHSList) -> {
            for (String RHS : RHSList) {
                int indexOfN = RHS.indexOf(N);
                if (indexOfN == -1) continue;
                int indexOfFollow = indexOfN + 1;

                while (true) {
                    if (indexOfFollow == RHS.length()) { // A -> aN
                        // include FOLLOW(A) in FOLLOW(N)
                        followTable.get(N).addAll(buildFollow(A));
                        break;
                    }

                    String follow = String.valueOf(RHS.charAt(indexOfFollow));
                    // A -> aNb
                    // include FIRST(b)\epsilon in FOLLOW(N)
                    // if FIRST(b) contains epsilon, then FOLLOW(N) includes FOLLOW(A)

                    if (grammar.Vt.contains(follow)) {
                        followTable.get(N).add(follow);
                        break;
                    }

                    Set<String> firstOfFollow = buildFirst(follow);
                    followTable.get(N).addAll(firstOfFollow);

                    if (!firstTable.get(follow).contains(epsilon)) break;
                    else {
                        followTable.get(N).remove(epsilon);
                        followTable.get(N).addAll(buildFollow(A));
                        indexOfFollow++;
                    }
                }
            }
        });

        return followTable.get(N);
    }

    public void parseWord(String word) {
        Stack<String> stack = new Stack<>();
        stack.push(endOfInput);
        stack.push(grammar.S);

        String input = word + endOfInput;
        String nextAction = grammar.S;

        System.out.println("PARSE WORD: " + word);
        System.out.printf("%-10s |%-10s |%-10s %n", "INPUT", "STACK", "NEXT");
            while (stack.size() > 0) {
                System.out.printf("%-10s |%-10s |%-10s %n", input, String.join("", stack), nextAction);

                String currentS = input.substring(0, 1);
                String topOfStack = stack.peek();

                if (currentS.equals(topOfStack)) {
                    if (currentS.equals(endOfInput)) {
                        System.out.println("WORD ACCEPTED: " + word);
                        break;
                    }
                    stack.pop();
                    input = input.substring(1);
                    nextAction = nextAction.substring(1);
                    continue;
                }

                String NT = parseTable.get(topOfStack).row.get(currentS);

                if (NT.equals("")) {
                    System.out.println("NO NEXT TRANSITION. WORD NOT ACCEPTED: " + word);
                    break;
                }

                NT = NT.replace("*", "");
                nextAction = NT + nextAction.substring(1);
                stack.pop();

                for (int i = NT.length() - 1; i >= 0; i--) {
                    stack.push(String.valueOf(NT.charAt(i)));
                }
            }
    }

    private void printTable(HashMap<String, Set<String>> h) {
        h.forEach((k, v) -> {
            System.out.println(k + ": " + String.join(", ", v));
        });
    }

    private void printParseTable() {
        parseTable.get(grammar.S).printHeader();
        parseTable.forEach((N, row) -> {
            System.out.println(N + " -> " + row);
        });
    }
}

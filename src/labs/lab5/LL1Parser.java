package labs.lab5;

import labs.lab4.Grammar;

import java.util.*;

public class LL1Parser {
    public HashMap<String, Set<String>> firstTable;
    public HashMap<String, Set<String>> followTable;
    public Grammar grammar;
    public final String epsilon = "*";
    public final String endOfInput = "$";

    // TODO: transition table
    // TODO: parsing
    // TODO: add prepareForParsing (remove init recursion, left factor) in Grammar

    public LL1Parser(Grammar g) {
        firstTable = new HashMap<>();
        followTable = new HashMap<>();
        grammar = g;
        for (String N : grammar.Vn) {
            buildFirst(N);
            buildFollow(N);
        }

        System.out.println("FIRST TABLE: " + firstTable);
        System.out.println("FOLLOW TABLE: " + followTable);
    }

    private Set<String> buildFirst (String N) {
        if (firstTable.containsKey(N)) return firstTable.get(N); // if row for current N was created, exit

        firstTable.put(N, new HashSet<>()); // else initialize
        Set<String> productionsOfN = grammar.P.get(N);

        productionsOfN.forEach(p -> {
            if (p.equals(epsilon)) firstTable.get(N).add(epsilon); // if N derives into empty directly, add
            else for (int i = 0; i < p.length(); i++) {
                String currSymbol = String.valueOf(p.charAt(i));
                if (grammar.Vt.contains(currSymbol)) {
                    firstTable.get(N).add(currSymbol);
                    break;
                }
                Set<String> firstOfPS = buildFirst(currSymbol);
                // if no epsilon in symbol's first table add set to N's first
                if (!firstOfPS.contains(epsilon)) {
                    firstTable.get(N).addAll(firstOfPS);
                    break;
                }
                else {
                    // 1) add all to N's first -- except epsilon
                    // 2) check next symbol
                    // 3) if all symbols derive into epsilon, add epsilon to current N first
                    firstTable.get(N).addAll(firstOfPS);
                    if (i != p.length() - 1) firstTable.get(N).remove(epsilon);
                }
            }
        });
        return firstTable.get(N);
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
                        //followTable.get(N).addAll(buildFollow(A));
                        break;
                    }

                    Set<String> firstOfFollow = buildFirst(follow);

                    followTable.get(N).addAll(firstOfFollow);
                    if (!firstTable.get(follow).contains(epsilon))
                        break;
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
}

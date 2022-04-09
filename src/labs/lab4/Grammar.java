package labs.lab4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class Grammar {
    public List<String> Vn;
    public List<String> Vt;
    public ConcurrentHashMap<String, Set<String>> P;
    public String S;

    Grammar(String filename) throws IOException {
        List<String> lines;
        Path p = Paths.get(filename);
        lines = Files.readAllLines(p);

        S = lines.get(0).trim();
        Vn = new ArrayList<>(Arrays.stream(lines.get(1).trim().split(" ")).toList());
        Vt = new ArrayList<>(Arrays.stream(lines.get(2).trim().split(" ")).toList());
        P = new ConcurrentHashMap<>();

        for (String N : Vn) P.put(N, new HashSet<>());

        for (int i = 3; i < lines.size(); i++) {
            String[] rule = lines.get(i).trim().split(" ");
            P.get(rule[0]).add(rule[1]);
        }
    }

    // CONVERT TO CHOMSKY NORMAL FORM
    public void convertToCNF() {
        printP();
        removeEmptyProductions();
        removeRenamings();
        removeNonProductives();
        removeInaccessibles();
        transformIntoChomsky();
    }
    // -------------------------- STEP 1 --------------------------
    //removal of empty productions
    private void removeEmptyProductions() {
        Set<String> nullables = getNullables();
        Map<String, Set<String>> copyP = deepCopyP();

        copyP.forEach((N, RHSList) -> {
            P.get(N).remove("*");
            P.get(N).remove("");
            for (String RHS : RHSList)
                for (String nullable : nullables)
                    if (RHS.contains(nullable)) addCombinations(nullable, N, RHS);
        });

        if (!getNullables().isEmpty()) removeEmptyProductions();
        else {
            System.out.println("STEP 1 (Remove Empty Productions):");
            printP();
        }
    }

    private Map<String, Set<String>> deepCopyP () {
        return P.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue()), (e1, e2) -> e1,LinkedHashMap::new));
    }

    private void addCombinations(String nullable, String N, String rhs) {
        List<String> combinations = (getCombinations(rhs, nullable.charAt(0), 0));
        P.get(N).addAll(combinations);
    }

    private Set<String> getNullables () { // A -> epsilon, A - nullable
        Set<String> nullables  = new HashSet<>();
        Map<String, Set<String>> copyP = deepCopyP();
        copyP.forEach((N, ListRHS) -> {
            for (String RHS : ListRHS)
                if (RHS.equals("*") || RHS.equals("")) nullables.add(N);
        });
        return nullables;
    }

    // gets combination for removal of empty productions
    // s = "AbAb" c = "A" -> [bb, Abb, bAb, AbAb]
    private List<String> getCombinations(String rhs, Character nullable, int lastIndex) {
        List<String> combinations = new ArrayList<>();
        int currIndex = rhs.indexOf(nullable, lastIndex);
        String prefix = rhs.substring(lastIndex, currIndex),
                postfix = rhs.substring(currIndex + 1);

        // if postfix has any nullables, call the function again & get results
        // if not, simply append it at the back of current combinations
        if (postfix.chars().filter(chr -> chr == nullable).count() > 0) {
            List<String> results = getCombinations(rhs, nullable, currIndex + 1);
            for (String result : results) // current combinations for "bA" -> "b", "bA"
                combinations.addAll(Arrays.asList(prefix + result, prefix + nullable + result));
        } else combinations.addAll(Arrays.asList(prefix + postfix, prefix + nullable + postfix));
        return combinations;
    }

    // -------------------------- STEP 2 --------------------------
    // removal of renamings
    private void removeRenamings() {
        AtomicBoolean notFinished = new AtomicBoolean(true);
        Map<String, Set<String>> copyP = deepCopyP();
        while (notFinished.get()){
            notFinished.set(false);
            copyP.forEach((N, RHSList) -> {
                for (String RHS : RHSList) {
                    if (Vn.contains(RHS)) {
                        notFinished.set(true);
                        P.get(N).remove(RHS);
                        P.get(N).addAll(P.get(RHS));
                    }
                }
            });

            copyP = deepCopyP();
        }
        System.out.println("STEP 2 (Remove Renamings):");
        printP();
    }

    // -------------------------- STEP 3 --------------------------
    // removal of non-productive symbols
    private void removeNonProductives() {
        Set<String> nonproductives = new HashSet<>(Vn);
        Set<String> productives = getProductives();
        nonproductives.removeAll(productives);

        Map<String, Set<String>> copyP = deepCopyP();
        copyP.forEach((N, RHSList) -> {
            for (String RHS : RHSList)
                for (char c : RHS.toCharArray()) {
                    if (nonproductives.contains(String.valueOf(c))) P.get(N).remove(RHS);
                }
            if (nonproductives.contains(N)) P.remove(N);
        });

        Vn.removeAll(nonproductives);
        System.out.println("STEP 3 (Remove Non-Productives): \n"
                + "Non-productives: "
                + nonproductives
                + " Productives: "
                + productives);
        printP();
    }

    private Set<String> getProductives() {
        Set<String> productives = new HashSet<>();
        P.forEach((N, RHSList) -> {
            for (String RHS : RHSList) {
                if (Vt.contains(RHS)) {
                    productives.add(N);
                    break;
                }
            }});
        productives.add(S);
        P.forEach((N, RHSList) -> {
            for (String RHS : RHSList) {
                boolean isProductive = true;
                for (char c : RHS.toCharArray()) {
                    isProductive = productives.contains(String.valueOf(c)) || Vt.contains(String.valueOf(c));
                }
                if (isProductive) productives.add(N);
            }
        });
        return productives;
    }

    // -------------------------- STEP 4 --------------------------
    // removal of inaccessible symbols
    private void removeInaccessibles() {
        List<String> inaccessibles = getInaccessibles();
        for (String i : inaccessibles) P.remove(i);

        Vn.removeAll(inaccessibles);
        System.out.println("STEP 4 (Remove Inaccessibles): \n"
                + "Inaccessibles: " + inaccessibles);
        printP();
    }

    private List<String> getInaccessibles() {
        List<String> inaccessibles = new ArrayList<>(Vn);
        inaccessibles.remove(S);
        P.forEach((N, RHSList) -> {
            for (String RHS : RHSList)
                for (char c : RHS.toCharArray()) {
                    if (!String.valueOf(c).equals(N) && Vn.contains(String.valueOf(c)))
                        inaccessibles.remove(String.valueOf(c));
                }});
        return inaccessibles;
    }

    // -------------------------- STEP 5 --------------------------
    // the chomsky normal form
    private final HashMap<String, String> X = new HashMap<>();
    private final HashMap<String, String> Y = new HashMap<>();
    private void transformIntoChomsky() {
        Map<String, Set<String>> copyP = deepCopyP();
        copyP.forEach((N, RHSList) -> {
            for (String RHS : RHSList) {
                StringBuilder repl = new StringBuilder();
                String firstSymbol = String.valueOf(RHS.charAt(0));
                if (RHS.length() == 1) continue;
                repl.append(mapSingle(firstSymbol));
                repl.append(convertProduction(RHS.substring(1)));
                P.get(N).remove(RHS);
                P.get(N).add(repl.toString());
            }
        });

        addReplacements(X);
        addReplacements(Y);

        System.out.println("STEP 5 (FINAL CHOMSKY FORM):");
        printP();
    }

    private void addReplacements(HashMap<String, String> h) {
        h.forEach((ogValue, replacement) -> {
            P.put(replacement, new HashSet<>());
            P.get(replacement).add(ogValue);
            Vn.add(replacement);
        });
    }

    // reduces to 1 Y symbol
    private String convertProduction(String p) {
        if (Vt.contains(p)) {
            return mapSingle(p);
        } else if (p.length() == 1) return p;

        return mapToY(mapSingle(String.valueOf(p.charAt(0))), convertProduction(p.substring(1)));
    }

    private String mapSingle(String t) {
        if (X.containsKey(t)) return X.get(t);
        if (Vn.contains(t)) return  t;
        String x = "X" + (X.size() + 1);
        X.put(t, x);
        return x;
    }

    private String mapToY(String N, String M) {
        String O = N + M;
        if (Y.containsKey(O)) return Y.get(O);
        String y = "Y" + (Y.size() + 1);
        Y.put(O, y);
        return y;
    }


    // -------------------------- GREIBACH FORM --------------------------
    public void convertToGNF() {
        AtomicInteger zCount = new AtomicInteger(1);
        AtomicBoolean notFinished = new AtomicBoolean(true);
        Set<String> recursionSymbols = new HashSet<>();
        convertToCNF();
        System.out.println(" ------------------- GREIBACH FORM STEPS: -------------------");
        removeInitialLeftRecursion(zCount);

        P.forEach((N, RHSList) -> {
            notFinished.set(true);
            while(notFinished.get()) {
                Set<String> RHSSet = Set.copyOf(RHSList);
                notFinished.set(false);
                for (String RHS : RHSSet) {
                    if (checkIfProductionGreibach(RHS)) continue;
                    notFinished.set(true);
                    String firstSymbol = getSymbol(RHS, 0);
                    System.out.println(N + " -> " + RHS + " - not in Greibach;");

                    if (firstSymbol.equals(N)) recursionSymbols.add(RHS); // add to set in case of multiple left recursions
                    else P.get(N).addAll(getIntoGreibachForm(N, RHS, zCount));

                    P.get(N).removeAll(recursionSymbols);
                    printP();
                }
                if (!recursionSymbols.isEmpty()) P.get(N).addAll(eliminateLeftRecursion(zCount, recursionSymbols, N));
                recursionSymbols.clear();
            }
        });
        System.out.println("FINAL GREIBACH FORM:");
        printP();
    }

    private void removeInitialLeftRecursion(AtomicInteger zCount) {
        Set<String> recursionSymbols = new HashSet<>();
        Set<String> toAdd = new HashSet<>();
        P.forEach((N, RHSList) -> {
            Set<String> RHSSet = Set.copyOf(RHSList);
            for (String RHS : RHSSet) {
                String firstSymbol = getSymbol(RHS, 0);
                if (firstSymbol.equals(N)) {
                    recursionSymbols.add(RHS);
                    System.out.println(N + " -> " + RHS + " - remove initial left recursion.");
                    P.get(N).remove(RHS);
                }
            }
            if (!recursionSymbols.isEmpty())toAdd.addAll(eliminateLeftRecursion(zCount, recursionSymbols, N));
            recursionSymbols.clear();
            P.get(N).addAll(toAdd);
            toAdd.clear();
        });
        printP();
    }

    // returns set of productions to be added (obtained after getting one RHS into Greibach)
    private Set<String> getIntoGreibachForm(String N, String RHS, AtomicInteger zCount) {
        String firstSymbol = getSymbol(RHS, 0);
        Set<String> toAdd = new HashSet<>();

        for (String prefix : P.get(firstSymbol)) {
            String newRHS = prefix + RHS.substring(firstSymbol.length());
            toAdd.add(newRHS);
        }

        P.get(N).remove(RHS);
        return toAdd;
    }

    private boolean checkIfProductionGreibach(String p) {
        boolean result = true;
        if (p.length() > 1) // Chomsky ensures no unit productions remain
            if (!Vt.contains(String.valueOf(p.charAt(0)))) result = false; // if first symbol is not terminal
            else for (int i = 1; i < p.length(); i++) {
                if (Character.isDigit(p.charAt(i))) continue; // ignore second part of composite symbols (X1, Z2, etc.)
                String C = getSymbol(p, i);
                if (!Vn.contains(C)) {
                    result = false;
                    break;
                }
            }
        return result;
    }

    // returns set of productions for A obtained after substituting Z back into A [check step 2 below]
    private Set<String> eliminateLeftRecursion(AtomicInteger count, Set<String> RHS, String N) {
        // A -> AX | Y
        // <eliminates left rec>
        // 1) Z -> XZ | X
        // 2) A -> YZ
        Set<String> toAdd = new HashSet<>();

        // implements (1)
        String Z = "Z" + count.getAndIncrement();
        P.put(Z, new HashSet<>());
        for (String s : RHS) {
            P.get(Z).add(s.substring(1) + Z);
            P.get(Z).add(s.substring(1));
        }
        Vn.add(Z);
        System.out.println(N + " -> " + RHS + " - left recursion;\n" + Z + " new variable.");

        // implements (2)
        for (String p : P.get(N)) if (!RHS.contains(p)) toAdd.add(p + Z);

        return toAdd;
    }

    private String getSymbol(String p, int i) {
        String C = (String.valueOf(p.charAt(i)));
        if (C.equals("Z") || C.equals("X") || C.equals("Y")
        ) C += String.valueOf(p.charAt(i + 1));
        return C;
    }

    public void printP() { P.forEach((N, RHSList) -> System.out.println(N + " -> " + String.join(" | ", RHSList)));
        System.out.println();
    }
}

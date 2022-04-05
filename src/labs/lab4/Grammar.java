package labs.lab4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    public List<String> Vn;
    public List<String> Vt;
    public LinkedHashMap<String, Set<String>> P;
    public String S;

    Grammar(String filename) throws IOException {
        List<String> lines;
        Path p = Paths.get(filename);
        lines = Files.readAllLines(p);

        S = lines.get(0).trim();
        Vn = new ArrayList<>(Arrays.stream(lines.get(1).trim().split(" ")).toList());
        Vt = new ArrayList<>(Arrays.stream(lines.get(2).trim().split(" ")).toList());
        P = new LinkedHashMap<>();

        for (String N : Vn) P.put(N, new HashSet<>());

        for (int i = 3; i < lines.size(); i++) {
            String[] rule = lines.get(i).trim().split(" ");
            P.get(rule[0]).add(rule[1]);
        }
    }

    // -------------------------- STEP 1 --------------------------
    //removal of empty productions
    public void removeEmptyProductions() {
        Set<String> nullables = getNullables();
        Map<String, Set<String>> copyP = deepCopyP();

        copyP.forEach((N, RHSList) -> {
            for (String RHS : RHSList)
                for (String nullable : nullables)
                    if (RHS.contains(nullable)) addCombinations(nullable, N, RHS);
        });

        if (!getNullables().isEmpty()) removeEmptyProductions();
        else System.out.println("STEP 1 (Remove Empty Productions):\n" + P);
    }

    private Map<String, Set<String>> deepCopyP () {
        return P.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue())));
    }

    private void addCombinations(String nullable, String N, String rhs) {
        List<String> combinations = (getCombinations(rhs, nullable.charAt(0), 0));
        P.get(N).addAll(combinations);
        P.get(N).remove(rhs);
    }

    private Set<String> getNullables () { // A -> epsilon, A - nullable
        Set<String> nullables  = new HashSet<>();
        P.forEach((N, ListRHS) -> {
            for (String RHS : ListRHS)
                if (RHS.equals("*")) nullables.add(N); ListRHS.remove("*");
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
    public void removeRenamings() {
        Map<String, Set<String>> copyP = deepCopyP();
        copyP.forEach((N, RHSList) -> {
            for (String RHS : RHSList) {
                if (Vn.contains(RHS)) {
                    P.get(N).remove(RHS);
                    P.get(N).addAll(P.get(RHS));
                }
            }
        });
        System.out.println("STEP 2 (Remove Renamings):\n"+ P);
    }

    // -------------------------- STEP 3 --------------------------
    // removal of non-productive symbols
    public void removeNonProductives() {
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
                + productives
                + "\n" + P);
    }

    private Set<String> getProductives() {
        Set<String> productives = new HashSet<>();
        P.forEach((N, RHSList) -> {
            for (String RHS : RHSList) {
                if (Vt.contains(RHS)) {
                    productives.add(N);
                    break;
                }
                boolean isProductive = true;
                for (char c : RHS.toCharArray()) {
                    isProductive = productives.contains(String.valueOf(c));
                }
                if (isProductive) productives.add(N);
            }
        });
        return productives;
    }

    // -------------------------- STEP 4 --------------------------
    // removal of inaccessible symbols
    public void removeInaccessibles() {
        List<String> inaccessibles = getInaccessibles();
        for (String i : inaccessibles) {
            P.remove(i);
        }
        Vn.removeAll(inaccessibles);
        System.out.println("STEP 4 (Remove Inaccessibles): \n"
                + "Inaccessibles: " + inaccessibles + "\n"
                + P);
    }

    public List<String> getInaccessibles() {
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
}

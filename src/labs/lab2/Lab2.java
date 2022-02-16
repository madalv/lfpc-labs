package labs.lab2;


public class Lab2 {

    public static void main(String[] args) {
        String dfaText = """
                a b c
                2
                0 1 2 3
                0 b 0
                0 a 1
                1 c 1
                1 a 2
                3 a 1
                3 a 3
                2 a 3
                """;
        FiniteAutomaton nfa = new FiniteAutomaton(dfaText);
        nfa.printTransitions(nfa.getTransitions());
        FiniteAutomaton dfa = nfa.transformIntoDFA();
    }
}

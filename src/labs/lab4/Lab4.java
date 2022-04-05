package labs.lab4;

import java.io.IOException;

public class Lab4 {
    public static void main(String[] args) throws IOException {
        Grammar g = new Grammar("C:\\Users\\Vlada\\IdeaProjects\\lfpc\\src\\labs\\lab4\\v13LFPC.txt");

        System.out.println("Vn: " + g.Vn);
        System.out.println("Vt: " + g.Vt);
        System.out.println("Init: " + g.P);
        g.removeEmptyProductions();
        g.removeRenamings();
        g.removeNonProductives();
        g.removeInaccessibles();
    }



}

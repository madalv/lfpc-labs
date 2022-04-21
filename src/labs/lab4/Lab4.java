package labs.lab4;

import java.io.IOException;

public class Lab4 {
    public static void main(String[] args) throws IOException {
        Grammar g = new Grammar("C:\\Users\\Vlada\\IdeaProjects\\lfpc\\src\\labs\\lab4\\v21_2.txt");
        g.convertToGNF();
    }
}

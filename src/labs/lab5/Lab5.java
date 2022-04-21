package labs.lab5;

import labs.lab4.Grammar;

import java.io.IOException;

public class Lab5 {
    public static void main(String[] args) throws IOException {
        Grammar g = new Grammar("C:\\Users\\Vlada\\IdeaProjects\\lfpc\\src\\labs\\lab5\\v12.txt");
        LL1Parser p = new LL1Parser(g);
    }
}

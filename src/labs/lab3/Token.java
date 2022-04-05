package labs.lab3;

public class Token {
    public TokenType type;
    public String literal;

    public Token(TokenType t, String l) {
        type = t;
        literal = l;
    }
}

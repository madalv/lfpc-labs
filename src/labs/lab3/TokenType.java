package labs.lab3;

public enum TokenType {
    // OPERATORS
    SEMICOLON(";"), R_BRACKET("]"), L_BRACKET("["), MOD("%"),
    R_BRACE("}"), L_BRACE("{"), R_PAR(")"), L_PAR("("), COMMA(","),
    PLUS("+"), MINUS("-"), NOT("!"), L_ANGLE("<"), R_ANGLE(">"),
    ASTERISK("*"), SLASH("/"), UNDERSCORE("_"), ASSIGN("="),
    LEQ_SIGN("<="), GEQ_SIGN(">="), EQUAL("=="), INCREMENT("++"),
    DECREMENT("--"), PLUS_ASSIGN("+="), MINUS_ASSIGN("-="),

    // KEYWORDS
    ARRAY("array"), INT_TYPE("int"), DOUBLE_TYPE("double"),
    STRING_TYPE("string"), BREAK("break"), FOR("for"),
    IF("if"), ELSE("else"), VOID("void"),
    RETURN("return"), RET("ret"), FUN("fun"), TO("to"),

    // VALUES
    INT, DOUBLE, STRING, IDENTIFIER,

    // MISC
    EOF("eof"), UNKNOWN("unknown"), TRUE("true"), FALSE("false");

    public String value;

    TokenType(String s) { value = s; }
    TokenType() {}
}

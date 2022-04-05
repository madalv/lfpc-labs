package labs.lab3;
import java.util.*;

public class Lexer {
    public Vector<Token> tokenize(String sourceCode) {
        Vector<Token> tokens = new Vector<>();
        for (int i = 0; i < sourceCode.length() - 1; i++) {
            char c = sourceCode.charAt(i);
            String next2Chars = "" + c + sourceCode.charAt(i + 1);
            Token singleOpTok = checkIfSingleCharOperator(c);
            Token doubleOpTok = checkIfDoubleCharOperator(next2Chars);

            if (checkIfIgnorable(c)) continue;
            else if (next2Chars.equals("//"))
                i = getCharAfterComment(i, sourceCode);
            else if (c == '\"')
                i = processString(sourceCode, i, tokens);
            else if (Character.isLetter(c))
                i = processIdentifier(sourceCode, i, tokens);
            else if (Character.isDigit(c))
                i = processNumber(sourceCode, i, tokens);
            else if (doubleOpTok != null) {
                tokens.add(doubleOpTok); i++;
            } else if (singleOpTok != null) tokens.add(singleOpTok);
            else tokens.add(new Token(TokenType.UNKNOWN, TokenType.UNKNOWN.value));
        }
        tokens.add(new Token(TokenType.EOF, TokenType.EOF.value));
        return tokens;
    }

    private int processString(String sourceCode, int i, Vector<Token> tokens) {
        int endIndex = getCurrString(i, sourceCode);
        String currString = sourceCode.substring(i + 1, endIndex);
        tokens.add(new Token(TokenType.STRING, currString));
        return endIndex - 1;
    }
    private int processIdentifier(String sourceCode, int i, Vector<Token> tokens) {
        int endIndex = getCurrWord(i, sourceCode);
        String currWord = sourceCode.substring(i, endIndex);
        Token tok = checkIfKeyword(currWord);
        if (tok != null) tokens.add(tok);
        else tokens.add(new Token(TokenType.IDENTIFIER, currWord));
        return endIndex - 1;
    }
    private int processNumber(String sourceCode, int i, Vector<Token> tokens) {
        int endIndex = getCurrNumber(i, sourceCode);
        String currNumber = sourceCode.substring(i, endIndex);
        if (currNumber.contains(".")) tokens.add(new Token(TokenType.DOUBLE, currNumber));
        else tokens.add(new Token(TokenType.INT, currNumber));
        return endIndex - 1;
    }
    private int getCurrWord(int i, String sourceCode) {
        int j = i;
        while (Character.isLetterOrDigit(sourceCode.charAt(j))) j++;
        return j;
    }
    private int getCurrNumber(int i, String sourceCode) {
        int j = i;
        while (Character.isDigit(sourceCode.charAt(j)) || sourceCode.charAt(j) == '.') j++;
        return j;
    }
    private int getCurrString(int i, String sourceCode) {
        int j = i + 1;
        while (sourceCode.charAt(j) != '\"') j++;
        return j;
    }


    private boolean checkIfIgnorable(char c) {
        return checkIfSpace(c)|| checkIfEOL(c) || c == '\t';
    }
    private boolean checkIfSpace(char c) {return c == ' ';}
    private int getCharAfterComment(int i, String sourceCode) {
        int j = i + 2;
        while (!checkIfEOL(sourceCode.charAt(j))) j++;
        return j - 1;
    }
    private boolean checkIfEOL(char c) {
        return c == '\n' || c == '\r';
    }
    private Token checkIfSingleCharOperator(char c) {
        return switch (c) {
            case ';' -> new Token(TokenType.SEMICOLON, TokenType.SEMICOLON.value);
            case '[' -> new Token(TokenType.L_BRACKET, TokenType.L_BRACKET.value);
            case ']' -> new Token(TokenType.R_BRACKET, TokenType.R_BRACKET.value);
            case '(' -> new Token(TokenType.L_PAR, TokenType.L_PAR.value);
            case ')' -> new Token(TokenType.R_PAR, TokenType.R_PAR.value);
            case '{' -> new Token(TokenType.L_BRACE, TokenType.L_BRACE.value);
            case '}' -> new Token(TokenType.R_BRACE, TokenType.R_BRACE.value);
            case ',' -> new Token(TokenType.COMMA, TokenType.COMMA.value);
            case '+' -> new Token(TokenType.PLUS, TokenType.PLUS.value);
            case '-' -> new Token(TokenType.MINUS, TokenType.MINUS.value);
            case '!' -> new Token(TokenType.NOT, TokenType.NOT.value);
            case '<' -> new Token(TokenType.L_ANGLE, TokenType.L_ANGLE.value);
            case '>' -> new Token(TokenType.R_ANGLE, TokenType.R_ANGLE.value);
            case '%' -> new Token(TokenType.MOD, TokenType.MOD.value);
            case '*' -> new Token(TokenType.ASTERISK, TokenType.ASTERISK.value);
            case '/' -> new Token(TokenType.SLASH, TokenType.SLASH.value);
            case '_' -> new Token(TokenType.UNDERSCORE, TokenType.UNDERSCORE.value);
            case '=' -> new Token(TokenType.ASSIGN, TokenType.ASSIGN.value);
            default -> null;
        };
    }
    private Token checkIfDoubleCharOperator(String s) {
        return switch (s) {
            case "<=" -> new Token(TokenType.LEQ_SIGN, TokenType.LEQ_SIGN.value);
            case ">=" -> new Token(TokenType.GEQ_SIGN, TokenType.GEQ_SIGN.value);
            case "==" -> new Token(TokenType.EQUAL, TokenType.EQUAL.value);
            case "++" -> new Token(TokenType.INCREMENT, TokenType.INCREMENT.value);
            case "--" -> new Token(TokenType.DECREMENT, TokenType.DECREMENT.value);
            case "-=" -> new Token(TokenType.MINUS_ASSIGN, TokenType.MINUS_ASSIGN.value);
            case "+=" -> new Token(TokenType.PLUS_ASSIGN, TokenType.PLUS_ASSIGN.value);
            default -> null;
        };
    }
    private Token checkIfKeyword(String s) {
        return switch (s.toLowerCase()) {
            case "array" -> new Token(TokenType.ARRAY, TokenType.ARRAY.value);
            case "int" -> new Token(TokenType.INT_TYPE, TokenType.INT_TYPE.value);
            case "double" -> new Token(TokenType.DOUBLE_TYPE, TokenType.DOUBLE_TYPE.value);
            case "string" -> new Token(TokenType.STRING_TYPE, TokenType.STRING_TYPE.value);
            case "break" -> new Token(TokenType.BREAK, TokenType.BREAK.value);
            case "for" -> new Token(TokenType.FOR, TokenType.FOR.value);
            case "if" -> new Token(TokenType.IF, TokenType.IF.value);
            case "void" -> new Token(TokenType.VOID, TokenType.VOID.value);
            case "return" -> new Token(TokenType.RETURN, TokenType.RETURN.value);
            case "else" -> new Token(TokenType.ELSE, TokenType.ELSE.value);
            case "ret", "returns" -> new Token(TokenType.RET, TokenType.RET.value);
            case "fun", "function" -> new Token(TokenType.FUN, TokenType.FUN.value);
            case "true" -> new Token(TokenType.TRUE, TokenType.TRUE.value);
            case "false" -> new Token(TokenType.FALSE, TokenType.FALSE.value);
            case "to" -> new Token(TokenType.TO, TokenType.TO.value);
            default -> null;
        };
    }
}

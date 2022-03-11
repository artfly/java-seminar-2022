package com.github.artfly.simplifier.lexer;

import com.github.artfly.simplifier.error.ErrorReporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lexer {

    private final BufferedReader br;
    private final ErrorReporter reporter;
    private final List<Token> tokens = new ArrayList<>();
    private int nLine;

    private Lexer(BufferedReader br, ErrorReporter reporter) {
        this.br = br;
        this.reporter = reporter;
    }

    public static List<Token> scan(BufferedReader br, ErrorReporter reporter) {
        Lexer lexer = new Lexer(br, reporter);
        return lexer.scan();
    }

    public List<Token> scan() {
        String line;
        boolean hasErrors = false;
        while ((line = getLine()) != null) {
            nLine++;
            int i = 0;
            while (i < line.length()) {
                char c = line.charAt(i);
                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }
                if (isDigit(c)) {
                    i = number(i, line);
                    continue;
                }
                if (Character.isLetter(c)) {
                    i = identifier(i, line);
                    continue;
                }
                TokenType tokenType = operatorType(c);
                if (tokenType == null) {
                    String error = unknownChar(line, i, c);
                    if (!reporter.report(error)) {
                        return null;
                    }
                    hasErrors = true;
                } else {
                    tokens.add(new Token(tokenType, nLine, i, c));
                }
                i++;
            }
            tokens.add(new Token(TokenType.EOL, nLine, -1, null));
        }
        tokens.add(new Token(TokenType.EOF, nLine, -1, null));

        return hasErrors ? null : tokens;
    }

    private String unknownChar(String line, int pos, char c) {
        return """
                Unknown char '%c' at line %d:
                %s
                %s^--- here
                """.formatted(c, nLine, line, " ".repeat(pos));
    }

    private static TokenType operatorType(char c) {
        return switch (c) {
            case '^' -> TokenType.POW;
            case '/' -> TokenType.DIVIDE;
            case '*' -> TokenType.MULTIPLY;
            case '+' -> TokenType.PLUS;
            case '-' -> TokenType.MINUS;
            case '(' -> TokenType.OP_BRACE;
            case ')' -> TokenType.CL_BRACE;
            default -> null;
        };
    }

    private String getLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int identifier(int i, String line) {
        return scanToken(i, line, TokenType.IDENTIFIER, Lexer::isLetterOrDigit, s -> s);
    }

    private int number(int i, String line) {
        return scanToken(i, line, TokenType.NUMBER, Lexer::isDigit, Integer::parseInt);
    }

    private int scanToken(int i, String line, TokenType tokenType, Predicate<Character> condition,
                          Function<String, Object> valueConvertor) {
        int start = i;
        do {
            i++;
        } while (i != line.length() && condition.test(line.charAt(i)));
        String value = line.substring(start, i);
        tokens.add(new Token(tokenType, nLine, start, valueConvertor.apply(value)));
        return i;
    }

    private static boolean isLetterOrDigit(char c) {
        return isDigit(c) || Character.isLetter(c);
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}

package com.github.artfly.simplifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/*
TODO:
1. i incremented incorrectly
2. error handling
3. number token has incorrect value type
 */
public class Lexer {

    private final BufferedReader br;
    private final List<Token> tokens = new ArrayList<>();

    Lexer(BufferedReader br) {
        this.br = br;
    }

    public List<Token> scan() {
        String line;
        while ((line = getLine()) != null) {
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (Character.isWhitespace(c)) {
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
                TokenType tokenType = switch (c) {
                    case '^' -> TokenType.POW;
                    case '/' -> TokenType.DIVIDE;
                    case '*' -> TokenType.MULTIPLY;
                    case '+' -> TokenType.PLUS;
                    case '-' -> TokenType.MINUS;
                    case '(' -> TokenType.OP_BRACE;
                    case ')' -> TokenType.CL_BRACE;
                    default -> null;
                };
                if (tokenType != null) {
                    tokens.add(new Token(tokenType, -1, c));
                    i = i + 1;
                    continue;
                }
                System.err.println("Unknown char: " + c);
            }
        }
        return tokens;
    }

    private String getLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int identifier(int i, String line) {
        return scanToken(i, line, TokenType.IDENTIFIER, Lexer::isLetterOrDigit);
    }

    // 1(
    private int number(int i, String line) {
        return scanToken(i, line, TokenType.NUMBER, Lexer::isDigit);
    }

    private int scanToken(int i, String line, TokenType tokenType, Predicate<Character> condition) {
        int start = i;
        do {
            i++;
        } while (i != line.length() && condition.test(line.charAt(i)));
        String value = line.substring(start, i);
        tokens.add(new Token(tokenType, -1, value));
        return i;
    }

    private static boolean isLetterOrDigit(char c) {
        return isDigit(c) || Character.isLetter(c);
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}

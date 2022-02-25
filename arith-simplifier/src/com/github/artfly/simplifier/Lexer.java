package com.github.artfly.simplifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                if (isOperator(c)) {
                    i = operator(i, line);
                    continue;
                }
                System.err.println("Unknown char: " + c);
            }
        }
        return null;
    }

    private String getLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int operator(int i, String line) {
        // TODO
        return i;
    }

    private boolean isOperator(char c) {
        // TODO
        return false;
    }

    private int identifier(int i, String line) {
        // TODO
        return i;
    }

    // 1(
    private int number(int i, String line) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(line.charAt(i));
            i++;
        } while (i != line.length() && Character.isDigit(line.charAt(i)));
        String value = sb.toString();
        tokens.add(new Token(TokenType.NUMBER, -1, value));
        return i;
    }


    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}

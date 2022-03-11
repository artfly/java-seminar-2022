package com.github.artfly.simplifier;


import com.github.artfly.simplifier.error.ConsoleReporter;
import com.github.artfly.simplifier.error.ErrorReporter;
import com.github.artfly.simplifier.lexer.Lexer;
import com.github.artfly.simplifier.lexer.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String expressions = """
                12 + 34 -> 46
                10 + -12 -> -2
                a + a -> 2 * a
                (ab3c + -a + b + a)^32 -> (abc + b)^2 -> abc^2 + 2 * a * b + b^2
                10 / a -> 10 / a
                (3 + 3 * 3)#!!!!
                ((12)) -> 12
                """;
        Reader reader = new StringReader(expressions);
        System.out.println((char) reader.read());
        BufferedReader br = new BufferedReader(reader);
        System.out.println(br.readLine());
        ErrorReporter reporter = new ConsoleReporter();
        List<Token> tokens = Lexer.scan(br, reporter);
        if (tokens == null) return;
        System.out.println(tokens);
    }
}

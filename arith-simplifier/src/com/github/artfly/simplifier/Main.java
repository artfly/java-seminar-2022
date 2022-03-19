package com.github.artfly.simplifier;


import com.github.artfly.simplifier.error.ConsoleReporter;
import com.github.artfly.simplifier.error.ErrorReporter;
import com.github.artfly.simplifier.lexer.Lexer;
import com.github.artfly.simplifier.lexer.Token;
import com.github.artfly.simplifier.parser.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String expressions = """
                12 + 34
                10 + -12
                a + a
                (ab3c + -a + b + a)^32
                10 / a
                (3 + 3 * 3)
                ((12))
                -2^2
                
                """;
        BufferedReader br = new BufferedReader(new StringReader(expressions));
        ErrorReporter reporter = new ConsoleReporter();
        List<Token> tokens = Lexer.scan(br, reporter);
        System.out.println(tokens);
        if (tokens == null) return;
        List<Expr> expr = Parser.parse(tokens, reporter);
        if (expr == null) return;
        expr.forEach(System.out::println);
    }
}

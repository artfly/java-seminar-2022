package com.github.artfly.simplifier;


import com.github.artfly.simplifier.error.ConsoleReporter;
import com.github.artfly.simplifier.error.ErrorReporter;
import com.github.artfly.simplifier.lexer.Lexer;
import com.github.artfly.simplifier.lexer.Token;
import com.github.artfly.simplifier.parser.*;
import com.github.artfly.simplifier.simplifier.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String exprText = """
                12 + 34
                10 + -12
                a + a
                (ab3c + -a + b + a)^32
                10 / a
                (3 + 3 * 3)
                ((12))
                -2^2
                -(2 + 2)^2
                (4 + b + a + 1)   +      3 * (5 + 2)
                2
                2 + 2
                2 * 2 + 2
                2 * 2 + 2 * 2
                (2 + 2 * 2) + (2 * 2 + 2)
                (a + 2 + b + 2) + (b + 2 + 2 + 2)
                """;
        BufferedReader br = new BufferedReader(new StringReader(exprText));
        ErrorReporter reporter = new ConsoleReporter();
        List<Token> tokens = Lexer.scan(br, reporter);
        System.out.println(tokens);
        if (tokens == null) return;
        List<Expr> expressions = Parser.parse(tokens, reporter);
        if (expressions == null) return;
        System.out.println("Before:");
        expressions.forEach(System.out::println);
        System.out.println("After:");
        expressions.stream().map(Simplifier::doSimplify).forEach(System.out::println);
    }
}

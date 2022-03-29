package com.github.artfly.simplifier;


import com.github.artfly.simplifier.error.ConsoleReporter;
import com.github.artfly.simplifier.error.ErrorReporter;
import com.github.artfly.simplifier.lexer.Lexer;
import com.github.artfly.simplifier.lexer.Token;
import com.github.artfly.simplifier.parser.*;
import com.github.artfly.simplifier.simplifier.Simplifier;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String exprText = """
                (x^2+y^2)^2
                x^3 + (x+y)^2
                (-3)^2
                -3^2
                (-x)^2
                (-x-y)^2
                2^12 + 1               
                (x+y+z)^2
                -(2)^3
                (x+y)^2+(2*x+2*x)^2
                (3*x)^2
                (2*x+3*y)^2
                (x+y)^2
                (x+y)^3
                (x+y+z)^4
                (x-y)^2
                2^4
                (2^2)^2
                (x+10)^2
                (x-y-z)^2
                """;
        BufferedReader br = new BufferedReader(new StringReader(exprText));
        ErrorReporter reporter = new ConsoleReporter();
        List<Expr> expressions = simplify(br, reporter);
        if (expressions == null) return;
        expressions.stream().map(Simplifier::doSimplify).forEach(System.out::println);
    }

    public static List<Expr> simplify(BufferedReader br, ErrorReporter reporter) {
        List<Token> tokens = Lexer.scan(br, reporter);
        if (tokens == null) return null;
        List<Expr> expressions = Parser.parse(tokens, reporter);
        if (expressions == null) return null;
        return expressions.stream().map(Simplifier::doSimplify).collect(Collectors.toList());
    }
}

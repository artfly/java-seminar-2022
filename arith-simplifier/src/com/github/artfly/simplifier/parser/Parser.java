package com.github.artfly.simplifier.parser;


import com.github.artfly.simplifier.error.*;
import com.github.artfly.simplifier.lexer.*;

import java.util.*;

/*

2 * 3 + 4

     +
   /  \
  *   4
 /\
2 3

expr := binary | unary
binary := expr '+' expr | expr '*' expr
unary := NUMBER

2 * 3 + 4

expr -> binary -> expr '+' expr -> (binary) '+' unary -> (expr '*' expr) '+' unary ->
-> (unary '*' unary) '+' unary

     +
   /  \
  *   4
 /\
2 3

expr -> binary -> (expr) '*' (expr) -> unary '*' (expr '+' expr) ->
-> unary '*' (unary '+' unary)

       *
      / \
     2  +
       /\
      3  4

1) precedence
2) associativity

a = b = c
a + b + c

expr := binary | unary
binary := expr '+' expr | expr '*' expr
unary := NUMBER


2 + 3
2

2 * 3 + 4

expr := term
term := factor '+' term | factor
factor := primary | primary '*' factor
primary := NUMBER

2 * 3 + 4

term -> factor '+' term -> (primary '*' factor) '+' primary ->
(primary '*' primary) + primary

+ - / * ^ () -1

low -> high
1) + -
2) * /
3) ^
4) -1
5) ()

expr := term
term := factor '+' term | factor
factor := primary | primary '*' factor
primary := NUMBER

expr := term
term := factor ('+' | '-') term | factor
factor := power ('*' | '/') factor | power
power := unary '^' unary | unary
unary := primary | '-' unary
primary := VAR | NUMBER | '(' expr ')'
 */
public class Parser {
    
    private final List<Token> tokens;
    private final ErrorReporter errorReporter;
    private int pos;

    public Parser(List<Token> tokens, ErrorReporter errorReporter) {
        this.tokens = tokens;
        this.errorReporter = errorReporter;
    }
    
    public static List<Expr> parse(List<Token> tokens, ErrorReporter errorReporter) {
        Parser parser = new Parser(tokens, errorReporter);
        return parser.parse();
    }
    
    private List<Expr> parse() {
        List<Expr> expressions = new ArrayList<>();
        while (!matches(TokenType.EOF)) {
            Expr expr = parseExpr();
            expressions.add(expr);
            advance(TokenType.EOL);
        }
        return expressions;
    }
    
    // expr := term
    private Expr parseExpr() {
        return parseTerm();
    }
    
    // term := factor ('+' | '-') term | factor
    // 2 + 3 + 4
    private Expr parseTerm() {
        Expr expr = parseFactor();
        if (matches(TokenType.PLUS, TokenType.MINUS)) {
            Token token = advance();
            Operator op = token.tokenType() == TokenType.PLUS ? Operator.PLUS : Operator.MINUS;
            Expr rhs = parseTerm();
            return new Expr.Binary(expr, rhs, op);
        }
        return expr;
    }

    private boolean matches(TokenType... expected) {
        if (expected.length == 0) return true;
        Token token = tokens.get(pos);
        TokenType actual = token.tokenType();
        for (TokenType t : expected) {
            if (actual == t) return true;
        }
        return false;
    }

    private Token advance(TokenType... expected) {
        if (!matches(expected)) {
            // TODO
            throw new IllegalStateException("Actual token: " + tokens.get(pos).tokenType());
        }
        Token token = tokens.get(pos);
        if (token.tokenType() != TokenType.EOF) pos++;
        return token;
    }

    // factor := power ('*' | '/') factor | power
    private Expr parseFactor() {
        Expr expr = parsePower();
        if (matches(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token token = advance();
            Operator op = token.tokenType() == TokenType.MULTIPLY ? Operator.MULTIPLY : Operator.DIVIDE;
            Expr rhs = parseFactor();
            return new Expr.Binary(expr, rhs, op);
        }
        return expr;
    }

    // TODO
    // power := unary '^' power | unary
    private Expr parsePower() {
        Expr expr = parseUnary();
        if (matches(TokenType.POW)) {
            advance();
            Expr rhs = parsePower();
            return new Expr.Binary(expr, rhs, Operator.POWER);
        }
        return expr;
    }
    
    // unary := primary | '-' unary := ('-')* primary
    private Expr parseUnary() {
        boolean isNegated = false;
        while (matches(TokenType.MINUS)) {
            isNegated = !isNegated;
            advance();
        }
        Expr expr = parsePrimary();
        if (!isNegated) return expr;
        return new Expr.Negated(expr);
    }
    
    // primary := VAR | NUMBER | '(' expr ')'
    private Expr parsePrimary() {
        if (matches(TokenType.OP_BRACE)) {
            advance();
            Expr expr = parseExpr();
            advance(TokenType.CL_BRACE);
            return expr;
        }
        if (matches(TokenType.NUMBER)) {
            Token token = advance();
            return new Expr.Number((Integer) token.value());
        }
        if (matches(TokenType.IDENTIFIER)) {
            Token token = advance();
            return new Expr.Var((String) token.value());
        }
        advance(TokenType.CL_BRACE, TokenType.IDENTIFIER, TokenType.NUMBER);
        throw new IllegalStateException("Should not reach");
    }
}

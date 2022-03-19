package com.github.artfly.simplifier.parser;


import com.github.artfly.simplifier.error.*;
import com.github.artfly.simplifier.lexer.*;

import java.util.*;

public class Parser {

    private final List<Token> tokens;
    private final ErrorReporter errorReporter;
    private int pos;

    private Parser(List<Token> tokens, ErrorReporter errorReporter) {
        this.tokens = tokens;
        this.errorReporter = errorReporter;
    }

    public static List<Expr> parse(List<Token> tokens, ErrorReporter errorReporter) {
        Parser parser = new Parser(tokens, errorReporter);
        return parser.parse();
    }

    private static String unexpectedToken(Token token, TokenType... expected) {
        int pos = token.pos();
        String position = pos == -1 ? 
                "End of line " + token.nLine() :
                "Line " + token.nLine() + ", position: " + pos;
        return """
                %s
                Expected tokens: %s,
                Actual: %s
                """.formatted(position, Arrays.toString(expected), token.tokenType());
    }

    private List<Expr> parse() {
        boolean hasErrors = false;
        List<Expr> expressions = new ArrayList<>();
        while (!matches(TokenType.EOF)) {
            try {
                Expr expr = parseExpr();
                expressions.add(expr);
                consume(TokenType.EOL);
            } catch (ParserException e) {
                hasErrors = true;
                if (!errorReporter.report(e.getMessage())) {
                    return null;
                }
            }
        }
        return hasErrors ? null : expressions;
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

    // factor := unary ('*' | '/') factor | unary
    private Expr parseFactor() {
        Expr expr = parseUnary();
        if (matches(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token token = advance();
            Operator op = token.tokenType() == TokenType.MULTIPLY ? Operator.MULTIPLY : Operator.DIVIDE;
            Expr rhs = parseFactor();
            return new Expr.Binary(expr, rhs, op);
        }
        return expr;
    }

    // unary := ('-')* power
    private Expr parseUnary() {
        boolean isNegated = false;
        while (matches(TokenType.MINUS)) {
            isNegated = !isNegated;
            advance();
        }
        Expr expr = parsePower();
        if (!isNegated) return expr;
        return new Expr.Negated(expr);
    }

    // power := primary '^' power | primary
    private Expr parsePower() {
        Expr expr = parsePrimary();
        if (matches(TokenType.POW)) {
            advance();
            Expr rhs = parsePower();
            return new Expr.Binary(expr, rhs, Operator.POWER);
        }
        return expr;
    }

    // primary := VAR | NUMBER | '(' expr ')'
    private Expr parsePrimary() {
        if (matches(TokenType.OP_BRACE)) {
            advance();
            Expr expr = parseExpr();
            consume(TokenType.CL_BRACE);
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
        String message = unexpectedToken(advance(), TokenType.OP_BRACE, TokenType.IDENTIFIER, TokenType.NUMBER);
        throw new ParserException(message);
    }

    private boolean matches(TokenType first, TokenType... rest) {
        Token token = tokens.get(pos);
        TokenType actual = token.tokenType();
        if (actual == first) return true;
        for (TokenType expected : rest) {
            if (actual == expected) return true;
        }
        return false;
    }

    private Token advance() {
        Token token = tokens.get(pos);
        if (token.tokenType() != TokenType.EOF) pos++;
        return token;
    }

    private void consume(TokenType expected) {
        Token token = advance();
        if (token.tokenType() != expected) {
            throw new ParserException(unexpectedToken(token, expected));
        }
    }
}

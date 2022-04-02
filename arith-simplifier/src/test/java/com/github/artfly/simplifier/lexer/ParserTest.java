package com.github.artfly.simplifier.lexer;

import com.github.artfly.simplifier.error.ErrorReporter;
import com.github.artfly.simplifier.parser.Expr;
import com.github.artfly.simplifier.parser.Operator;
import com.github.artfly.simplifier.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParserTest {

     @Test
     public void empty() {
          List<Expr> exprs = parse(tokens());
          Assert.assertTrue(exprs.isEmpty());
     }

     @Test
     public void number() {
          List<Expr> exprs = parse(tokens(new TokenInfo(TokenType.NUMBER, 12345)));
          Assert.assertEquals(1, exprs.size());
          Assert.assertEquals(new Expr.Number(12345), exprs.get(0));
     }

     @Test
     public void var() {
          List<Expr> exprs = parse(tokens(new TokenInfo(TokenType.IDENTIFIER, "foo")));
          Assert.assertEquals(1, exprs.size());
          Assert.assertEquals(new Expr.Var("foo"), exprs.get(0));
     }

     @Test
     public void binary() {
          TokenInfo[] operators = {
                  new TokenInfo(TokenType.MINUS, "-"),
                  new TokenInfo(TokenType.POW, "^"),
                  new TokenInfo(TokenType.MULTIPLY, "*"),
                  new TokenInfo(TokenType.DIVIDE, "/"),
                  new TokenInfo(TokenType.PLUS, "+")
          };
          TokenInfo[] operands = {
                  new TokenInfo(TokenType.IDENTIFIER, "foo"),
                  new TokenInfo(TokenType.NUMBER, 12345)
          };

          for (TokenInfo operator : operators) {
               for (TokenInfo lhs : operands) {
                    for (TokenInfo rhs : operands) {
                         List<Expr> exprs = parse(tokens(lhs, operator, rhs));
                         Assert.assertEquals(1, exprs.size());
                         Expr.Binary expected = new Expr.Binary(lhs.expr(), rhs.expr(), operator.op());
                         Assert.assertEquals(expected, exprs.get(0));
                    }
               }
          }
     }

     private static List<Token> tokens(TokenInfo... tokenInfos) {
          List<Token> tokens = new ArrayList<>();
          for (TokenInfo tokenInfo : tokenInfos) {
               tokens.add(new Token(tokenInfo.type, -1, -1, tokenInfo.value));
          }
          tokens.add(new Token(TokenType.EOL, -1, -1, null));
          tokens.add(new Token(TokenType.EOF, -1, -1, null));
          return tokens;
     }

     private static List<Expr> parse(List<Token> tokens) {
          return Parser.parse(tokens, ErrorReporter.EMPTY);
     }

     private record TokenInfo(TokenType type, Object value) {

          private Expr expr() {
               return switch (type) {
                    case NUMBER -> new Expr.Number((Integer) value);
                    case IDENTIFIER -> new Expr.Var((String) value);
                    default -> throw new UnsupportedOperationException();
               };
          }

          public Operator op() {
               return switch (type) {
                    case MINUS -> Operator.MINUS;
                    case PLUS -> Operator.PLUS;
                    case DIVIDE -> Operator.DIVIDE;
                    case MULTIPLY -> Operator.MULTIPLY;
                    case POW -> Operator.POWER;
                    default -> throw new UnsupportedOperationException();
               };
          }
     }
}

package com.github.artfly.simplifier.lexer;

import com.github.artfly.simplifier.Main;
import com.github.artfly.simplifier.error.ErrorReporter;
import com.github.artfly.simplifier.parser.Expr;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

public class IntegrationTest {

    @Test
    public void test() {
        String exprText = """
                12 + 34
                10 + -12
                """;
        BufferedReader br = new BufferedReader(new StringReader(exprText));
        List<Expr> actual = Main.simplify(br, ErrorReporter.EMPTY);
        Assert.assertNotNull(actual);
        List<Expr> expected = List.of(
          new Expr.Number(46),
          new Expr.Number(-2)
        );
        Assert.assertEquals(expected, actual);
    }

}

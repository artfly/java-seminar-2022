package com.github.artfly.simplifier.lexer;

import com.github.artfly.simplifier.error.ErrorReporter;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;

public class LexerTest {

    private static List<TokenType> scan(String expressions) {
        BufferedReader br = new BufferedReader(new StringReader(expressions));
        List<Token> tokens = Lexer.scan(br, ErrorReporter.EMPTY);
        return tokens == null ? null : tokens.stream().map(Token::tokenType).toList();
    }

    private static void assertTypes(List<TokenType> types, TokenType... expected) {
        MatcherAssert.assertThat(types, is(List.of(expected)));
    }

    @Test
    public void empty() {
        assertTypes(scan(""), TokenType.EOF);
    }

    @Test
    public void allowedChars() {
        String allowed = """
                0123456789
                ^/*-+()
                abcdefghijklmnopqrstuvwxyz
                ABCDEFGHIJKLMNOPQRSTUVWXYZ
                """;
        List<TokenType> tokenTypes = scan(allowed);
        Assert.assertNotNull(tokenTypes);
        Set<TokenType> actual = new HashSet<>(tokenTypes);
        TokenType[] expected = TokenType.values();
        Assert.assertEquals(actual.size(), expected.length);
        MatcherAssert.assertThat(actual, CoreMatchers.hasItems(expected));
    }

    @Test
    public void unknownChar() {
        Assert.assertNull(scan("$"));
    }

    @Test
    public void eol() {
        assertTypes(scan("\n\n"), TokenType.EOL, TokenType.EOL, TokenType.EOF);
    }

    @Test
    public void strangeDigits() {
        // some strange digits from Character.isDigit() javadoc
        String digits = "\u0660\u06F0\u0966\uFF10";
        for (int i = 0; i < digits.length(); i++) {
            Assert.assertNull(scan(String.valueOf(digits.charAt(i))));
        }
    }
}

package com.github.artfly.simplifier.lexer;

public record Token(TokenType tokenType, int nLine, int pos, Object value) {}
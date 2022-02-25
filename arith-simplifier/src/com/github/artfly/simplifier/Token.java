package com.github.artfly.simplifier;

public record Token(TokenType tokenType, int nLine, Object value) {}
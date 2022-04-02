package com.github.artfly.simplifier.error;

public interface ErrorReporter {

    ErrorReporter EMPTY = message -> true;

    /**
     * @param message error message
     * @return false if it no more errors can be reported
     */
    boolean report(String message);
}

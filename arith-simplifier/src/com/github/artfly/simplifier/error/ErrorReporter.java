package com.github.artfly.simplifier.error;

public interface ErrorReporter {
    /**
     * @param message error message
     * @return false if it no more errors can be reported
     */
    boolean report(String message);
}

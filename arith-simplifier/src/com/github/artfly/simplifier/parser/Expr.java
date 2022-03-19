package com.github.artfly.simplifier.parser;

public sealed interface Expr {
    record Var(String name) implements Expr {

        @Override
        public String toString() {
            return name;
        }
    }
    record Number(int value) implements Expr {
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
    // -1  ----1 -(2 + 4)
    record Negated(Expr expr) implements Expr {
        @Override
        public String toString() {
            return "-(" + expr + ")"; 
        }
    }
    record Binary(Expr lhs, Expr rhs, Operator op) implements Expr {
        @Override
        public String toString() {
            return "(" + lhs + ")" + op + "(" + rhs + ")";
        }
    }
}
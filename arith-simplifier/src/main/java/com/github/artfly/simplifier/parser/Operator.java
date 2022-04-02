package com.github.artfly.simplifier.parser;

public enum Operator {
    PLUS {
        @Override
        public String toString() {
            return "+";
        }
    },
    MINUS {
        @Override
        public String toString() {
            return "-";
        }
    },
    DIVIDE {
        @Override
        public String toString() {
            return "/";
        }
    },
    MULTIPLY {
        @Override
        public String toString() {
            return "*";
        }
    },
    POWER {
        @Override
        public String toString() {
            return "^";
        }
    }
}

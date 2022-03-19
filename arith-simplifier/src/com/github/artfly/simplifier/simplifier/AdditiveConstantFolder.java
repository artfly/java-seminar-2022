package com.github.artfly.simplifier.simplifier;

import com.github.artfly.simplifier.parser.*;

public class AdditiveConstantFolder implements Simplifier {

    private int value;

    @Override
    public Expr simplify(Expr expr) {
        return switch (expr) {
            case Expr.Number n -> n;
            case Expr.Var v -> v;
            case Expr.Negated n -> n;
            case Expr.Binary b -> {
                Operator op = b.op();
                if (op != Operator.PLUS && op != Operator.MINUS) {
                    // TODO
                }
                Expr lhs = simplify(b.lhs());
                Expr rhs = simplify(b.rhs());
                if (lhs instanceof Expr.Number n1 && rhs instanceof Expr.Number n2) {
                    boolean negated = op == Operator.MINUS;
                    yield new Expr.Number(n1.value() + (negated ? -1 : 1) * n2.value());
                }
                if (lhs instanceof Expr.Number n) {
                    value += n.value();
                    yield rhs;
                }
                if (rhs instanceof Expr.Number n) {
                    boolean negated = op == Operator.MINUS;
                    value += (negated ? -1 : 1) * n.value();
                    yield lhs;
                }
                yield b;
            } 
        };
    }
}

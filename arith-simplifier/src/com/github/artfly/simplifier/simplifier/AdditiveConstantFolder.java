package com.github.artfly.simplifier.simplifier;

import com.github.artfly.simplifier.parser.*;

public class AdditiveConstantFolder implements Simplifier {

    private int value;

    @Override
    public Expr simplify(Expr expr) {
        expr = fold(expr);
        if (value != 0) {
            expr = new Expr.Binary(new Expr.Number(value), expr, Operator.PLUS);
        }
        value = 0;
        return expr;
    }
    
    private Expr fold(Expr expr) {
        return switch (expr) {
            case Expr.Number n -> n;
            case Expr.Var v -> v;
            case Expr.Negated n -> n;
            case Expr.Binary b -> {
                Operator op = b.op();
                if (op != Operator.PLUS && op != Operator.MINUS && value != 0) {
                    Expr.Number lhs = new Expr.Number(value);
                    value = 0;
                    Expr rhs = fold(b);
                    yield new Expr.Binary(lhs, rhs, Operator.PLUS);
                }
                Expr lhs = fold(b.lhs());
                Expr rhs = fold(b.rhs());
                if (op != Operator.PLUS && op != Operator.MINUS && value == 0) {
                    yield new Expr.Binary(lhs, rhs, op);
                }
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
                yield new Expr.Binary(lhs, rhs, op);
            }
        };
    }
}

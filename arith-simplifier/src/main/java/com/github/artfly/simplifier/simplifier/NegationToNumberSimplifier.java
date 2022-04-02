package com.github.artfly.simplifier.simplifier;

import com.github.artfly.simplifier.parser.Expr;

public class NegationToNumberSimplifier implements Simplifier {

    @Override
    public Expr simplify(Expr expr) {
        if (expr instanceof Expr.Binary b) {
            return new Expr.Binary(simplify(b.lhs()), simplify(b.rhs()), b.op());
        }
        if (expr instanceof Expr.Negated n) {
            if (n.expr() instanceof Expr.Number num) return new Expr.Number(num.value() * -1);
            return n;
        }
        return expr;
    }
}

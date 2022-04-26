package com.github.artfly.simplifier.simplifier;

import com.github.artfly.simplifier.parser.Expr;
import com.github.artfly.simplifier.parser.Operator;
public class ExponentiationPolynomial implements Simplifier{
    @Override
    public Expr simplify(Expr expr) {
        return exponentiation(expr);
    }

    private int factorial(int n) {
        int result = 1;
        for (int i = 2; i <= n; i++)
            result *= i;
        return result;
    }

    private int truncatedFactorial(int k, int n){
        int result = 1;
        for (int i = k + 1; i < n; i++){
            result *= i;
        }
        return result;
    }

    int getBinomialCoefficient(int k, int n){
        if (k > (n - k)) return truncatedFactorial(k, n) / factorial(n - k);
        return truncatedFactorial(n - k, n) / factorial(k);
    }

    private Expr exponentiation(Expr expr){
        return switch (expr) {
            case Expr.Number n -> n;
            case Expr.Var v -> v;
            case Expr.Negated n -> new Expr.Negated(exponentiation(n.expr()));
            case Expr.Binary b -> {
                if (b.op() != Operator.POWER){
                    Expr lhs = exponentiation(b.lhs());
                    Expr rhs = exponentiation(b.rhs());
                    yield new Expr.Binary(lhs, rhs, b.op());
                }
                if (b.lhs() instanceof Expr.Binary bin){
                    if (bin.op() == Operator.POWER){
                        Expr degree = exponentiation(new Expr.Binary(bin.rhs(), b.rhs(), Operator.POWER));
                        yield exponentiation(new Expr.Binary(bin.lhs(), degree, Operator.POWER));
                    }
                }
                if (b.rhs() instanceof Expr.Number n2) {
                    if (b.lhs() instanceof Expr.Number n1) yield new Expr.Number((int) Math.pow(n1.value(), n2.value()));
                    if (b.lhs() instanceof  Expr.Negated n1){
                        Expr ex = exponentiation(getExprInDegree((n1.expr()), n2.value()));
                        if (n2.value() % 2 == 0) yield ex;
                        yield new Expr.Negated(ex);
                    }
                    if (b.lhs() instanceof Expr.Var n1){
                        if (n2.value() == 1) yield n1;
                        yield new Expr.Binary(n1, n2, Operator.POWER);
                    }
                    if (b.lhs() instanceof Expr.Binary lhsBin) {
                        if (lhsBin.op() == Operator.PLUS || lhsBin.op() == Operator.MINUS) yield createBinomialSeries(lhsBin, n2);
                        yield new Expr.Binary(exponentiation(getExprInDegree(lhsBin.lhs(), n2.value())),
                                exponentiation(getExprInDegree(lhsBin.rhs(), n2.value())),
                                lhsBin.op());
                    }
                }
                yield expr;
            }
        };
    }

    private Expr createBinomialSeries(Expr.Binary lhs, Expr.Number degree) {
        int length = degree.value();
        Expr result = exponentiation(getExprInDegree(lhs.lhs(), length));
        for (int i = 1; i < length; i++){
            Expr firstMultiplier  = exponentiation(getExprInDegree(lhs.lhs(), length - i));
            Expr secondMultiplier = exponentiation(getExprInDegree(lhs.rhs(), i));

            Expr mixed = new Expr.Binary(firstMultiplier, secondMultiplier, Operator.MULTIPLY);
            Expr member = new Expr.Binary(new Expr.Number(getBinomialCoefficient(i, length)), mixed, Operator.MULTIPLY);
            result = new Expr.Binary(result, member, lhs.op());
        }
        Expr lastMember = exponentiation(getExprInDegree(lhs.rhs(), length));
        result = new Expr.Binary(result, lastMember, Operator.PLUS);
        return result;
    }

    private Expr.Binary getExprInDegree(Expr expr, int degree) {
        return new Expr.Binary(expr, new Expr.Number(degree), Operator.POWER);
    }
}
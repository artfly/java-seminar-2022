package com.github.artfly.simplifier.simplifier;

import com.github.artfly.simplifier.parser.Expr;

public interface Simplifier {

    Simplifier[] SIMPLIFIERS = { new AdditiveConstantFolder() }; 
    Expr simplify(Expr expr);
    
    static Expr doSimplify(Expr expr) {
        for (Simplifier simplifier : SIMPLIFIERS) {
            expr = simplifier.simplify(expr);
        }
        return expr;
    }
}

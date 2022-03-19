package com.github.artfly.simplifier.simplifier;

import com.github.artfly.simplifier.parser.*;

public interface Simplifier {

    Simplifier[] SIMPLIFIERS = {}; 
    Expr simplify(Expr expr);
    
    default Expr doSimplify(Expr expr) {
        for (Simplifier simplifier : SIMPLIFIERS) {
            expr = simplifier.simplify(expr);
        }
        return expr;
    }
}

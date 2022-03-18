package com.github.artfly.simplifier.parser;



/*

2 * 3 + 4

     +
   /  \
  *   4
 /\
2 3

expr := binary | unary
binary := expr '+' expr | expr '*' expr
unary := NUMBER

2 * 3 + 4

expr -> binary -> expr '+' expr -> (binary) '+' unary -> (expr '*' expr) '+' unary ->
-> (unary '*' unary) '+' unary

     +
   /  \
  *   4
 /\
2 3

expr -> binary -> (expr) '*' (expr) -> unary '*' (expr '+' expr) ->
-> unary '*' (unary '+' unary)

       *
      / \
     2  +
       /\
      3  4

1) precedence
2) associativity

a = b = c
a + b + c

expr := binary | unary
binary := expr '+' expr | expr '*' expr
unary := NUMBER


2 + 3
2

2 * 3 + 4

expr := term
term := factor '+' term | factor
factor := primary | primary '*' factor
primary := NUMBER

2 * 3 + 4

term -> factor '+' term -> (primary '*' factor) '+' primary ->
(primary '*' primary) + primary

+ - / * ^ () -1

low -> high
1) + -
2) * /
3) ^
4) -1
5) ()

expr := term
term := factor '+' term | factor
factor := primary | primary '*' factor
primary := NUMBER

expr := term
term := factor ('+' | '-') term | factor
factor := power ('*' | '/') factor | power
power := power '^' unary | unary
unary := primary | '-' unary
primary := VAR | NUMBER | '(' expr ')'


 */
public class Parser {
}

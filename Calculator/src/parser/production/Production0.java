package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production0 extends Production {
    public Production0() {
        super("Expr", new String[] {"ArithExpr"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens.get(0).getValue(), "Expr");
    }
}

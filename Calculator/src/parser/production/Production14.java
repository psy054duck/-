package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production14 extends Production {
    public Production14() {
        super("BoolExpr", new String[] {"!", "BoolExpr"});
    }

    public Token action(ArrayList<Token> tokens) {
        boolean res = Boolean.valueOf(tokens.get(0).getValue());

        return new Token(String.valueOf(! res), "BoolExpr");
    }
}

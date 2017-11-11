package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production3 extends Production {
    public Production3() {
        super("ArithExpr", new String[] {"T"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens.get(0).getValue(), "ArithExpr");
    }
}

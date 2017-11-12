package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production6 extends Production {
    public Production6() {
        super("ArithExpr", new String[] {"Decimal"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens.get(0).getValue(), "ArithExpr");
    }
}

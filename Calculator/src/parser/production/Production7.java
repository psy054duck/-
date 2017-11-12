package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production7 extends Production {
    public Production7() {
        super("ArithExpr", new String[] {"(", "ArithExpr", ")"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens.get(1).getValue(), "ArithExpr");
    }
}

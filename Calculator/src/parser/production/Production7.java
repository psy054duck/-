package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production7 extends Production {
    public Production7() {
        super("ArithExpr/BoolExpr", new String[] {"(", "ArithExpr/BoolExpr", ")"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens.get(1).getValue(), tokens.get(1).getType());
    }
}

package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production10 extends Production {
    public Production10() {
        super("F", new String[] {"(", "ArithExpr", ")"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens.get(1).getValue(), "F");
    }
}

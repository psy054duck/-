package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production9 extends Production {
    public Production9() {
        super("F", new String[] {"Decimal"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens.get(0).getValue(), "F");
    }
}

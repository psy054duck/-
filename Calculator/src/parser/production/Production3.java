package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production3 extends Production {
    public Production3() {
        super("T", new String[] {"T", "*", "P"});
    }

    public Token action(ArrayList<Token> tokens) {
        double rightOperand = Double.valueOf(tokens.get(0).getValue());
        double leftOperand = Double.valueOf(tokens.get(2).getValue());
        double res = leftOperand * rightOperand;

        return new Token(String.valueOf(res), "T");
    }
}

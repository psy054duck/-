package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production7 extends Production {
    public Production7() {
        super("P", new String[] {"P", "^", "Decimal"});
    }

    public Token action(ArrayList<Token> tokens) {
        double rightOperand = Double.valueOf(tokens.get(0).getValue());
        double leftOperand = Double.valueOf(tokens.get(2).getValue());
        double res = Math.pow(leftOperand, rightOperand);

        return new Token(String.valueOf(res), "P");
    }
}

package parser.production;

import parser.token.Token;

import java.util.ArrayList;

public class Production1 extends Production {
    public Production1() {
        super("ArithExpr", new String[] {"ArithExpr", "+", "ArithExpr"});
    }

    public Token action(ArrayList<Token> tokens) {
        double rightOperand = Double.valueOf(tokens.get(0).getValue());
        double leftOperand = Double.valueOf(tokens.get(2).getValue());
        double res = leftOperand + rightOperand;

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production4 extends Production {
    public Production4() {
        super("ArithExpr", new String[] {"ArithExpr", "/", "ArithExpr"});
    }

    public Token action(ArrayList<Token> tokens) {
        double rightOperand = Double.valueOf((tokens.get(0).getValue()));
        double leftOperand = Double.valueOf((tokens.get(2).getValue()));
        double res = leftOperand / rightOperand;

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

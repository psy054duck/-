package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production10 extends Production {
    public Production10() {
        super("ArithExpr", new String[] {"BoolExpr", "?", "ArithExpr", ":", "ArithExpr"});
    }

    public Token action(ArrayList<Token> tokens) {
        boolean predicate = Boolean.valueOf(tokens.get(4).getValue());
        double leftOperand = Double.valueOf(tokens.get(2).getValue());
        double rightOperand = Double.valueOf(tokens.get(0).getValue());
        double res = predicate ? leftOperand : rightOperand;

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

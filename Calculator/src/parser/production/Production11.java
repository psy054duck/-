package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production11 extends Production {
    public Production11() {
        super("BoolExpr", new String[] {"BoolExpr", "&", "BoolExpr"});
    }

    public Token action(ArrayList<Token> tokens) {
        boolean leftOperand = Boolean.valueOf(tokens.get(2).getValue());
        boolean rightOperand = Boolean.valueOf(tokens.get(0).getValue());
        boolean res = leftOperand & rightOperand;

        return new Token(String.valueOf(res), "BoolExpr");
    }
}

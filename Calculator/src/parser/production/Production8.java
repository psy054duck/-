package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production8 extends Production {
    public Production8() {
        super("ArithExpr", new String[] {"UnaryFunc", "(", "ArithExpr", ")"});
    }

    public Token action(ArrayList<Token> tokens) {
        double operand = Double.valueOf(tokens.get(1).getValue());
        double res = 0;

        if (tokens.get(3).getValue().equals("sin")) {
            res = Math.sin(operand);
        } else {
            res = Math.cos(operand);
        }

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

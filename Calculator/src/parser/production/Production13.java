package parser.production;

import parser.token.*;

import java.util.ArrayList;

public class Production13 extends Production {
    public Production13() {
        super("ArithExpr", new String[] {"VariableFunc", "(", "ArithList", ")"});
    }

    public Token action(ArrayList<Token> tokens) {
        ArrayList<Double> operands = new ArrayList<Double>();
        for (Token token : tokens) {
            if (token.getType().equals("ArithExpr")) {
                operands.add(Double.valueOf(token.getValue()));
            }
        }
        double max = -Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        double res = 0;

        for (Double operand : operands) {
            if (operand > max) {
                max = operand;
            }
            if (operand < min) {
                min = operand;
            }
        }

        if (tokens.get(tokens.size()-1).getValue().equals("max")) {
            res = max;
        } else {
            res = min;
        }

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

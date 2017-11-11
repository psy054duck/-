package parser.production;

import parser.token.Token;

public class Production1 extends Production {
    public Production1() {
        super("ArithExpr", new String[] {"ArithExpr", "+", "T"});
    }

    public Token action(ArrayList<Token> tokens) {
        double rightOperand = Double.valueOf(tokens[0].getValue());
        double leftOperand = Double.valueOf(tokens[2].getValue());
        double res = leftOperand + rightOperand;

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

package parser.production;

public class Production2 extends Production {
    public Production2() {
        super("ArithExpr", new String[] {"ArithExpr", "-", "T"});
    }

    public Token action(ArrayList<Token> tokens) {
        double rightOperand = Double.valueOf(tokens[0].getValue());
        double leftOperand = Double.valueOf(tokens[2].getValue());
        double res = leftOperand + rightOperand;

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

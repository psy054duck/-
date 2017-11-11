package parser.production;

public class Production7 extends Production {
    public Production7() {
        super("P", new String[] {"P", "^", "Decimal"});
    }

    public Token action(ArrayList<Token> tokens) {
        double rightOperand = Double.valueOf(tokens[0].getValue());
        double leftOperand = Double.valueOf(tokens[3].getValue());
        double res = Math.pow(leftOperand, rightOperand);

        return new Token(String.valueOf(res), "P");
    }
}

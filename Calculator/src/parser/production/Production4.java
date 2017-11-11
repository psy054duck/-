package parser.production;

public class Production4 extends Production {
    public Production4() {
        super("T", new String[] {"T", "*", "P"});
    }

    public Token action(ArrayList<Token> tokens) {
        double rightOperand = Double.valueOf(tokens[0].getValue());
        double leftOperand = Double.valueOf(tokens[2].getValue());
        double res = leftOperand + rightOperand;

        return new Token(String.valueOf(res), "T");
    }
}

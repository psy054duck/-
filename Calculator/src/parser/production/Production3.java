package parser.production;

public class Production3 extends Production {
    public Production3() {
        super("ArithExpr", new String[] {"T"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens[0].getValue(), "ArithExpr");
    }
}

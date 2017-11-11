package parser.production;

public class Production0 extends Production {
    public Production0() {
        super("Expr", new String[] {"ArithExpr"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens[0].getValue(), "Expr");
    }
}

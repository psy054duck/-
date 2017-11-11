package parser.production;

public class Production10 extends Production {
    public Production10() {
        super("F", new String[] {"(", "ArithExpr", ")"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens[1].getValue(), "F");
    }
}

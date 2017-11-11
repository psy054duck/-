package parser.production;

public class Production6 extends Production {
    public Production6() {
        super("T", new String[] {"P"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens[0].getValue(), "T");
    }
}

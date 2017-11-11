package parser.production;

public class Production8 extends Production {
    public Production8() {
        super("P", new String[] {"F"});
    }

    public Token action(ArrayList<Token> tokens) {
        return new Token(tokens[0].getValue(), "P");
    }
}

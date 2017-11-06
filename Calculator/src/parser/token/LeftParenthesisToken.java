package parser.token;

public class LeftParenthesisToken extends Token {
    public LeftParenthesisToken() {
        super("(");
    }

    public String getType() {
        return "LeftParenthesis";
    }
}

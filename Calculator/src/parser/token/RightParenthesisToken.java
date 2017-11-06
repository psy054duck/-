package parser.token;

public class RightParenthesisToken extends Token {
    public RightParenthesisToken() {
        super(")");
    }

    public String getType() {
        return "RightParenthesis";
    }
}

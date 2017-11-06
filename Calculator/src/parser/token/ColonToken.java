package parser.token;

public class ColonToken extends Token {
    public ColonToken() {
        super(":");
    }

    public String getType() {
        return "Colon";
    }
}

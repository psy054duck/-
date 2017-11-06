package parser.token;

public class CompToken extends Token {
    public CompToken(String v) {
        super(v.toLowerCase());
    }

    public String getType() {
        return "Comp";
    }
}

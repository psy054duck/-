package parser.token;

public class BoolToken extends Token {
    public BoolToken(String v) {
        super(v.toLowerCase());
    }

    public String getType() {
        return "Bool";
    }
}

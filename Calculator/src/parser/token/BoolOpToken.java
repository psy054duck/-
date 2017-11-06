package parser.token;

public class BoolOpToken extends Token {
    public BoolOpToken(String v) {
        super(v);
    }

    public String getType() {
        return "BoolOp";
    }
}

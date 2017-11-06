package parser.token;

public class BinaryOpToken extends Token {
    public BinaryOpToken(String v) {
        super(v);
    }

    public String getType() {
        return "BinaryOp";
    }
}

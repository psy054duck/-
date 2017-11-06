package parser.token;

public class UnaryFuncToken extends Token {
    public UnaryFuncToken(String v) {
        super(v.toLowerCase());
    }

    public String getType() {
        return "UnaryFunc";
    }
}

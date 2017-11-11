package parser.token;

public class Token {
    private String value;
    private String type;

    public Token() {}

    public Token(String v) {
        value = v;
    }

    public Token(String v, String t) {
        value = v;
        type = t;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}

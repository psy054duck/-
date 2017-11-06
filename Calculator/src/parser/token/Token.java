package parser.token;

abstract public class Token {
    private String value;

    public Token() {}

    public Token(String v) {
        value = v;
    }

    public abstract String getType();

    public String getValue() {
        return value;
    }
}

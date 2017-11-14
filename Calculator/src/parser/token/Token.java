package parser.token;

import java.util.ArrayList;

public class Token {
    private String value;
    private String type;
    private ArrayList<Token> children;

    public Token() {}

    public Token(String v) {
        children = new ArrayList<Token>();
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

    public ArrayList<Token> getChildren() {
        return children;
    }
}

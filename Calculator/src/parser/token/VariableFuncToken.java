package parser.token;

public class VariableFuncToken extends Token {
    public VariableFuncToken(String v) {
        super(v.toLowerCase());
    }

    public String getType() {
        return "VariableFunc";
    }
}

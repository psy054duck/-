package parser.token;

public class DecimalToken extends Token {
    public DecimalToken(double value) {
        super(String.valueOf(value));
    }

    public DecimalToken(String value) {
        super(value);
    }

    public String getType() {
        return "Decimal";
    }
}

package parser.token;

public class MinusToken extends Token {
    public MinusToken() {
        super("-");
    }

    public String getType() {
        return "Minus";
    }
}

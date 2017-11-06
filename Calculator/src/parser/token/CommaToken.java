package parser.token;

public class CommaToken extends Token {
    public CommaToken() {
        super(",");
    }

    public String getType() {
        return "Comma";
    }
}

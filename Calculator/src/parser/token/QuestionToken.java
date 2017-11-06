package parser.token;

public class QuestionToken extends Token {
    public QuestionToken() {
        super("?");
    }

    public String getType() {
        return "Question";
    }
}

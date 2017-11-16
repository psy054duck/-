package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public class Production10 extends Production {
    public Production10() {
        super("ArithExpr", new String[] {"BoolExpr", "?", "ArithExpr", ":", "ArithExpr"});
    }

    public Token action(ArrayList<Token> tokens) throws MissingOperandException,
                                                        TypeMismatchedException,
                                                        TrinaryOperationException {
        if ((tokens.get(0).getType().equals("BoolExpr")
            || tokens.get(2).getType().equals("BoolExpr")
            || tokens.get(4).getType().equals("ArithExpr"))) {
                throw new TypeMismatchedException();
        }
        boolean question = false, colon = false;
        for (Token token : tokens) {
            if (token.getType().equals("?")) {
                question = true;
            } else if (token.getType().equals(":")) {
                colon = true;
            }
        }
        if (! (question && colon)) {
            throw new TrinaryOperationException();
        }

        for (int i = 0; i < bodyLength(); ++i) {
            if (! tokens.get(bodyLength() - i - 1).getType().equals(body[i])) {
                throw new MissingOperandException();
            }
        }
        boolean predicate = Boolean.valueOf(tokens.get(4).getValue());
        double leftOperand = Double.valueOf(tokens.get(2).getValue());
        double rightOperand = Double.valueOf(tokens.get(0).getValue());
        double res = predicate ? leftOperand : rightOperand;

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

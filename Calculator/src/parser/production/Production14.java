package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public class Production14 extends Production {
    public Production14() {
        super("BoolExpr", new String[] {"!", "BoolExpr"});
    }

    public Token action(ArrayList<Token> tokens) throws MissingOperandException,
                                                        TypeMismatchedException {
        if (tokens.get(1).getType().equals("!")) {
            if (! tokens.get(0).getType().equals("BoolExpr")) {
                  throw new TypeMismatchedException();
            }
        }

        for (int i = 0; i < bodyLength(); ++i) {
            if (! tokens.get(bodyLength() - i - 1).getType().equals(body[i])) {
                throw new MissingOperandException();
            }
        }
        boolean res = Boolean.valueOf(tokens.get(0).getValue());

        return new Token(String.valueOf(! res), "BoolExpr");
    }
}

package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public class Production11 extends Production {
    public Production11() {
        super("BoolExpr", new String[] {"BoolExpr", "&", "BoolExpr"});
    }

    public Token action(ArrayList<Token> tokens) throws MissingOperandException,
                                                        TypeMismatchedException {
        if (tokens.get(1).getType().equals("&")) {
            if (! tokens.get(0).getType().equals("BoolExpr")
              || ! tokens.get(2).getType().equals("BoolExpr")) {
                  throw new TypeMismatchedException();
            }
        }

        for (int i = 0; i < bodyLength(); ++i) {
            if (! tokens.get(i).getType().equals(body[i])) {
                throw new MissingOperandException();
            }
        }
        boolean leftOperand = Boolean.valueOf(tokens.get(2).getValue());
        boolean rightOperand = Boolean.valueOf(tokens.get(0).getValue());
        boolean res = leftOperand & rightOperand;

        return new Token(String.valueOf(res), "BoolExpr");
    }
}

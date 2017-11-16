package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public class Production3 extends Production {
    public Production3() {
        super("ArithExpr", new String[] {"ArithExpr", "*", "ArithExpr"});
    }

    public Token action(ArrayList<Token> tokens) throws MissingOperandException,
                                                        TypeMismatchedException {
        if (tokens.get(1).getType().equals("+")) {
            if (! tokens.get(0).getType().equals("ArithExpr")
              || ! tokens.get(2).getType().equals("ArithExpr")) {
                  throw new TypeMismatchedException();
            }
        }

        for (int i = 0; i < bodyLength(); ++i) {
            if (! tokens.get(i).getType().equals(body[i])) {
                throw new MissingOperandException();
            }
        }
        double rightOperand = Double.valueOf(tokens.get(0).getValue());
        double leftOperand = Double.valueOf(tokens.get(2).getValue());
        double res = leftOperand * rightOperand;

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

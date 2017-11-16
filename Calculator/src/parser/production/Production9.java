package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public class Production9 extends Production {
    public Production9() {
        super("ArithList", new String[] {"ArithExpr", "Comp", "ArithExpr"});
    }

    public Token action(ArrayList<Token> tokens) throws MissingOperandException,
                                                        TypeMismatchedException {
        if (tokens.get(1).getType().equals("Comp")) {
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
        double leftOperand = Double.valueOf(tokens.get(2).getValue());
        double rightOperand = Double.valueOf(tokens.get(0).getValue());
        String operator = tokens.get(1).getValue();
        boolean res = false;
        
        if (operator.equals("<")) {
            res = leftOperand < rightOperand;
        } else if (operator.equals(">")) {
            res = leftOperand > rightOperand;
        } else if (operator.equals("=")) {
            res = leftOperand == rightOperand;
        } else if (operator.equals("<=")) {
            res = leftOperand <= rightOperand;
        } else if (operator.equals(">=")) {
            res = leftOperand >= rightOperand;
        } else {
            res = leftOperand != rightOperand;
        }

        return new Token(String.valueOf(res), "BoolExpr");
    }
}

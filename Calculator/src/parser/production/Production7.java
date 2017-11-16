package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public class Production7 extends Production {
    public Production7() {
        super("ArithExpr/BoolExpr", new String[] {"(", "ArithExpr/BoolExpr", ")"});
    }

    public Token action(ArrayList<Token> tokens) throws MissingOperandException,
                                                        MissingLeftParenthesisException {
        if (! tokens.get(1).getType().equals("(")
           && ! tokens.get(2).getType().equals("(")) {
               throw new MissingLeftParenthesisException();
           }
        if (! tokens.get(0).getType().equals(")")) {
            throw new MissingOperandException();
        }
        if (! tokens.get(2).getType().equals("(")) {
            throw new MissingOperandException();
        }
        if (! tokens.get(1).getType().equals("ArithExpr")
           && ! tokens.get(1).getType().equals("BoolExpr")) {
            throw new MissingOperandException();
        }
        return new Token(tokens.get(1).getValue(), tokens.get(1).getType());
    }
}

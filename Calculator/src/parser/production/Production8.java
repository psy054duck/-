package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public class Production8 extends Production {
    public Production8() {
        super("ArithExpr", new String[] {"UnaryFunc", "UnaryLeftP", "ArithExpr", "UnaryRightP"});
    }

    public Token action(ArrayList<Token> tokens) throws MissingOperandException,
                                                        TypeMismatchedException,
                                                        FunctionCallException {

        ArrayList<Token> parameters = new ArrayList<>();
        for (Token token : tokens) {
            if (token.getType().equals("ArithExpr")) {
                parameters.add(token);
            }
        }
        if (tokens.size() < 4) {
            throw new MissingOperandException();
        } else if (parameters.size() > 1) {
            throw new FunctionCallException();
        }
        for (Token token : tokens) {
            if (token.getType().equals("BoolExpr")) {
                throw new TypeMismatchedException();
            }
        }
        double operand = Double.valueOf(parameters.get(0).getValue());
        double res = 0;

        if (tokens.get(tokens.size()-1).getValue().equals("sin")) {
            res = Math.sin(operand);
        } else {
            res = Math.cos(operand);
        }

        return new Token(String.valueOf(res), "ArithExpr");
    }
}

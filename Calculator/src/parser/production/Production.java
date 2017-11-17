package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public abstract class Production {
    protected String head;
    protected String[] body;

    /**
     * The action should do when parser decides to reduce.
     * 
     * @param tokens the tokens should be reduced
     * 
     * @throws ExpressionException if any expression error occur
     * 
     * @return the head of the production
     */
    public abstract Token action(ArrayList<Token> tokens) throws SyntacticException,
                                                                 SemanticException;

    /**
     * Constructor with head and body
     * 
     * @param h the head of this production
     * @param b the body of this production
     */
    public Production(String h, String[] b) {
        head = h;
        body = b;
    }

    /**
     * Get the number of symbols in the body.
     * 
     * @return the number of symbols in the body
     */
    public int bodyLength() {
        return body.length;
    }

    /**
     * Get the type of the head.
     * 
     * @return the type of the head
     */
    public String getHead() {
        return head;
    }

    /**
     * Get the types of the symbols in body.
     * 
     * @return the types of the symbols in body
     */
    public String[] getBody() {
        return body;
    }

    /**
     * Auxiliar method to print this production
     */
    public void print() {
        System.out.print(head + "  ->  ");
        for (String s : body) {
            System.out.print(s + " ");
        }
        System.out.print("\n");
    }
}

package parser.production;

import parser.token.*;
import exceptions.*;

import java.util.ArrayList;

public abstract class Production {
    protected String head;
    protected String[] body;

    public abstract Token action(ArrayList<Token> tokens) throws ExpressionException;

    public Production(String h, String[] b) {
        head = h;
        body = b;
    }

    public int bodyLength() {
        return body.length;
    }

    public String getHead() {
        return head;
    }

    public String[] getBody() {
        return body;
    }

    public void print() {
        System.out.print(head + "  ->  ");
        for (String s : body) {
            System.out.print(s + " ");
        }
        System.out.print("\n");
    }
}

package parser;

import parser.token.*;
import parser.production.*;
import exceptions.*;
import tools.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private Scanner scanner;
    private SymbolStack stack = new SymbolStack();
    private Table table = new Table();
    private Token lookahead;
    private Grammer grammer;

    public Parser(String expression) throws Exception {
        lookahead = null;
        scanner = new Scanner(expression);
        grammer = new Grammer();
        stack.add(new Token("$", "$"));
    }

    public double parse() throws Exception {
        lookahead = scanner.getNextToken();
        if (lookahead.getType().equals("$")) {
            throw new EmptyExpressionException();
        }
        while (true) {
            // stack.printType();
            // stack.printValue();
            if (lookahead.getValue().equals("$")
             && stack.topOperator().getValue().equals("$")) {
                break;
            }
            String prevType = stack.top().getType();
            if ((prevType.equals("UnaryFunc") || prevType.equals("VariableFunc"))
                && ! lookahead.getType().equals("(")) {
                    throw new FunctionCallException();
            }
            String action = null;
            lookahead = stack.convert(lookahead);
            if (lookahead.getType().equals("ArithExpr") || lookahead.getType().equals("BoolExpr")) {
                if (stack.top().getType().equals("ArithExpr") || stack.top().getType().equals("BoolExpr")) {
                    throw new MissingOperatorException();
                }
            }
            action = table.get(stack.topOperator().getType(),
                               lookahead.getType());

            if (action.charAt(0) == '<') {
                shift();
            } else {
                reduce(Integer.valueOf(action.substring(1)));
            }
        }

        if (! stack.top().getType().equals("ArithExpr")) {
            throw new TypeMismatchedException();
        }
        return Double.valueOf(stack.top().getValue());
    }

    private void shift() throws Exception {
        stack.add(lookahead);
        lookahead = scanner.getNextToken();
    }

    private void reduce(int index) throws ExpressionException {
        Production production = grammer.get(index);
        ArrayList<Token> tokens = null;
        if (index == 13) {
            tokens = new ArrayList<Token>();
            Token token = stack.pop();
            while (! token.getType().equals("VariableFunc")) {
                tokens.add(token);
                token = stack.pop();
                if (tokens.get(tokens.size()-1).getType().equals(",")
                    && token.getType().equals(",")) {
                        throw new MissingOperandException();
                }
                if (token.getType().equals(",") && tokens.get(tokens.size()-1).getValue().equals(")")) {
                    throw new MissingOperandException();
                }
                if (token.getValue().equals("(") && tokens.get(tokens.size()-1).getType().equals(",")) {
                    throw new MissingOperandException();
                }
            }
            tokens.add(token);
        } else if (index == 8) {
            tokens = new ArrayList<Token>();
            Token token = stack.pop();
            while (! token.getType().equals("UnaryFunc")) {
                tokens.add(token);
                token = stack.pop();
            }
            tokens.add(token);
        } else if (index == 10) {
            if (stack.size()-1 < production.bodyLength()) {
                if (stack.hasType("?")) {
                    throw new MissingOperandException();
                } else {
                    throw new TrinaryOperationException();
                }
            }
            tokens = stack.remove(production.bodyLength());
        } else {
            if (stack.size()-1 < production.bodyLength()) {
                throw new MissingOperandException();
            }
            tokens = stack.remove(production.bodyLength());
        }
        Token newToken = production.action(tokens);
        if (newToken.getType().equals("ArithExpr")
           && stack.top().getType().equals("ArithExpr")) {
            throw new MissingOperatorException();
        }
        stack.add(production.action(tokens));
    }

    public static void main(String[] args) throws Exception {
        Parser parser = new Parser("1+true ? 1 : 2");
        System.out.println(parser.parse());
    }
}

class SymbolStack {
    private ArrayList<Token> stack;
    private String[] operators = "+ - * / ^ ( ) UnaryLeftP UnaryRightP Comp ? : & | Negative , VariableLeftP VariableRightP ! $".split(" ");

    public SymbolStack() {
        stack = new ArrayList<Token>();
    }

    public void add(Token token) {
        stack.add(token);
    } 

    public Token topOperator() {
        for (int i = stack.size()-1; i >= 0; --i) {
            for (String s : operators) {
                if (s.equals(stack.get(i).getType())) {
                    return stack.get(i);
                }
            }
        }
        return new Token("Error", "Error");
    }

    public int size() {
        return stack.size();
    }

    public void printValue() {
        for (Token token : stack) {
            System.out.print(token.getValue() + " ");
        }
        System.out.print("\n");
    }

    public void printType() {
        for (Token token : stack) {
            System.out.print(token.getType() + " ");
        }
        System.out.print("\n");
    }

    public Token pop() {
        return stack.remove(stack.size()-1);
    }

    public Token convert(Token token) throws MissingLeftParenthesisException {
        token = analyzeParenthesis(token);
        // token = analyzeComma(token);
        token = analyzeDecimal(token);
        token = analyzeMinus(token);

        return token;
    }

    public Token analyzeMinus(Token token) {
        String prevType = top().getType();

        if (token.getType().equals("-")) {
            for (String s : operators) {
                if (prevType.equals(s)) {
                    return new Token("-", "Negative");
                }
            }
        }

        return token;
    }

    public Token analyzeDecimal(Token token) {
        String type = token.getType();

        if (type.equals("Decimal")) {
            return new Token(token.getValue(), "ArithExpr");
        }
        return token;
    }

    public Token analyzeParenthesis(Token token) throws MissingLeftParenthesisException {
        String value = token.getValue();
        if (value.equals("(")) {
            String prevType = top().getType();
            String newType = null;

            if (prevType.equals("UnaryFunc")) {
                newType = "UnaryLeftP";
            } else if (prevType.equals("VariableFunc")) {
                newType = "VariableLeftP";
            } else {
                newType = "(";
            }

            return new Token(token.getValue(), newType);
        } else if (value.equals(")")) {
            int pos = stack.size()-1;
            for (; pos >= 0 && ! stack.get(pos).getValue().equals("("); --pos);

            if (pos < 0) {
                throw new MissingLeftParenthesisException();
            }

            String leftType = stack.get(pos).getType();
            String newType = null;

            if (leftType.equals("UnaryLeftP")) {
                newType = "UnaryRightP";
            } else if (leftType.equals("VariableLeftP")) {
                newType = "VariableRightP";
            } else {
                newType = ")";
            }

            return new Token(token.getValue(), newType);
        } else {
            return token;
        }
    }

    public Token analyzeComma(Token token) {
        if (! token.getValue().equals(",")) {
            return token;
        }

        String prevType = topOperator().getType();
        if (prevType.equals("VariableLeftP")) {
            return new Token(",", "FuncComma");
        } else {
            return new Token(",", "ListComma");
        }
    }

    public Token top() {
        return stack.get(stack.size()-1);
    }

    public boolean hasType(String type) {
        for (Token t : stack) {
            if (type.equals(t.getType())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Token> remove(int num) {
        ArrayList<Token> tokens = new ArrayList<Token>();

        for (int i = 0; i < num; ++i) {
            tokens.add(stack.remove(stack.size()-1));
        }

        return tokens;
    }
}

class Grammer {
    private ArrayList<Production> productions;
    private final int numProduction = 15;

    public Grammer() throws Exception {
        productions = new ArrayList<Production>();
        for (int i = 0; i < numProduction; ++i) {
            productions.add((Production) Class.forName("parser.production.Production"+String.valueOf(i)).newInstance());
        }
    }

    public Production get(int index) {
        return productions.get(index);
    }
}

class Table {
    private HashMap<String, Integer> map = new HashMap<String, Integer>();
    private String[] symbols = "+ - * / ^ ( ) UnaryLeftP UnaryRightP Comp ? : & | Negative , VariableLeftP VariableRightP ! $".split(" ");
    private final String[][] table = {
        /*                 Operator Precedence Table                     */
        /*****************************************************************/
        /*+      -      *      /      ^      (      )     ULP    URP    COMP    ?      :      &      |     NEG     ,     VLP   VRP    !     $ */
        {">1" , ">1" , "<"  , "<"  , "<"  , "<"  , ">1" , "<"  , ">1" , ">1" , ">1" , ">1" , ">1" , ">1" , "<"  , ">1" , "<" , ">1" , "<", ">1" ,},  // +
        {">2" , ">2" , "<"  , "<"  , "<"  , "<"  , ">2" , "<"  , ">2" , ">2" , ">2" , ">2" , ">2" , ">2" , "<"  , ">2" , "<" , ">2" , "<", ">2" ,},  // -
        {">3" , ">3" , ">3" , ">3" , "<"  , "<"  , ">3" , "<"  , ">3" , ">3" , ">3" , ">3" , ">3" , ">3" , "<"  , ">3" , "<" , ">3" , "<", ">3" ,},  // *
        {">4" , ">4" , ">4" , ">4" , "<"  , "<"  , ">4" , "<"  , ">4" , ">4" , ">4" , ">4" , ">4" , ">4" , "<"  , ">4" , "<" , ">4" , "<", ">4" ,},  // /
        {">5" , ">5" , ">5" , ">5" , "<"  , "<"  , ">5" , "<"  , ">5" , ">5" , ">5" , ">5" , ">5" , ">5" , "<"  , ">5" , "<" , ">5" , "<", ">5" ,},  // ^
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , " "  , "<"  , "<"  , "<"  , "<"  , "<" , " "  , "<", " "  ,},  // (
        {">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , ">7" , " "  , ">7", ">7" , ">7", ">7",},  // )
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , " "  , "<"  , "<"  , "<"  , "<"  , "<" , " "  , "<", " "  ,},  // ULP
        {">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , ">8" , " "  , ">8", ">8" , ">8", ">8",},  // URP
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , ">9" , "<"  , ">9" , ">9" , ">9" , ">9"  , ">9" , ">9" , "<"  , ">9" , "<" , ">9" , " ", ">9" ,},  // COMP
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , " "  , "<"  , " "  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , " "  , "<" , " "  , "<", " "  ,},  // ?
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , ">10", "<"  , ">10", "<"  , "<"  , ">10", "<"  , "<"  , "<"  , ">10", "<" , ">10", "<", ">10",},  // :
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , ">11", "<"  , ">11", "<"  , ">11", " "  , ">11", ">11", "<"  , " "  , "<" , ">11", "<", ">11",},  // &
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , ">12", "<"  , ">12", "<"  , ">12", " "  , "<"  , ">12", "<"  , " "  , "<" , ">12", "<", ">12",},  // |
        {">6" , ">6" , ">6" , ">6" , ">6" , "<"  , ">6" , " "  , ">6" , ">6" , ">6" , ">6" , ">6" , ">6" , "<"  , ">6" , "<" , ">6" , ">6", ">6",},  // NEG
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , " "  , "<"  , "<"  , "<"  , "<"  , "<" , "<"  , " ", " "  ,},  // ,
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , " "  , "<"  , " "  , "<"  , "<"  , " "  , "<"  , "<"  , "<"  , "<"  , "<" , "<"  , "<", " "  ,},  // VLP
        {">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", ">13", " " , ">13", " ", ">13",},  // VRP
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , ">14", "<"  , ">14", "<"  , ">14", " "  , ">14", ">14", "<"  , " "  , "<" , " "  , "<", " "  ,},  // !
        {"<"  , "<"  , "<"  , "<"  , "<"  , "<"  , " "  , "<"  , " "  , "<"  , "<"  , "<"  , "<"  , "<"  , "<"  , " "  , "<" , "<"  , "<", "<"  ,},  // $
    };

    public Table() {
        for (int i = 0; i < symbols.length; ++i) {
            map.put(symbols[i], i);
        }
    }

    public String get(String left, String right) throws MissingRightParenthesisException,
                                                        TrinaryOperationException {
        if (left.equals("(") && right.equals(":")) {
            throw new TrinaryOperationException();
        }
        if (left.equals(",") && right.equals("$")) {
            throw new MissingRightParenthesisException();
        }
        if (left.equals("?") && right.equals("$")) {
            throw new TrinaryOperationException();
        }
        if ((left.equals("(") || left.equals("UnaryLeftP") || left.equals("VariableLeftP"))
           && right.equals("$")) {
            throw new MissingRightParenthesisException();
        }
        if (left.equals(")") || left.equals("UnaryRightP") || left.equals("VariableRightP")) {
            return table[map.get(left)][0];
        }
        if (right.equals("ArithExpr") || right.equals("UnaryFunc") || right.equals("BoolExpr") || right.equals("VariableFunc")) {
            return "<";
        }
        // System.out.println(left);
        // System.out.println(right);
        return table[map.get(left)][map.get(right)];
    }
}

package parser;

import parser.token.*;
import parser.production.*;

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
        while (true) {
            stack.printType();
            if (lookahead.getValue().equals("$")
             && stack.topOperator().getValue().equals("$")) {
                break;
            }
            String action = null;
            lookahead = stack.convert(lookahead);
            action = table.get(stack.topOperator().getType(),
                               lookahead.getType());

            if (action.charAt(0) == '<') {
                shift();
            } else {
                reduce(Integer.valueOf(action.substring(1)));
            }
        }

        return Double.valueOf(stack.top().getValue());
    }

    private void shift() throws Exception {
        stack.add(lookahead);
        lookahead = scanner.getNextToken();
    }

    private void reduce(int index) {
        Production production = grammer.get(index);
        ArrayList<Token> tokens = stack.remove(production.bodyLength());
        stack.add(production.action(tokens));
    }

    public static void main(String[] args) throws Exception {
        Parser parser = new Parser(" -(15)");
        System.out.println(parser.parse());
    }
}

class SymbolStack {
    private ArrayList<Token> stack;
    private String[] operators = "+ - * / ^ ( ) UnaryLeftP UnaryRightP Comp ? : & | Negative $".split(" ");

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

    public Token convert(Token token) {
        token = analyzeParenthesis(token);
        token = analyzeComma(token);
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
    public Token analyzeParenthesis(Token token) {
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
    private final int numProduction = 13;

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
    private final String[][] table = {
        /*                 Operator Precedence Table                     */
        /*****************************************************************/
        /*+     -     *     /     ^     (     )     ULP   URP    COMP   ?      :      &       |     NEG    $ */
        {">1", ">1", "<" , "<" , "<" , "<" , ">1" , "<" , ">1" , ">1", ">1" , ">1" , ">1"  , ">1" , "<" , ">1" ,},  // +
        {">2", ">2", "<" , "<" , "<" , "<" , ">2" , "<" , ">2" , ">2", ">2" , ">2" , ">2"  , ">2" , "<" , ">2" ,},  // -
        {">3", ">3", ">3", ">3", "<" , "<" , ">3" , "<" , ">3" , ">3", ">3" , ">3" , ">3"  , ">3" , "<" , ">3" ,},  // *
        {">4", ">4", ">4", ">4", "<" , "<" , ">4" , "<" , ">4" , ">4", ">4" , ">4" , ">4"  , ">4" , "<" , ">4" ,},  // /
        {">5", ">5", ">5", ">5", "<" , "<" , ">5" , "<" , ">5" , ">5", ">5" , ">5" , ">5"  , ">5" , "<" , ">5" ,},  // ^
        {"<" , "<" , "<" , "<" , "<" , "<" , "<"  , "<" , "<"  , "<" , " "  , " "  , "<"   , "<"  , "<" , "<"  ,},  // (
        {">7", ">7", ">7", ">7", ">7", ">7", ">7" , ">7", ">7" , ">7", ">7" , ">7" , ">7"  , ">7" , ">7", ">7" ,},  // )
        {"<" , "<" , "<" , "<" , "<" , "<" , "<"  , "<" , "<"  , "<" , "<"  , " "  , "<"   , "<"  , "<" , "<"  ,},  // ULP
        {">8", ">8", ">8", ">8", ">8", ">8", ">8" , ">8", ">8" , ">8", ">8" , ">8" , ">8"  , ">8" , ">8", ">8" ,},  // URP
        {"<" , "<" , "<" , "<" , "<" , "<" , ">9" , "<" , " "  , ">9", ">9" , " "  , ">9"  , ">9" , "<" , ">9" ,},  // COMP
        {"<" , "<" , "<" , "<" , "<" , "<" , " "  , "<" , " "  , "<" , "<"  , "<"  , "<"   , "<"  , "<" , " "  ,},  // ?
        {"<" , "<" , "<" , "<" , "<" , "<" , " "  , "<" , ">10", "<" , "<"  , ">10", "<"   , "<"  , "<" , ">10",},  // :
        {"<" , "<" , "<" , "<" , "<" , "<" , ">11", "<" , ">11", "<" , ">11", " "  , ">11" , ">11", "<" , ">11",},  // &
        {"<" , "<" , "<" , "<" , "<" , "<" , ">12", "<" , ">12", "<" , ">12", " "  , "<"   , ">12", "<" , ">12",},  // |
        {">6", ">6", ">6", ">6", ">6", "<" , ">6" , " " , ">6" , ">6", ">6" , ">6" , ">6"  , ">6" , "<" , ">6" ,},  // NEG
        {"<" , "<" , "<" , "<" , "<" , "<" , " "  , "<" , " "  , "<" , "<"  , " "  , "<"   , "<"  , "<" , "<"  ,},  // $
    };

    public Table() {
        String[] symbols = "+ - * / ^ ( ) UnaryLeftP UnaryRightP Comp ? : & | Negative $".split(" ");
        for (int i = 0; i < symbols.length; ++i) {
            map.put(symbols[i], i);
        }
    }

    public String get(String left, String right) {
        if (right.equals("ArithExpr") || right.equals("UnaryFunc") || right.equals("BoolExpr")) {
            return "<";
        }
        // System.out.println(left);
        // System.out.println(right);
        return table[map.get(left)][map.get(right)];
    }
}

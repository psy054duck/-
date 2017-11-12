package parser;

import parser.token.*;
import parser.production.*;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.SysexMessage;

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
        String[] symbols = "+ - * / ^ $".split(" ");
        lookahead = scanner.getNextToken();
        while (true) {
            if (lookahead.getValue().equals("$")
               && stack.topOperator().getValue().equals("$")) {
                   break;
            }
            String action = null;
            action = table.get(stack.topOperator().getType(), lookahead.getType());

            if (action.charAt(0) == '<') {
                shift();
            } else {
                reduce(action.charAt(1)-'0');
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
        Parser parser = new Parser("1*(2+3)");
        System.out.println(parser.parse());
    }
}

class SymbolStack {
    private ArrayList<Token> stack;
    private String[] operators = "+ - * / ^ ( ) $".split(" ");

    public SymbolStack() {
        stack = new ArrayList<Token>();
    }

    public void add(Token token) {
        stack.add(token);
    } 

    public Token topOperator() {
        for (int i = stack.size()-1; i >= 0; --i) {
            for (String s : operators) {
                if (s.equals(stack.get(i).getValue())) {
                    return stack.get(i);
                }
            }
        }
        return new Token("Error", "Error");
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

    public Grammer() throws Exception {
        productions = new ArrayList<Production>();
        for (int i = 0; i < 8; ++i) {
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
        {">1", ">1", "<", "<", "<", "<", ">1", ">1",},
        {">2", ">2", "<", "<", "<", "<", ">2", ">2",},
        {">3", ">3", ">3", ">3", "<", "<", ">3", ">3",},
        {">4", ">4", ">4", ">4", "<", "<", ">4", ">4",},
        {">5", ">5", ">5", ">5", "<", "<", ">5", ">5",},
        {"<", "<", "<", "<", "<", "<", "<", "<",},
        {">7", ">7", ">7", ">7", ">7", ">7", ">7", ">7"},
        {"<", "<", "<", "<", "<", "<", "<", "<",},
    };

    public Table() {
        String[] symbols = "+ - * / ^ ( ) $".split(" ");
        for (int i = 0; i < symbols.length; ++i) {
            map.put(symbols[i], i);
        }
    }

    public String get(String left, String right) {
        if (right.equals("Decimal")) {
            return "<";
        }
        return table[map.get(left)][map.get(right)];
    }
}

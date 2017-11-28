package parser;

import parser.token.*;
import parser.production.*;
import exceptions.*;
import tools.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An OPP parser of this calculator.
 * It consists of an operator precedence table and a symbol stack.
 */
public class Parser {
    private Scanner scanner;
    private SymbolStack stack = new SymbolStack();
    private Table table = new Table();
    private Token lookahead;
    private Grammer grammer;

    /**
     * Constructor with the expression to be calculated.
     * It will instantiate a scanner and a symbol stack.
     * A dollar symbol marks the bottom of this stack.
     * 
     * @throws Exception if any error occurs
     * @param expression the expression to be calculated
     */
    public Parser(String expression) throws Exception {
        lookahead = null;
        scanner = new Scanner(expression);
        grammer = new Grammer();
        stack.add(new Token("$", "$"));
    }

    /**
     * The most important method provided by Parser.
     * This method will do the whole job of parsing.
     * Parser will get the next token by invoking the scanner's
     * getNextToken method, and then look up the operator precedence table
     * to judge whether it should shift or reduce.
     * Note that the semantic behavior is embedded in reduce.
     * 
     * @throws LexicalException if the expression has lexical error
     * @throws SyntacticException if the expression has syntax error
     * @throws SemanticException if the expression has semantic error
     * 
     * @return the result of this expression
     */
    public double parse() throws LexicalException,
                                 SyntacticException, 
                                 SemanticException {
        lookahead = scanner.getNextToken();
        if (lookahead.getType().equals("$")) {
            throw new EmptyExpressionException();
        }
        while (true) {
            stack.printType();
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

    /**
     * Parser looks up the operator precedence table to judge whether
     * it should do shift or reduce.
     * This method will add the lookahead to the stack, and get the
     * next token.
     * 
     * @throws LexicalException if the expression has lexical error
     */
    private void shift() throws LexicalException {
        stack.add(lookahead);
        lookahead = scanner.getNextToken();
    }

    /**
     * Parser looks up the operator precedence table to judge whether
     * it should do shift or reduce.
     * This method will do the job of reducing according to the
     * production referred by index.
     * 
     * @param index used to refer to a spefici production
     * 
     * @throws SyntacticException if the expression has syntax error
     * @throws SemanticException if the expression has semantic error
     */
    private void reduce(int index) throws SyntacticException,
                                          SemanticException {
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
        Parser parser = new Parser("1+2 ? 3 : 4");
        System.out.println(parser.parse());
    }
}

/**
 * Special stack providing some useful operations for parser.
 */
class SymbolStack {
    private ArrayList<Token> stack;
    private String[] operators = "+ - * / ^ ( ) UnaryLeftP UnaryRightP Comp ? : & | Negative , VariableLeftP VariableRightP ! $".split(" ");

    /**
     * Default constructor
     */
    public SymbolStack() {
        stack = new ArrayList<Token>();
    }

    /**
     * Add the given token into stack.
     * 
     * @param token the token to be pushed
     */
    public void add(Token token) {
        stack.add(token);
    } 

    /**
     * Get the top operator in the stack.
     * Note that this operator doesn't need to be at the top,
     * but it should be at the top among all operators.
     * 
     * @return the top operator
     */
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

    /**
     * Get the size of this stack.
     * 
     * @return the size of this stack
     */
    public int size() {
        return stack.size();
    }

    /**
     * Auxiliar method used to print all symbols' value in the stack.
     */
    public void printValue() {
        for (Token token : stack) {
            System.out.print(token.getValue() + " ");
        }
        System.out.print("\n");
    }

    /**
     * Auxiliar method used to print all symbols' type in the stack.
     */
    public void printType() {
        for (Token token : stack) {
            System.out.print(token.getType() + " ");
        }
        System.out.print("\n");
    }

    /**
     * Pop the top of stack, return it.
     * 
     * @return the top token
     */
    public Token pop() {
        return stack.remove(stack.size()-1);
    }

    /**
     * Method used to implement overloading.
     * It will used the stack to decide what on earth the given
     * token's type.
     * 
     * @param token the token to be analyze
     * 
     * @throws MissingLeftParenthesisException if the expression miss left
     *         parenthesis
     * 
     * @return the token after converting
     */
    public Token convert(Token token) throws MissingLeftParenthesisException {
        token = analyzeParenthesis(token);
        token = analyzeDecimal(token);
        token = analyzeMinus(token);

        return token;
    }

    /**
     * Component of convert method used to analyze minus
     * 
     * @param token the token to be anlyzed
     * 
     * @return the token after converting
     */
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

    /**
     * Component of convert method used to convert decimal to ArithExpr
     * 
     * @param token the token to be converted
     * 
     * @return the token after converting
     */
    public Token analyzeDecimal(Token token) {
        String type = token.getType();

        if (type.equals("Decimal")) {
            return new Token(token.getValue(), "ArithExpr");
        }
        return token;
    }

    /**
     * Component of convert method used to analyze parenthesis
     * 
     * @param token the token to be analyzed
     * 
     * @throws MissingLeftParenthesisException if the expression miss a left
     *         parenthesis
     * 
     * @return the token after converting
     */
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

    /**
     * Get the top of the stack
     * 
     * @return the top token
     */
    public Token top() {
        return stack.get(stack.size()-1);
    }

    /**
     * Judge whether a token of the given type is in stack
     * 
     * @param type the expected type
     * 
     * @return true if the token of given type is in stack
     */
    public boolean hasType(String type) {
        for (Token t : stack) {
            if (type.equals(t.getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pop the given number of tokens, and return them.
     * 
     * @param num the number of tokens should be popped;
     * 
     * @return an ArrayList containing the tokens
     */
    public ArrayList<Token> remove(int num) {
        ArrayList<Token> tokens = new ArrayList<Token>();

        for (int i = 0; i < num; ++i) {
            tokens.add(stack.remove(stack.size()-1));
        }

        return tokens;
    }
}

/**
 * Class used to represent the grammer that the calculator is based on.
 * A grammer consists of productions.
 */
class Grammer {
    private ArrayList<Production> productions;
    private final int numProduction = 15;

    /**
     * Constructor to initialize the grammer by adding productions.
     * 
     * @throws Exception if the production can not be get
     */
    public Grammer() throws Exception {
        productions = new ArrayList<Production>();
        for (int i = 0; i < numProduction; ++i) {
            productions.add((Production) Class.forName("parser.production.Production"+String.valueOf(i)).newInstance());
        }
    }

    /**
     * Get the specific production indexed by index
     * 
     * @param index used to referred to the specific production
     * 
     * @return the production indexed by index
     */
    public Production get(int index) {
        return productions.get(index);
    }
}

/**
 * Operator precedence table.
 * Note that the entries in the table may not be what it exactly should be.
 * Some error detectations are implemented by filling some entries with '&lt;'
 * or '&gt;' so that during reducing the errors can be detected.
 */
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

    /**
     * Get the entry index by left and right.
     * 
     * @param left represents the line
     * @param right represents the column
     * 
     * @throws MissingRightParenthesisException if the expression miss a right
     *         parenthesis
     * @throws TrinaryOperationException if the expression has an error in
     *         using trinary operation
     * 
     * @return the content of the entry
     */
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

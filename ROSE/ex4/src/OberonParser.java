import java.util.*;
import java.io.*;
import java.util.regex.*;
import flowchart.*;
import exceptions.*;

public class OberonParser {
    private OberonScanner scanner;
    private Symbol lookahead;
    private Symbol previous;
    private String _start;
    private HashMap<String, Set<String>> _first;
    private HashMap<String, Set<String>> _follow;
    private HashMap<String, Set<String>> _grammar;
    private HashMap<String, String> global;
    private HashMap<String, String> local;
    private ArrayList<MyProcedure> _procedures;
    private ArrayList<ProcedureCall> _procedureCalls;
    private int numArgument;
    private MyProcedure _procedure;
    private Stack<Scope> scopeStack;
    private Module module;

    public OberonParser(String input, String grammar) throws Exception {
        scanner = new OberonScanner(new FileReader(input));
        lookahead = scanner.yylex();
        _start = null;
        _first = new HashMap<String, Set<String>>();
        _follow = new HashMap<String, Set<String>>();
        _grammar = new HashMap<>();
        _procedure = new MyProcedure("");
        _procedures = new ArrayList<>();
        _procedureCalls = new ArrayList<>();
        scopeStack = new Stack<>();
        setGrammar(grammar);
        calFollow();
    }

    public void parse() throws Exception {
        module();
        // for (MyProcedure p : _procedures) {
        //     System.out.println(p.getName() + ": " + String.valueOf(p.getTypes().size()));
        // }
        if (! check()) {
            throw new ParameterMismatchedException(getErrorMessage());
        }
        module.show();
    }

    private boolean check() {
        for (ProcedureCall c : _procedureCalls) {
            String name = c.getName();
            if (name.toLowerCase().equals("write")
                || name.toLowerCase().equals("writeln")
                || name.toLowerCase().equals("read")) {
                    continue;
            }
            if (getProcedure(c.getName()).getTypes().size() != c.getNum()) {
                return false;
            }
        }
        return true;
    }

    private MyProcedure getProcedure(String name) {
        for (MyProcedure p : _procedures) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    private String match(int expected) throws Exception {
        // System.out.println(lookahead);
        // System.out.println(lookahead.getValue());
        if (lookahead.getType() == expected) {
            String res = lookahead.getValue();
            if (res == "") {
                res = Type.terminalNames[lookahead.getType()];
            }
            previous = lookahead;
            lookahead = scanner.yylex();
            return res;
        } else if (isOperator(previous.getType())) {
            throw new MissingOperandException(getErrorMessage());
        } else if (expected == Type.LEFTP) {
            throw new MissingLeftParenthesisException(getErrorMessage());
        } else if (expected == Type.RIGHTP) {
            throw new MissingRightParenthesisException(getErrorMessage());
        } else if (previous.getType() == lookahead.getType() && lookahead.getType() == Type.IDENTIFIER) {
            throw new MissingOperatorException(getErrorMessage());
        } else {
            System.out.println(Type.terminalNames[expected]);
            throw new Exception(getErrorMessage());
        }
    }

    private boolean isOperator(int symbol) {
        return symbol == Type.PLUS
               || symbol == Type.MINUS
               || symbol == Type.TIMES
               || symbol == Type.EQ
               || symbol == Type.NE
               || symbol == Type.LT
               || symbol == Type.LE
               || symbol == Type.GT
               || symbol == Type.GE;
    }

    private String getErrorMessage() {
        return lookahead.getValue() + "    line: " + lookahead.getLine() + "    column: " + lookahead.getColumn();
    }

    private boolean isTerminal(String symbol) {
        return ! _grammar.containsKey(symbol);
    }

    private int countFollow() {
        int cnt = 0;
        for (Set<String> s : _follow.values()) {
            cnt += s.size();
        }
        return cnt;
    }

    private void calFollow() {
        HashSet<String> tmp = new HashSet<>();
        tmp.add("$");
        _follow.put(_start, tmp);

        while (true) {
            int cnt = countFollow();
            for (Map.Entry<String, Set<String>> e : _grammar.entrySet()) {
                for (String body : e.getValue()) {
                    String[] symbols = body.split(" ");
                    for (int i = 0; i < symbols.length; ++i) {
                        if (! isTerminal(symbols[i])) {
                            int j = i + 1;
                            Set<String> set = null;
                            if (_follow.containsKey(symbols[i])) {
                                set = _follow.get(symbols[i]);
                            } else {
                                set = new HashSet<>();
                            }
                            while (j < symbols.length) {
                                set.addAll(getFirst(symbols[j]));
                                if (! getFirst(symbols[j]).contains("")) {
                                    break;
                                }
                                j += 1;
                            }
                            if (j == symbols.length) {
                                if (_follow.containsKey(e.getKey())) {
                                    set.addAll(_follow.get(e.getKey()));
                                }
                            }
                            _follow.put(symbols[i], set);
                        }
                    }
                }
            }
            if (cnt == countFollow()) {
                break;
            }
        }
        for (Set<String> s : _follow.values()) {
            s.remove("");
        }
    }

    private Set getFollow(String symbol) {
        return _follow.get(symbol);
    }

    private Set getFirst(String symbol) {
        if (isTerminal(symbol)) {
            HashSet<String> tmp = new HashSet<>();
            tmp.add(symbol);
            return tmp;
        }

        if (_first.containsKey(symbol)) {
            return _first.get(symbol);
        }

        HashSet<String> tmp = new HashSet<>();
        if (_grammar.get(symbol).contains("")) {
            tmp.add("");
        }
        for (String body : _grammar.get(symbol)) {
            String[] bodySymbols = body.split(" ");
            int i = 0;
            for (i = 0; i < bodySymbols.length; ++i) {
                if (isTerminal(bodySymbols[i])) {
                    tmp.add(bodySymbols[i]);
                    break;
                } else {
                    tmp.addAll(getFirst(bodySymbols[i]));
                    tmp.remove("");
                    if (! getFirst(bodySymbols[i]).contains("")) {
                        break;
                    }
                }
            }
            if (i == bodySymbols.length) {
                tmp.add("");
            }
        }
        _first.put(symbol, tmp);
        return _first.get(symbol);
    }

    private void setGrammar(String input) throws Exception {
        FileInputStream fstream = new FileInputStream(input);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String rawProduction;
        while ((rawProduction = br.readLine()) != null)   {
            Pattern pattern = Pattern.compile("\\[\\d+\\]");
            Matcher match = pattern.matcher(rawProduction); 
            String production = match.replaceAll("").trim();
            String[] headAndBody = production.split("::=");
            String body = null;
            String head = headAndBody[0].trim();
            if (headAndBody.length == 2) {
                body = headAndBody[1].trim();
            } else {
                body = "";
            }

            if (_grammar.containsKey(head)) {
                _grammar.get(head).add(body);
            } else {
                HashSet<String> set = new HashSet<>();
                set.add(body);
                _grammar.put(head, set);
            }
            
            if (_start == null) {
                _start = head;
            }
        }
        br.close();
    }

    private String module() throws Exception {
        String res = "";
        match(Type.MODULE);
        res += match(Type.IDENTIFIER);
        module = new Module(res);
        res += match(Type.SEMI);
        res += declarations();
        res += begin();
        res += match(Type.END);
        res += match(Type.IDENTIFIER);
        res += match(Type.DOT);
    
        return res;
    }
    
    private String begin() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.BEGIN) {
            res += match(Type.BEGIN);
            res += statement_sequence();
        }
        return res;
    }
    
    private String declarations() throws Exception {
        String res = "";
        res += const_declaration();
        res += type_declaration();
        res += var_declaration();
        res += procedure_declarations();
    
        return res;
    }
    
    private String const_declaration() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.CONST) {
            res += match(Type.CONST);
            res += const_expression();
            res += match(Type.SEMI);
        }
        return res;
    }
    
    private String const_expression_prefix() throws Exception {
        String res = "";
        res += match(Type.IDENTIFIER);
        res += match(Type.EQ);
        res += expression();
    
        return res;
    }
    
    private String const_expression_suffix() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.SEMI) {
            res += match(Type.SEMI);
            res += const_expression();
        }
        return res;
    }
    
    private String const_expression() throws Exception {
        String res = "";
        res += const_expression_prefix();
        res += const_expression_suffix();
    
        return res;
    }
    
    private String type_declaration() throws Exception {
        String res = "";
        if (getFirst("type").contains(Type.terminalNames[lookahead.getType()])) {
            res += type();
            res += type_expression();
        }
        return res;
    }
    
    private String type_expression_prefix() throws Exception {
        String res = "";
        res += match(Type.IDENTIFIER);
        res += match(Type.EQ);
        res += type();
        res += match(Type.SEMI);
    
        return res;
    }
    
    private String type_expression_suffix() throws Exception {
        String res = "";
        if (getFirst("type_expression").contains(Type.terminalNames[lookahead.getType()])) {
            res += type_expression();
        }
        return res;
    }
    
    private String type_expression() throws Exception {
        String res = "";
        res += type_expression_prefix();
        res += type_expression_suffix();
    
        return res;
    }
    
    private String var_declaration() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.VAR) {
            res += match(Type.VAR);
            res += var_expression();
        }
        return res;
    }
    
    private String var_expression_suffix() throws Exception {
        String res = "";
        res += match(Type.COLON);
        res += type();
        res += match(Type.SEMI);
    
        return res;
    }
    
    private String var_expression_prefix() throws Exception {
        String res = "";
        if (getFirst("var_expression").contains(Type.terminalNames[lookahead.getType()])) {
            res += var_expression();
        }
        return res;
    }
    
    private String var_expression() throws Exception {
        String res = "";
        res += identifier_list();
        res += var_expression_prefix();
        res += var_expression_suffix();
    
        return res;
    }
    
    private String procedure_declarations() throws Exception {
        String res = "";
        if (getFirst("procedure_declaration").contains(Type.terminalNames[lookahead.getType()])) {
            res += procedure_declaration();
            res += match(Type.SEMI);
            res += procedure_declarations();
        }
        return res;
    }
    
    private String procedure_declaration() throws Exception {
        String res = "";
        res += procedure_heading();
        scopeStack.push(new Scope(module.add(res)));
        res += match(Type.SEMI);
        res += procedure_body();
    
        return res;
    }
    
    private String procedure_body() throws Exception {
        String res = "";
        res += declarations();
        res += begin();
        res += match(Type.END);
        res += match(Type.IDENTIFIER);
    
        return res;
    }
    
    private String procedure_heading() throws Exception {
        String res = "";
        match(Type.PROCEDURE);
        if (lookahead.getType() == Type.IDENTIFIER) {
            _procedure = new MyProcedure(lookahead.getValue());
            res += match(Type.IDENTIFIER);
        }
        formal_parameters();
        _procedures.add(_procedure);
        _procedure = null;
        return res;
    }
    
    private String formal_parameters() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.LEFTP) {
            res += match(Type.LEFTP);
            res += fp_sections();
            res += match(Type.RIGHTP);
        } else if (lookahead.getType() != Type.SEMI) {
            throw new MissingLeftParenthesisException(getErrorMessage());
        }
        return res;
    }
    
    private String fp_sections() throws Exception {
        String res = "";
        if (getFirst("fp_section").contains(Type.terminalNames[lookahead.getType()])) {
            res += fp_section();
            res += fp_sections_suffix();
        }
        return res;
    }
    
    private String fp_sections_suffix() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.SEMI) {
            res += match(Type.SEMI);
            res += fp_sections();
        }
        return res;
    }
    
    private String fp_section() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.VAR) {
            res += match(Type.VAR);
        }
        res += identifier_list();
        res += match(Type.COLON);
        String tmp = type();
        ArrayList<String> l = _procedure.getTypes();
        for (int i = 0; i < l.size(); ++i) {
            if (l.get(i) == "") {
                l.set(i, tmp);
            }
        }
        return res + tmp;
    }
    
    private String type() throws Exception {
        String res = "";
        if (getFirst("array_type").contains(Type.terminalNames[lookahead.getType()])) {
            res += array_type();
        } else if (lookahead.getType() == Type.IDENTIFIER) {
            res += match(Type.IDENTIFIER);
        } else {
            res += record_type();
        }
        return res;
    }
    
    private String record_type() throws Exception {
        String res = "";
        res += match(Type.RECORD);
        res += field_list();
        res += field_list_with_semi();
        res += match(Type.END);
    
        return res;
    }
    
    private String field_list() throws Exception {
        String res = "";
        if (getFirst("identifier_list").contains(Type.terminalNames[lookahead.getType()])) {
            res += identifier_list();
            res += match(Type.COLON);
            res += type();
        }
        return res;
    }
    
    private String field_list_with_semi() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.SEMI) {
            res += match(Type.SEMI);
            res += field_list();
            res += field_list_with_semi();
        }
        return res;
    }
    
    private String array_type() throws Exception {
        String res = "";
        res += match(Type.ARRAY);
        res += expression();
        res += match(Type.OF);
        res += type();
    
        return res;
    }
    
    private String identifier_list() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.IDENTIFIER && _procedure != null) {
            _procedure.getTypes().add("");
        }
        res += match(Type.IDENTIFIER);
        res += identifier_list_with();
    
        return res;
    }
    
    private String identifier_list_with() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.COMMA) {
            res += match(Type.COMMA);
            if (lookahead.getType() == Type.IDENTIFIER && _procedure != null) {
                _procedure.getTypes().add("");
            }
            res += match(Type.IDENTIFIER);
            res += identifier_list_with();
        }
        return res;
    }
    
    private String statement_sequence() throws Exception {
        String res = "";
        res += statement();
        res += statement_with_semi();
        
        return res;
    }
    
    private String statement_with_semi() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.SEMI) {
            match(Type.SEMI);
            res += statement() + "\n";
            res += statement_with_semi();
        }
        return res;
    }
    
    private String statement() throws Exception {
        String res = "";
        if (getFirst("if_statement").contains(Type.terminalNames[lookahead.getType()])) {
            res += if_statement();
        } else if (getFirst("while_statement").contains(Type.terminalNames[lookahead.getType()])) {
            res += while_statement();
        } else if (lookahead.getType() == Type.IDENTIFIER) {
            res += match(Type.IDENTIFIER);
            res += statement_suffix();
            scopeStack.peek().add(new PrimitiveStatement(res));
        }
        return res;
    }
    
    private String statement_suffix() throws Exception {
        String res = "";
        if (getFirst("procedure_call").contains(Type.terminalNames[lookahead.getType()])) {
            res += procedure_call();
        } else {
            res += assignment();
        }
        return res;
    }
    
    private String while_statement() throws Exception {
        String res = "";
        match(Type.WHILE);
        res += expression();
        WhileStatement whileSmt = new WhileStatement(res);
        scopeStack.peek().add(whileSmt);
        match(Type.DO);
        scopeStack.push(new Scope(whileSmt.getLoopBody()));
        statement_sequence();
        match(Type.END);
        scopeStack.pop();
        return res;
    }
    
    private String if_statement() throws Exception {
        String res = "";
        res += match(Type.IF);
        IfStatement ifSmt = new IfStatement(expression());
        scopeStack.peek().add(ifSmt);
        scopeStack.push(new Scope(ifSmt.getFalseBody()));
        scopeStack.push(new Scope(ifSmt.getTrueBody()));
        res += match(Type.THEN);
        res += statement_sequence();
        scopeStack.pop();
        res += elsif_statement();
        res += else_statement();
        res += match(Type.END);
    
        return res;
    }
    
    private String elsif_statement() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.ELSIF) {
            res += match(Type.ELSIF);
            IfStatement ifSmt = new IfStatement(expression());
            scopeStack.peek().add(ifSmt);
            scopeStack.push(new Scope(ifSmt.getTrueBody()));
            res += match(Type.THEN);
            res += statement_sequence();
            scopeStack.pop();
            scopeStack.pop();
            scopeStack.push(new Scope(ifSmt.getFalseBody()));
            res += elsif_statement();
        }
        return res;
    }
    
    private String else_statement() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.ELSE) {
            res += match(Type.ELSE);
            res += statement_sequence();
        }
        scopeStack.pop();
        return res;
    }
    
    private String procedure_call() throws Exception {
        String res = "";
        String name = previous.getValue();
        if (getFirst("actual_parameters").contains(Type.terminalNames[lookahead.getType()])) {
            numArgument = 0;
            res += actual_parameters();
        }
        // System.out.println(numArgument);
        _procedureCalls.add(new ProcedureCall(name, numArgument));
        return res;
    }
    
    private String actual_parameters() throws Exception {
        String res = "";
        res += match(Type.LEFTP);
        res += expression_list();
        res += match(Type.RIGHTP);
        return res;
    }
    
    private String assignment() throws Exception {
        String res = "";
        res += selector();
        res += " " + match(Type.ASSIGN) + " ";
        res += expression();
    
        return res;
    }
    
    private String expression() throws Exception {
        String res = "";
        res += simple_expression();
        res += comp_expression();
    
        return res;
    }
    
    private String expression_list() throws Exception {
        String res = "";
        if (getFirst("expression").contains(Type.terminalNames[lookahead.getType()])) {
            res += expression();
            if (! res.equals("")) {
                numArgument++;
            }
            res += expression_list_with();
        }
        return res;
    }
    
    private String expression_list_with() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.COMMA) {
            res += match(Type.COMMA);
            String tmp = expression();
            if (! tmp.equals("")) {
                ++numArgument;
            }
            res += tmp;
            res += expression_list_with();
        }
        return res;
    }
    
    private String comp() throws Exception {
        String res = " ";
        if (lookahead.getType() == Type.LE) {
            res += match(Type.LE);
        } else if (lookahead.getType() == Type.LT) {
            res += match(Type.LT);
        } else if (lookahead.getType() == Type.EQ) {
            res += match(Type.EQ);
        } else if (lookahead.getType() == Type.GE) {
            res += match(Type.GE);
        } else if (lookahead.getType() == Type.GT) {
            res += match(Type.GT);
        } else {
            res += match(Type.NE);
        }
        return res + " ";
    }
    
    private String comp_expression() throws Exception {
        String res = "";
        if (getFirst("comp").contains(Type.terminalNames[lookahead.getType()])) {
            res += comp();
            res += simple_expression();
        }
        return res;
    }
    
    private String unary() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.PLUS) {
            res += match(Type.PLUS);
        } else if (lookahead.getType() == Type.MINUS) {
            res += match(Type.MINUS);
        }
        return res;
    }
    
    private String binary_low() throws Exception {
        String res = " ";
        if (lookahead.getType() == Type.OR) {
            res += match(Type.OR);
        } else if (lookahead.getType() == Type.PLUS) {
            res += match(Type.PLUS);
        } else {
            res += match(Type.MINUS);
        }
        return res + " ";
    }
    
    private String binary_mid() throws Exception {
        String res = " ";
        if (lookahead.getType() == Type.TIMES) {
            res += match(Type.TIMES);
        } else if (lookahead.getType() == Type.DIV) {
            res += match(Type.DIV);
        } else if (lookahead.getType() == Type.MOD) {
            res += match(Type.MOD);
        } else {
            res += match(Type.AND);
        }
        return res + " ";
    }
    
    private String simple_expression() throws Exception {
        String res = "";
        res += unary();
        res += term();
        res += term_list_with();
        return res;
    }
    
    private String term_list_with() throws Exception {
        String res = "";
        if (getFirst("binary_low").contains(Type.terminalNames[lookahead.getType()])) {
            res += binary_low();
            res += term();
            res += term_list_with();
        }
        return res;
    }
    
    private String term() throws Exception {
        String res = "";
        res += factor();
        res += term_suffix();
    
        return res;
    }
    
    private String term_suffix() throws Exception {
        String res = "";
        if (getFirst("binary_mid").contains(Type.terminalNames[lookahead.getType()])) {
            res += binary_mid();
            res += factor();
        }
        return res;
    }
    
    private String factor() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.NOT) {
            res += match(Type.NOT);
            res += factor();
        } else if (getFirst("number").contains(Type.terminalNames[lookahead.getType()])) {
            res += number();
        } else if (lookahead.getType() == Type.IDENTIFIER) {
            res += match(Type.IDENTIFIER);
            res += selector();
        } else {
            res += match(Type.LEFTP);
            res += expression();
            res += match(Type.RIGHTP);
        }
        return res;
    }
    
    private String number() throws Exception {
        String res = "";
        res += match(Type.INTEGER);
    
        return res;
    }
    
    private String selector() throws Exception {
        String res = "";
        if (lookahead.getType() == Type.LEFTB) {
            res += match(Type.LEFTB);
            res += expression();
            res += match(Type.RIGHTB);
            res += selector();
        } else if (lookahead.getType() == Type.DOT) {
            res += match(Type.DOT);
            res += match(Type.IDENTIFIER);
            res += selector();
        }
        return res;
    }
    
    public static void main(String[] argv) throws Exception {
        OberonParser parser = new OberonParser(argv[0], "../src/grammar");
        parser.parse();
    }
}

class Scope {
    Procedure procedure;
    StatementSequence smtSequence;
    
    public Scope(Procedure p) {
        procedure = p;
    }

    public Scope(StatementSequence s) {
        smtSequence = s;
    }

    public void add(AbstractStatement p) {
        if (procedure != null) {
            procedure.add(p);
        } else {
            smtSequence.add(p);
        }
    }
}

class MyProcedure {
    private String name;
    private ArrayList<String> types;

    public MyProcedure(String n) {
        name = n;
        types = new ArrayList<>();
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> t) {
        types = t;
    }

    public String getName() {
        return name;
    }
}

class ProcedureCall {
    private String name;
    private int num;

    public ProcedureCall(String n, int nu) {
        name = n;
        num = nu;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }
}
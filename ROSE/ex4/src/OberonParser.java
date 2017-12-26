import java.util.*;
import java.io.*;
import java.util.regex.*;
import flowchart.*;



import com.sun.j3d.utils.scenegraph.io.state.com.sun.j3d.utils.geometry.PrimitiveState;

public class OberonParser {
    private OberonScanner scanner;
    private Symbol lookahead;
    private String _start;
    private HashMap<String, Set<String>> _first;
    private HashMap<String, Set<String>> _follow;
    private HashMap<String, Set<String>> _grammar;
    private Stack<Scope> scopeStack;
    private Module module;

    public OberonParser(String input, String grammar) throws Exception {
        scanner = new OberonScanner(new FileReader(input));
        lookahead = scanner.yylex();
        _start = null;
        _first = new HashMap<String, Set<String>>();
        _follow = new HashMap<String, Set<String>>();
        _grammar = new HashMap<>();
        scopeStack = new Stack<>();
        setGrammar(grammar);
        calFollow();
    }

    public void parse() throws Exception {
        module();
        module.show();
    }

    private String match(int expected) throws Exception {
        // System.out.println(lookahead);
        if (lookahead.getType() == expected) {
            String res = lookahead.getValue();
            if (res == "") {
                res = sym.terminalNames[lookahead.getType()];
            }
            lookahead = scanner.yylex();
            return res;
        } else {
            System.out.print("Expected: ");
            System.out.println(sym.terminalNames[expected]);
            System.out.print("Lookahead: ");
            System.out.println(sym.terminalNames[lookahead.getType()]);
            System.out.println(lookahead);
            throw new Exception();
        }
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
        match(sym.MODULE);
        res += match(sym.IDENTIFIER);
        module = new Module(res);
        res += match(sym.SEMI);
        res += declarations();
        res += begin();
        res += match(sym.END);
        res += match(sym.IDENTIFIER);
        res += match(sym.DOT);
    
        return res;
    }
    
    private String begin() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.BEGIN) {
            res += match(sym.BEGIN);
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
        if (lookahead.getType() == sym.CONST) {
            res += match(sym.CONST);
            res += const_expression();
            res += match(sym.SEMI);
        }
        return res;
    }
    
    private String const_expression_prefix() throws Exception {
        String res = "";
        res += match(sym.IDENTIFIER);
        res += match(sym.EQ);
        res += expression();
    
        return res;
    }
    
    private String const_expression_suffix() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.SEMI) {
            res += match(sym.SEMI);
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
        if (getFirst("type").contains(sym.terminalNames[lookahead.getType()])) {
            res += type();
            res += type_expression();
        }
        return res;
    }
    
    private String type_expression_prefix() throws Exception {
        String res = "";
        res += match(sym.IDENTIFIER);
        res += match(sym.EQ);
        res += type();
        res += match(sym.SEMI);
    
        return res;
    }
    
    private String type_expression_suffix() throws Exception {
        String res = "";
        if (getFirst("type_expression").contains(sym.terminalNames[lookahead.getType()])) {
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
        if (lookahead.getType() == sym.VAR) {
            res += match(sym.VAR);
            res += var_expression();
        }
        return res;
    }
    
    private String var_expression_suffix() throws Exception {
        String res = "";
        res += match(sym.COLON);
        res += type();
        res += match(sym.SEMI);
    
        return res;
    }
    
    private String var_expression_prefix() throws Exception {
        String res = "";
        if (getFirst("var_expression").contains(sym.terminalNames[lookahead.getType()])) {
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
        if (getFirst("procedure_declaration").contains(sym.terminalNames[lookahead.getType()])) {
            res += procedure_declaration();
            res += match(sym.SEMI);
            res += procedure_declarations();
        }
        return res;
    }
    
    private String procedure_declaration() throws Exception {
        String res = "";
        res += procedure_heading();
        scopeStack.push(new Scope(module.add(res)));
        res += match(sym.SEMI);
        res += procedure_body();
    
        return res;
    }
    
    private String procedure_body() throws Exception {
        String res = "";
        res += declarations();
        res += begin();
        res += match(sym.END);
        res += match(sym.IDENTIFIER);
    
        return res;
    }
    
    private String procedure_heading() throws Exception {
        String res = "";
        match(sym.PROCEDURE);
        res += match(sym.IDENTIFIER);
        formal_parameters();
    
        return res;
    }
    
    private String formal_parameters() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.LEFTP) {
            res += match(sym.LEFTP);
            res += fp_sections();
            res += match(sym.RIGHTP);
        }
        return res;
    }
    
    private String fp_sections() throws Exception {
        String res = "";
        if (getFirst("fp_section").contains(sym.terminalNames[lookahead.getType()])) {
            res += fp_section();
            res += fp_sections_suffix();
        }
        return res;
    }
    
    private String fp_sections_suffix() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.SEMI) {
            res += match(sym.SEMI);
            res += fp_sections();
        }
        return res;
    }
    
    private String fp_section() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.VAR) {
            res += match(sym.VAR);
            res += identifier_list();
            res += match(sym.COLON);
            res += type();
        } else {
            res += identifier_list();
            res += match(sym.COLON);
            res += type();
        }
        return res;
    }
    
    private String type() throws Exception {
        String res = "";
        if (getFirst("array_type").contains(sym.terminalNames[lookahead.getType()])) {
            res += array_type();
        } else if (lookahead.getType() == sym.IDENTIFIER) {
            res += match(sym.IDENTIFIER);
        } else {
            res += record_type();
        }
        return res;
    }
    
    private String record_type() throws Exception {
        String res = "";
        res += match(sym.RECORD);
        res += field_list();
        res += field_list_with_semi();
        res += match(sym.END);
    
        return res;
    }
    
    private String field_list() throws Exception {
        String res = "";
        if (getFirst("identifier_list").contains(sym.terminalNames[lookahead.getType()])) {
            res += identifier_list();
            res += match(sym.COLON);
            res += type();
        }
        return res;
    }
    
    private String field_list_with_semi() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.SEMI) {
            res += match(sym.SEMI);
            res += field_list();
            res += field_list_with_semi();
        }
        return res;
    }
    
    private String array_type() throws Exception {
        String res = "";
        res += match(sym.ARRAY);
        res += expression();
        res += match(sym.OF);
        res += type();
    
        return res;
    }
    
    private String identifier_list() throws Exception {
        String res = "";
        res += match(sym.IDENTIFIER);
        res += identifier_list_with();
    
        return res;
    }
    
    private String identifier_list_with() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.COMMA) {
            res += match(sym.COMMA);
            res += match(sym.IDENTIFIER);
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
        if (lookahead.getType() == sym.SEMI) {
            match(sym.SEMI);
            res += statement() + "\n";
            res += statement_with_semi();
        }
        return res;
    }
    
    private String statement() throws Exception {
        String res = "";
        if (getFirst("if_statement").contains(sym.terminalNames[lookahead.getType()])) {
            res += if_statement();
        } else if (getFirst("while_statement").contains(sym.terminalNames[lookahead.getType()])) {
            res += while_statement();
        } else if (lookahead.getType() == sym.IDENTIFIER) {
            res += match(sym.IDENTIFIER);
            res += statement_suffix();
            scopeStack.peek().add(new PrimitiveStatement(res));
        }
        return res;
    }
    
    private String statement_suffix() throws Exception {
        String res = "";
        if (getFirst("procedure_call").contains(sym.terminalNames[lookahead.getType()])) {
            res += procedure_call();
        } else {
            res += assignment();
        }
        return res;
    }
    
    private String while_statement() throws Exception {
        String res = "";
        match(sym.WHILE);
        res += expression();
        WhileStatement whileSmt = new WhileStatement(res);
        scopeStack.peek().add(whileSmt);
        match(sym.DO);
        scopeStack.push(new Scope(whileSmt.getLoopBody()));
        statement_sequence();
        match(sym.END);
        scopeStack.pop();
        return res;
    }
    
    private String if_statement() throws Exception {
        String res = "";
        res += match(sym.IF);
        expression();
        res += match(sym.THEN);
        res += statement_sequence();
        res += elsif_statement();
        res += else_statement();
        res += match(sym.END);
    
        return res;
    }
    
    private String elsif_statement() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.ELSIF) {
            res += match(sym.ELSIF);
            res += expression();
            res += match(sym.THEN);
            res += statement_sequence();
            res += elsif_statement();
        }
        return res;
    }
    
    private String else_statement() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.ELSE) {
            res += match(sym.ELSE);
            res += statement_sequence();
        }
        return res;
    }
    
    private String procedure_call() throws Exception {
        String res = "";
        if (getFirst("actual_parameters").contains(sym.terminalNames[lookahead.getType()])) {
            res += actual_parameters();
        }
        return res;
    }
    
    private String actual_parameters() throws Exception {
        String res = "";
        res += match(sym.LEFTP);
        res += expression_list();
        res += match(sym.RIGHTP);
    
        return res;
    }
    
    private String assignment() throws Exception {
        String res = "";
        res += selector();
        res += " " + match(sym.ASSIGN) + " ";
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
        if (getFirst("expression").contains(sym.terminalNames[lookahead.getType()])) {
            res += expression();
            res += expression_list_with();
        }
        return res;
    }
    
    private String expression_list_with() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.COMMA) {
            res += match(sym.COMMA);
            res += expression();
            res += expression_list_with();
        }
        return res;
    }
    
    private String comp() throws Exception {
        String res = " ";
        if (lookahead.getType() == sym.LE) {
            res += match(sym.LE);
        } else if (lookahead.getType() == sym.LT) {
            res += match(sym.LT);
        } else if (lookahead.getType() == sym.EQ) {
            res += match(sym.EQ);
        } else if (lookahead.getType() == sym.GE) {
            res += match(sym.GE);
        } else if (lookahead.getType() == sym.GT) {
            res += match(sym.GT);
        } else {
            res += match(sym.NE);
        }
        return res + " ";
    }
    
    private String comp_expression() throws Exception {
        String res = "";
        if (getFirst("comp").contains(sym.terminalNames[lookahead.getType()])) {
            res += comp();
            res += simple_expression();
        }
        return res;
    }
    
    private String unary() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.PLUS) {
            res += match(sym.PLUS);
        } else if (lookahead.getType() == sym.MINUS) {
            res += match(sym.MINUS);
        }
        return res;
    }
    
    private String binary_low() throws Exception {
        String res = " ";
        if (lookahead.getType() == sym.OR) {
            res += match(sym.OR);
        } else if (lookahead.getType() == sym.PLUS) {
            res += match(sym.PLUS);
        } else {
            res += match(sym.MINUS);
        }
        return res + " ";
    }
    
    private String binary_mid() throws Exception {
        String res = " ";
        if (lookahead.getType() == sym.TIMES) {
            res += match(sym.TIMES);
        } else if (lookahead.getType() == sym.DIV) {
            res += match(sym.DIV);
        } else if (lookahead.getType() == sym.MOD) {
            res += match(sym.MOD);
        } else {
            res += match(sym.AND);
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
        if (getFirst("binary_low").contains(sym.terminalNames[lookahead.getType()])) {
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
        if (getFirst("binary_mid").contains(sym.terminalNames[lookahead.getType()])) {
            res += binary_mid();
            res += factor();
        }
        return res;
    }
    
    private String factor() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.NOT) {
            res += match(sym.NOT);
            res += factor();
        } else if (getFirst("number").contains(sym.terminalNames[lookahead.getType()])) {
            res += number();
        } else if (lookahead.getType() == sym.IDENTIFIER) {
            res += match(sym.IDENTIFIER);
            res += selector();
        } else {
            res += match(sym.LEFTP);
            res += expression();
            res += match(sym.RIGHTP);
        }
        return res;
    }
    
    private String number() throws Exception {
        String res = "";
        res += match(sym.INTEGER);
    
        return res;
    }
    
    private String selector() throws Exception {
        String res = "";
        if (lookahead.getType() == sym.LEFTB) {
            res += match(sym.LEFTB);
            res += expression();
            res += match(sym.RIGHTB);
            res += selector();
        } else if (lookahead.getType() == sym.DOT) {
            res += match(sym.DOT);
            res += match(sym.IDENTIFIER);
            res += selector();
        }
        return res;
    }
    
    public static void main(String[] argv) throws Exception {
        OberonParser parser = new OberonParser("../testcases/case1.obr", "../src/grammar");
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
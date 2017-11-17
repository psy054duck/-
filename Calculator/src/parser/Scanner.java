package parser;

import java.util.ArrayList;

import exceptions.*;
import parser.token.*;
import tools.*;

/**
 * Instance of this class will convert the input of character stream
 * into token stream. And Parser will invoke the getNextToken method
 * to get the next token.
 * Note that this scanner won't convert the whole character stream
 * as soon as it receives the expression when it's instantiated.
 * Instead, it will recognize only one token when its getNextToken
 * method is invoked, and remain the rest of the charactor stream
 * in its buffer.
 */
class Scanner {
	private String buffer;
	private int lookahead;
	private int tokenBegin;

	/**
	 * Constructor with the expression it should parse.
	 * 
	 * @param expression the expression to be calculated
	 */
	public Scanner(String expression) {
		buffer = expression;
		lookahead = 0;
	}

	/**
	 * The most important method provided by Scanner.
	 * Scanner will convert the expression into token stream,
	 * and then Parser will use it to get the next token of
	 * the token stream.
	 * This method use a series of sub-procedures to reconize
	 * tokens. Each procedure can be viewed as a DFA and
	 * By adding a start state and using epsilon transitions
	 * to connect it to the start states of those DFAs.
	 * So the getNextToken method can be viewed as a NFA.
	 * 
	 * @throws LexicalException if any lexical error occurs
	 * 
	 * @return the next token
	 */
	public Token getNextToken() throws LexicalException {
		while (lookahead < buffer.length()
		      && buffer.charAt(lookahead) == ' ') {
				  ++lookahead;
		}
		if (lookahead >= buffer.length()) {
			return new Token("$", "$");
		}
		Token token = bool();
		if (token.getType() != "Error") {
			return token;
		}
		token = question();
		if (token.getType() != "Error") {
			return token;
		}
		token = colon();
		if (token.getType() != "Error") {
			return token;
		}
		token = binaryOp();
		if (token.getType() != "Error") {
			return token;
		}
		token = minus();
		if (token.getType() != "Error") {
			return token;
		}
		token = comma();
		if (token.getType() != "Error") {
			return token;
		}
		token = exclamation();
		if (token.getType() != "Error") {
			return token;
		}
		token = boolOp();
		if (token.getType() != "Error") {
			return token;
		}
		token = comp();
		if (token.getType() != "Error") {
			return token;
		}
		token = decimal();
		if (token.getType() != "Error") {
			return token;
		}
		token = leftParenthesis();
		if (token.getType() != "Error") {
			return token;
		}
		token = rightParenthesis();
		if (token.getType() != "Error") {
			return token;
		}
		token = identifier();
		return token;
	}

	/**
	 * DFA used to recognize left parenthesis.
	 * 
	 * @return Error token if the next token is not left
	 *         parenthesis
	 */
	private Token leftParenthesis() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		if (buffer.charAt(lookahead) == '(') {
			++lookahead;
			return new Token("(", "(");
		}

		return new Token("Error", "Error");
	}

	/**
	 * DFA used to recognize right parenthesis.
	 * 
	 * @return Error token if the next token is not right
	 *         parenthesis
	 */
	private Token rightParenthesis() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		if (buffer.charAt(lookahead) == ')') {
			++lookahead;
			return new Token(")", ")");
		}

		return new Token("Error", "Error");
	}

	/**
	 * DFA used to recognize identifier.
	 * 
	 * @throws IllegalSymbolException if this scanner encounter
	 *         an illegal symbol.
	 * @throws IllegalIdentifierException if this scanner recognizes
	 *         an illegal identifier.
	 * @return the corresponding token.
	 */
	private Token identifier() throws IllegalSymbolException,
	                                  IllegalIdentifierException {
		int tokenBegin = lookahead;

		if (lookahead < buffer.length()
		   && ! isValid(buffer.charAt(lookahead))) {
			   throw new IllegalSymbolException();
		   }

		while (lookahead < buffer.length()
		       && Character.isLetter(buffer.charAt(lookahead))) {
			++lookahead;
		}

		String lexeme = buffer.substring(tokenBegin, lookahead);
		if (lexeme.equals("sin") || lexeme.equals("cos")) {
			return new Token(lexeme, "UnaryFunc");
		} else if (lexeme.equals("max") || lexeme.equals("min")) {
			return new Token(lexeme, "VariableFunc");
		} else {
			throw new IllegalIdentifierException();
		}
	}

	/**
	 * DFA used to recognize bool literal.
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         a bool literal
	 */
	private Token bool() {
		if (lookahead + 4 >= buffer.length()) {
			return new Token("Error", "Error");
		}

		int init = lookahead;
		String lexeme = buffer.substring(lookahead, lookahead + 4).toLowerCase();
		if (lexeme.equals("true")
		    && (lookahead+4 == buffer.length() || ' ' == buffer.charAt(lookahead+4))) {
			lookahead += 4;
			return new Token("true", "BoolExpr");
		}

		lexeme = buffer.substring(lookahead, lookahead + 5).toLowerCase();
		if (lexeme.equals("false")
		    && (lookahead+5 == buffer.length() || ' ' == buffer.charAt(lookahead+5))) {
			lookahead += 5;
			return new Token("false", "BoolExpr");
		}
		lookahead = init;
		return new Token("Error", "Error");
	}

	/**
	 * DFA used to recognize a question mark.
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         a question mark
	 */
	private Token question() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		if (buffer.charAt(lookahead) == '?') {
			++lookahead;
			return new Token("?", "?");
		} else {
			return new Token("Error", "Error");
		}
	}

	/**
	 * DFA used to recognize a colon mark.
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         a colon mark
	 */
	private Token colon() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		if (buffer.charAt(lookahead) == ':') {
			++lookahead;
			return new Token(":", ":");
		} else {
			return new Token("Error", "Error");
		}
	}

	/**
	 * DFA used to recognize binary arithmetic operators
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         binary arithmetic operators
	 */
	private Token binaryOp() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		char next = buffer.charAt(lookahead);
		if (next == '+' || next == '/' || next == '^' || next == '*') {
			++lookahead;
			return new Token(""+next, ""+next);
		} else {
			return new Token("Error", "Error");
		}
	}

	/**
	 * DFA used to recognize minus mark.
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         a minus mark
	 */
	private Token minus() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		if (buffer.charAt(lookahead) == '-') {
			++lookahead;
			return new Token("-", "-");
		} else {
			return new Token("Error", "Error");
		}
	}

	/**
	 * DFA used to recognize comma mark.
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         a comma mark
	 */
	private Token comma() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		if (buffer.charAt(lookahead) == ',') {
			++lookahead;
			return new Token(",", ",");
		}
		return new Token("Error", "Error");
	}

	/**
	 * DFA used to recognize exclamation mark.
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         an exclamation mark
	 */
	private Token exclamation() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		if (buffer.charAt(lookahead) == '!') {
			++lookahead;
			return new Token("!", "!");
		}

		return new Token("Error", "Error");
	}

	/**
	 * DFA used to recognize bool operators.
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         a bool operator
	 */
	private Token boolOp() {
		if (lookahead >= buffer.length()) {
			return new Token("Error", "Error");
		}

		char ch = buffer.charAt(lookahead);
		if (ch == '&' || ch == '|') {
			++lookahead;
			return new Token(""+ch, ""+ch);
		} else {
			return new Token("Error", "Error");
		}
	}

	/**
	 * DFA used to recognize comparison operator.
	 * 
	 * @return Error token if this scanner fails to recognize
	 *         a comparison operator
	 */
	private Token comp() {
		int tokenBegin = lookahead;
		int state = 0;

		while (true) {
			char ch = 0;
			if (lookahead < buffer.length()) {
				ch = buffer.charAt(lookahead);
			}

			if (state == 0) {
				if (ch == '<') state = 1;
				else if (ch == '=') state = 4;
				else if (ch == '>') state = 5;
				else state = 8;
				++lookahead;
			} else if (state == 1) {
				if (ch == '=' || ch == '>') state = 2;
				else state = 3;
				++lookahead;
			} else if (state == 2) {
				break;
			} else if (state == 3) {
				--lookahead;
				break;
			} else if (state == 4) {
				break;
			} else if (state == 5) {
				if (ch == '=') state = 6;
				else state = 7;
				++lookahead;
			} else if (state == 6) {
				break;
			} else if (state == 7) {
				--lookahead;
				break;
			} else if (state == 8) {
				lookahead = tokenBegin;
				return new Token("Error", "Error");
			}
		}

		return new Token(buffer.substring(tokenBegin, lookahead), "Comp");
	}

	/**
	 * DFA used to recognize decimal literal.
	 * 
	 * @throws IllegalDecimalException if this scanner fails to
	 *         recognize decimal literal
	 * @return Decimal token if this scanner succeed to recognize
	 *         a decimal token
	 */
	private Token decimal() throws IllegalDecimalException {
		tokenBegin = lookahead;
		int state = 0;

		char ch = buffer.charAt(lookahead);
		if (ch == '.' || ch == 'E' || ch == 'e') {
			throw new IllegalDecimalException();
		}
		while (true) {
            ch = 0;
            if (lookahead < buffer.length()) {
                ch = buffer.charAt(lookahead);
			}
			if (state == 0) {
				if (Character.isDigit(ch)) {
					state = 1;
				} else {
					return new Token("Error", "Error");
				}
			} else if (state == 1) {
				if (ch == '.') {
					state = 2;
				} else if (ch == 'e' || ch == 'E') {
					state = 4;
				} else if (Character.isDigit(ch)) {
					state = 1;
				} else {
					state = 7;
				}
			} else if (state == 2) {
				if (Character.isDigit(ch)) {
					state = 3;
				} else {
                    state = 8;
				}
			} else if (state == 3) {
				if (ch == 'e' || ch == 'E') {
					state = 4;
				} else if (Character.isDigit(ch)) {
					state = 3;
				} else if (ch == '.') {
					state = 8;
				} else {
                    state = 7;
                }
			} else if (state == 4) {
				if (ch == '+' || ch == '-') {
					state = 5;
				} else if (Character.isDigit(ch)) {
					state = 6;
				} else {
                    state = 8;
				}
			} else if (state == 5) {
				if (Character.isDigit(ch)) {
					state = 6;
				} else {
                    state = 8;
				}
			} else if (state == 6) {
				if (Character.isDigit(ch)) {
					state = 6;
				} else if (ch == '.') {
					state = 8;
				} else {
                    state = 7;
                }
			} else if (state == 7) {
				--lookahead;
				return new Token(buffer.substring(tokenBegin, lookahead), "Decimal");
			} else if (state == 8) {
                --lookahead;
				throw new IllegalDecimalException();
            }

			++lookahead;
		}
	}

	/**
	 * Auxiliar method used to judge whether a character is a valid
	 * character of this calculator.
	 * 
	 * @param ch the character to be judge
	 * @return false if the given character is invalid
	 */
	private boolean isValid(char ch) {
		return Character.isDigit(ch)
			   || Character.isLetter(ch)
			   || " (),?:+-*/^<>=&|!".contains(""+ch);
	}

	/**
	 * Auxiliar method mainly used to debug. It will print
	 * the buffer and lookahead.
	 */
	public void printState() {
		System.out.println("Scanner state:");
		Tools.printn(' ', 8);
		System.out.println(buffer);
		Tools.printn(' ', lookahead);
		Tools.printn(' ', 8);
		System.out.println("^");
	}

	/**
	 * Auxiliar method mainly used to debug.
	 * It will convert the whole character stream into token
	 * stream.
	 * 
	 * @throws LexicalException if any lexical exception occurs
	 * 
	 * @return the token stream
	 */
	public String getTokens() throws LexicalException {
		// System.out.println(buffer);
		// printState();
		String output = "";
		Token token = getNextToken();
		while (token.getType() != "$") {
			output += token.getType() + " ";
			// printState();
			token = getNextToken();
		}
		return output;
	}

	public static void main(String[] args) throws Exception {
		String[] test = {
			"4 4.7 5e-3 5.3e3 true false tRue faLse",
			"sin cos max min",
			"! & | ? : = > < >= <= <>",
		};
		String[] output = {
			"Decimal Decimal Decimal Decimal BoolExpr BoolExpr BoolExpr BoolExpr ",
			"UnaryFunc UnaryFunc VariableFunc VariableFunc ",
			"! & | ? : Comp Comp Comp Comp Comp Comp ",
		};
		for (int i = 0; i < test.length; ++i) {
			Scanner scanner = new Scanner(test[i]);
			String out = scanner.getTokens();
			if (out.equals(output[i])) {
				System.out.println("Pass!");
			} else {
				System.out.print("Fail: ");
				System.out.printf("\tOutput: %s\n", out);
				System.out.printf("\tExpected: %s\n", output[i]);
			}
		}

		String[] exceptions = {
			".11",
			"e4",
			"4.e6",
			"3.",
			"7e",
			"mix",
			"@",
		};
		String[] exceptionsOutput = {
			"IllegalDecimalException",
			"IllegalDecimalException",
			"IllegalDecimalException",
			"IllegalDecimalException",
			"IllegalDecimalException",
			"IllegalIdentifierException",
			"IllegalSymbolException",
		};

		for (int i = 0; i < exceptions.length; ++i) {
			Scanner scanner = new Scanner(exceptions[i]);
			String out = null;
			try {
				out = scanner.getTokens();
			} catch (Exception e) {
				out = e.getClass().getName().substring(11);
			}
			if (out.equals(exceptionsOutput[i])) {
				System.out.println("Pass!");
			} else {
				System.out.println("Fail");
				System.out.printf("\tOutput: %s\n", out);
				System.out.printf("\tExpected: %s\n", exceptionsOutput[i]);
			}
		}
	}
}

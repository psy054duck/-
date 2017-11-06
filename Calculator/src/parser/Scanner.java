package parser;

import java.util.ArrayList;

import exceptions.*;
import parser.token.*;
import tools.*;

class Scanner {
	private String buffer;
	private int lookahead;
	private int tokenBegin;

	public Scanner(String expression) {
		buffer = expression;
		lookahead = 0;
	}

	public Token getNextToken() throws IllegalDecimalException,
									   IllegalSymbolException,
									   IllegalIdentifierException {
		while (lookahead < buffer.length()
		      && buffer.charAt(lookahead) == ' ') {
				  ++lookahead;
		}
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
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

	private Token leftParenthesis() {
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
		}

		if (buffer.charAt(lookahead) == '(') {
			++lookahead;
			return new LeftParenthesisToken();
		}

		return new ErrorToken();
	}

	private Token rightParenthesis() {
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
		}

		if (buffer.charAt(lookahead) == ')') {
			++lookahead;
			return new RightParenthesisToken();
		}

		return new ErrorToken();
	}
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
		if (lexeme == "sin" || lexeme == "cos") {
			return new UnaryFuncToken(lexeme);
		} else if (lexeme == "max" || lexeme == "min") {
			return new VariableFuncToken(lexeme);
		} else {
			throw new IllegalIdentifierException();
		}
	}
	private Token bool() {
		if (lookahead + 4 >= buffer.length()) {
			return new ErrorToken();
		}

		int init = lookahead;
		String lexeme = buffer.substring(lookahead, lookahead + 4).toLowerCase();
		if (lexeme.equals("true")
		    && (lookahead+4 == buffer.length() || ' ' == buffer.charAt(lookahead+4))) {
			lookahead += 4;
			return new BoolToken("true");
		}

		lexeme = buffer.substring(lookahead, lookahead + 5).toLowerCase();
		if (lexeme.equals("false")
		    && (lookahead+5 == buffer.length() || ' ' == buffer.charAt(lookahead+5))) {
			lookahead += 5;
			return new BoolToken("false");
		}
		lookahead = init;
		return new ErrorToken();
	}

	private Token question() {
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
		}

		if (buffer.charAt(lookahead) == '?') {
			++lookahead;
			return new QuestionToken();
		} else {
			return new ErrorToken();
		}
	}

	private Token colon() {
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
		}

		if (buffer.charAt(lookahead) == ':') {
			++lookahead;
			return new ColonToken();
		} else {
			return new ErrorToken();
		}
	}

	private Token unaryFunc() {
		if (lookahead + 3 >= buffer.length()) {
			return new ErrorToken();
		}

		String nextThree = buffer.substring(lookahead, lookahead+3);
		if ((nextThree == "sin" || nextThree == "cos")
		    && ! Character.isLetter(buffer.charAt(lookahead+4))) {
			lookahead += 3;
			return new UnaryFuncToken(nextThree);
		} else {
			return new ErrorToken();
		}
	}

	private Token binaryOp() {
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
		}

		char next = buffer.charAt(lookahead);
		if (next == '+' || next == '/' || next == '^' || next == '*') {
			++lookahead;
			return new BinaryOpToken(""+next);
		} else {
			return new ErrorToken();
		}
	}

	private Token minus() {
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
		}

		if (buffer.charAt(lookahead) == '-') {
			++lookahead;
			return new MinusToken();
		} else {
			return new ErrorToken();
		}
	}

	private Token variableFunc() {
		if (lookahead + 3 >= buffer.length()) {
			return new ErrorToken();
		}

		String nextThree = buffer.substring(lookahead, lookahead+3);
		if ((nextThree == "max" || nextThree == "min")
		    && ! Character.isLetter(buffer.charAt(lookahead+4))) {
			lookahead += 3;
			return new VariableFuncToken(nextThree);
		} else {
			return new ErrorToken();
		}
	}

	private Token comma() {
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
		}

		if (buffer.charAt(lookahead) == ',') {
			++lookahead;
			return new CommaToken();
		}
		return new ErrorToken();
	}

	private Token boolOp() {
		if (lookahead >= buffer.length()) {
			return new ErrorToken();
		}

		char ch = buffer.charAt(lookahead);
		if (ch == '&' || ch == '|') {
			++lookahead;
			return new BoolOpToken("" + ch);
		} else {
			return new ErrorToken();
		}
	}

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
				return new ErrorToken();
			}
		}

		return new CompToken(buffer.substring(tokenBegin, lookahead));
	}

	private Token decimal() throws IllegalDecimalException {
		tokenBegin = lookahead;
		int state = 0;

		while (true) {
            char ch = 0;
            if (lookahead < buffer.length()) {
                ch = buffer.charAt(lookahead);
            }
			if (state == 0) {
				if (Character.isDigit(ch)) {
					state = 1;
				} else {
					return new ErrorToken();
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
				return new DecimalToken(buffer.substring(tokenBegin, lookahead));
			} else if (state == 8) {
                --lookahead;
				throw new IllegalDecimalException();
            }

			++lookahead;
		}
	}

	private boolean isValid(char ch) {
		return Character.isDigit(ch)
			   || Character.isLetter(ch)
			   || " (),?:+-*/^<>=&|!".contains(""+ch);
	}

	public void printState() {
		System.out.println("Scanner state:");
		Tools.printn(' ', 8);
		System.out.println(buffer);
		Tools.printn(' ', lookahead);
		Tools.printn(' ', 8);
		System.out.println("^");
	}

	public void printTokens() throws IllegalDecimalException,
									 IllegalSymbolException,
									 IllegalIdentifierException {
		System.out.println(buffer);
		// printState();
		Token token = getNextToken();
		while (token.getType() != "Error") {
			System.out.print(token.getType() + " ");
			// printState();
			token = getNextToken();
		}
		System.out.print("\n");
	}

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner("2.25E+2 - (55.5 + 4 * (10 / 2) ^ 2)");
		scanner.printTokens();
	}
}

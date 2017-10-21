import java.io.*;

/**
 * Parser with error recovery strategy that when parser encounter an
 * unexpected token, it will discard all the following tokens until
 * an expected one appear.
 */
class Parser {
	/** The next token */
	private static int lookahead;

	/** The previous token */
	private static int previous;

	/** Whether the expression is wrong */
	private boolean isWrong = false;

	private Scanner scanner;

	/** Postfix expression of the input expression */
	private String res;

	/**
	 * Constructor to initiate a scanner and get the first token for
	 * this parser.
	 * 
	 * @throws IOException if scanner fails to read from input
	 */
	public Parser() throws IOException {
		scanner = new Scanner();
		try {
			lookahead = scanner.getNextToken();
		} catch (Error e) {
			System.out.println(e.getMessage());
		}
		res = "";
	}

	/**
	 * Method for non-terminal expr
	 * 
	 * @throws Error if this parser encounters some syntax error
	 */
	void expr() throws Error {
		term();
		rest();
		if (! isWrong) {
			System.out.println(res);
			isWrong = false;
		}
	}

	/**
	 * Method for non-terminal rest.
	 * 
	 * @throws Error if this parser encoutners some syntax error
	 */
	void rest() throws Error {
		while (true) {
			if (lookahead == '+') {
				match('+');
				term();
				res += "+";
			} else if (lookahead == '-') {
				match('-');
				term();
				res += "-";
			} else if (lookahead == '\n') {
				break;
			} else {
				missOperator();
			}
		}
	}

	/**
	 * Method for non'terminal term.
	 * 
	 * @throws Error if this parser encounters some syntax error
	 */
	void term() throws Error {
		while (true) {
			if (Character.isDigit((char) lookahead)) {
				res += (char) lookahead;
				match(lookahead);
				break;
			} else {
				missDigit();
				if (lookahead == '\n') {
					break;
				}
			}
		}
	}

	/**
	 * Method for match.
	 * If lookahead if the expected token, this method will get next token.
	 * Otherwise Error will be thrown.
	 * 
	 * @param t the token that this parser want
	 * 
	 * @throws Error if lookahead if not the expected token
	 */
	void match(int t) throws Error {
		if (lookahead == t) {
			try {
				previous = lookahead;
				lookahead = scanner.getNextToken();
			} catch (Error e) {
				isWrong = true;
				lookahead = 'e';
				System.out.println(e.getMessage());
			}
		} else {
			isWrong = true;
			throw new Error("syntax error");
		}
	}

	/**
	 * Handler for situation where a digit is expected 
	 * but another kind of token is recognized.
	 */
	private void missDigit() {
		String errors = "";
		isWrong = true;
		if (previous == 0) {
			System.out.println("Syntax error: expected 'digit' token");
		} else {
			System.out.println("Syntax error: expected 'digit' token after "
			                   + (char) previous);
		}
		System.out.print("\t" + scanner.getInput() + "\n\t");
		System.out.print(nspace(scanner.getCount()-1));
		System.out.print("^");
		int cnt = -1;
		previous = lookahead;
		while (lookahead != '\n' && ! Character.isDigit((char) lookahead)) {
			++cnt;
			try {
				lookahead = scanner.getNextToken();
			} catch (Error e) {
				lookahead = 'e';
				errors += e.getMessage() + "\n";
			}
		}
		for (int i = 0; i < cnt; ++i) {
			System.out.print("~");
		}
		System.out.print("\n");
		if (! errors.isEmpty()) {
			System.out.print(errors);
		}
	}

	/**
	 * Handler for situation where an operator is expected
	 * but another kind of token is recognized.
	 */
	private void missOperator() {
		String errors = "";
		isWrong = true;
		System.out.println("Syntax error: expected '+' or '-' token after "
		                   + "a 'digit' token");
		System.out.print("\t" + scanner.getInput() + "\n\t");
		System.out.print(nspace(scanner.getCount()-1));
		System.out.print("^");
		int cnt = -1;
		previous = lookahead;
		while (lookahead != '\n' && lookahead != '+' && lookahead != '-') {
			++cnt;
			try {
				lookahead = scanner.getNextToken();
			} catch (Error e) {
				lookahead = 'e';
				errors += e.getMessage() + "\n";
			}
		}
		for (int i = 0; i < cnt; ++i) {
			System.out.print("~");
		}
		System.out.print("\n");
		if (! errors.isEmpty()) {
			System.out.print(errors);
		}
	}

	/**
	 * Auxiliary function to generate the given number of spaces
	 * 
	 * @param n the number of spaces expected to generate
	 * 
	 * @return a string containing n spaces
	 */
	private String nspace(int n) {
		String res = "";
		for (int i = 0; i < n; ++i) {
			res += " ";
		}
		return res;
	}
}

/**
 * Scanner with basic lexical check.
 * getNextToken() is the method used to get the next token.
 * Note that if the scanner encounters an invalid token, Error will be thrown
 * instead of returning a special token standing for invalid token.
 * This scanner is so simple that just one buffer is sufficient to handle an
 * expression.
 */
class Scanner {
	int cnt = 0;
	String buffer;

	/**
	 * Constructor to read input and put it into buffer.
	 * 
	 * @throws IOException if fail to read from input
	 */
	public Scanner() throws IOException {
		BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
		buffer = scanner.readLine();
	}

	/**
	 * Recognize next token from buffer.
	 * 
	 * @throws Error if encounters invalid characters.
	 * @return '\n' if the buffer is exhausted
	 */
	public int getNextToken() {
		if (cnt >= buffer.length()) {
			++cnt;
			return '\n';
		}
		char ch = buffer.charAt(cnt++);
		if (! check(ch)) {
			String errorMessage = "Lexical error: '+', '-' or digits are expected\n";
			errorMessage += "\t" + buffer + "\n\t" + nspace(cnt-1) + "^";
			throw new Error(errorMessage);
		}
		return ch;
	}

	public String getInput() {
		return buffer;
	}

	/**
	 * Get the number of token has been recognized.
	 * 
	 * @return cnt
	 */
	public int getCount() {
		return cnt;
	}

	/**
	 * Check whether ch is a valid character.
	 * 
	 * @param ch the character to be checked
	 * 
	 * @return false if ch is invalid
	 */
	private boolean check(char ch) throws Error {
		return (ch == '+' || ch == '-' || Character.isDigit((char) ch));
	}

	/**
	 * Auxiliary function to generate the given number of spaces.
	 * 
	 * @param n the number of spaces should be generated.
	 * 
	 * @return a string containing n spaces
	 */
	private String nspace(int n) {
		String res = "";
		for (int i = 0; i < n; ++i) {
			res += " ";
		}
		return res;
	}
}

/**
 * Class with main method to start this program.
 */
public class Postfix {
	public static void main(String[] args) throws IOException {
		System.out.println("Input an infix expression and output its postfix notation:");
		new Parser().expr();
		System.out.println("End of program.");
	}
}

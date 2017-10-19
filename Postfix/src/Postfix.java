import java.io.*;

class Parser {
	static int lookahead;
	Scanner scanner;

	public Parser() throws IOException, Error {
		scanner = new Scanner();
		lookahead = scanner.getNextToken();
	}

	void expr() throws IOException, Error {
		term();
		rest();
	}

	void rest() throws IOException, Error {
		while (true) {
			if (lookahead == '+') {
				match('+');
				term();
				System.out.write('+');
			} else if (lookahead == '-') {
				match('-');
				term();
				System.out.write('-');
			} else {
				break;
			}
		}
	}

	void term() throws IOException, Error {
		if (Character.isDigit((char)lookahead)) {
			System.out.write((char)lookahead);
			match(lookahead);
		} else throw new Error("syntax error");
	}

	void match(int t) throws IOException, Error {
		if (lookahead == t) {
			lookahead = scanner.getNextToken();
		} else {
			throw new Error("syntax error");
		}
	}
}

class Scanner {
	int cnt = 0;
	String buffer;

	public Scanner() throws IOException {
		BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
		buffer = scanner.readLine();
	}

	public int getNextToken() {
		if (cnt == buffer.length()) return '\n';
		char ch = buffer.charAt(cnt++);
		if (! check(ch)) {
			System.out.printf("\n\t%s\n", buffer);
			System.out.print('\t');
			nspace(cnt-1);
			System.out.print("^\n");
			System.out.println("Lexical error: '+', '-' and digits are expected");
			throw new Error();
		}
		return ch;
	}

	private boolean check(char ch) throws Error {
		return (ch == '+' || ch == '-' || Character.isDigit((char) ch));
	}

	private void nspace(int n) {
		for (int i = 0; i < n; ++i) {
			System.out.print(' ');
		}
	}
}

public class Postfix {
	public static void main(String[] args) throws IOException {
		System.out.println("Input an infix expression and output its postfix notation:");
		new Parser().expr();
		System.out.println("\nEnd of program.");
	}
}

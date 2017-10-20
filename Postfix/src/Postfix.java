import java.io.*;

class Parser {
	static int lookahead;
	static int previous;
	boolean isWrong = false;
	Scanner scanner;
	String res;

	public Parser() throws IOException, Error {
		scanner = new Scanner();
		try {
			lookahead = scanner.getNextToken();
		} catch (Error e) {
			System.out.println(e.getMessage());
		}
		res = "";
	}

	void expr() throws IOException, Error {
		term();
		rest();
		if (! isWrong) {
			System.out.print(res);
			isWrong = false;
		}
	}

	void rest() throws IOException, Error {
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

	void term() throws IOException, Error {
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

	void match(int t) throws IOException, Error {
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

	private void missDigit() {
		String errors = "";
		isWrong = true;
		System.out.println("Syntax error: expected 'digit' token");
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

	private void missOperator() {
		String errors = "";
		isWrong = true;
		System.out.println("Syntax error: expected '+' or '-' token");
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

	private String nspace(int n) {
		String res = "";
		for (int i = 0; i < n; ++i) {
			res += " ";
		}
		return res;
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
			String errorMessage = "Lexical error: '+', '-' or digits are expected\n";
			errorMessage += "\t" + buffer + "\n\t" + nspace(cnt-1) + "^";
			throw new Error(errorMessage);
		}
		return ch;
	}

	public String getInput() {
		return buffer;
	}

	public int getCount() {
		return cnt;
	}

	private boolean check(char ch) throws Error {
		return (ch == '+' || ch == '-' || Character.isDigit((char) ch));
	}

	private String nspace(int n) {
		String res = "";
		for (int i = 0; i < n; ++i) {
			res += " ";
		}
		return res;
	}
}

public class Postfix {
	public static void main(String[] args) throws IOException {
		System.out.println("Input an infix expression and output its postfix notation:");
		new Parser().expr();
		System.out.println("End of program.");
	}
}

import java.io.FileReader;

public class Test {
    public static void main(String[] argv) throws Exception {
        OberonScanner scanner = new OberonScanner(new FileReader(argv[0]));
        Symbol s = null;
        do {
            s = scanner.yylex();
            System.out.println(s);
        } while (s.getType() != Type.EOF);
    }
}

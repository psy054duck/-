import java.io.FileReader;

class Main {
    public static void main(String[] argv) throws Exception{
        Parser p = new Parser(new OberonScanner(new FileReader(argv[0])));
        if (argv.length > 1) {
            p.debug_parse();
        } else {
            p.parse();
        }
    }
}
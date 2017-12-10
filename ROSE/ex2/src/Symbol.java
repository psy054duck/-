public class Symbol {
    private int type;
    private int line;
    private int column;
    private String value;

    public Symbol(int t, int l, int c, String v) {
        type = t;
        line = l;
        column = c;
        value = v;
    }

    public Symbol(int t, int l, int c) {
        type = t;
        line = l;
        column = c;
        value = "";
    }

    public String toString() {
        String lineStr = "line:" + String.format("%3d", line);
        String columnStr = "column:" + String.format("%3d", column);
        if (! value.isEmpty()) {
            return String.format("sym:%3d %15s%15s%10s: %s", type, lineStr, columnStr, "value", value);
        } else {
            return String.format("sym:%3d %15s%15s", type, lineStr, columnStr);
        }
    }

    public int getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }
}

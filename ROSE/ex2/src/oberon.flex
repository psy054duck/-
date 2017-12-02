%%

%public
%class OberonScanner
%line
%column
%type Symbol
%caseless

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, String value) {
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
%}

Alphanum   = {:letter:} | {:digit:}
Identifier = {:letter:}{Alphanum}*
Integer    = [:digit:]+
WhiteSpace = [ \t\n\r]*

%%
<YYINITIAL> {
    "if"                            { return symbol(Type.IF); }
    "else"                          { return symbol(Type.ELSE); }
    "elsif"                         { return symbol(Type.ELSIF); }
    "then"                          { return symbol(Type.THEN); }
    "end"                           { return symbol(Type.END); }
    "array"                         { return symbol(Type.ARRAY); }
    "var"                           { return symbol(Type.VAR); }
    "of"                            { return symbol(Type.OF); }
    "while"                         { return symbol(Type.WHILE); }
    "do"                            { return symbol(Type.DO); }
    "record"                        { return symbol(Type.RECORD); }
    "begin"                         { return symbol(Type.BEGIN); }
    "module"                        { return symbol(Type.MODULE); }
    "procedure"                     { return symbol(Type.PROCEDURE); }
    "const"                         { return symbol(Type.CONST); }
    "mod"                           { return symbol(Type.MOD); }
    "div"                           { return symbol(Type.DIV); }
    "or"                            { return symbol(Type.OR); }
    "write"                         { return symbol(Type.WRITE); }
    "writeln"                       { return symbol(Type.WRITELN); }
    "read"                          { return symbol(Type.READ); }

    "+"                             { return symbol(Type.PLUS); }
    "-"                             { return symbol(Type.MINUS); }
    "*"                             { return symbol(Type.TIMES); }
    ";"                             { return symbol(Type.SEMI); }
    ","                             { return symbol(Type.COMMA); }
    "("                             { return symbol(Type.LEFTP); }
    ")"                             { return symbol(Type.RIGHTP); }
    "["                             { return symbol(Type.LEFTB); }
    "]"                             { return symbol(Type.RIGHTB); }
    "."                             { return symbol(Type.DOT); }
    ":"                             { return symbol(Type.COLON); }
    
    "="                             { return symbol(Type.EQ); }
    "#"                             { return symbol(Type.NE); }
    "<"                             { return symbol(Type.LT); }
    "<="                            { return symbol(Type.LE); }
    ">"                             { return symbol(Type.GT); }
    ">="                            { return symbol(Type.GE); }
    ":="                            { return symbol(Type.ASSIGN); }
}

<YYINITIAL> {
    {Identifier}        { return symbol(Type.IDENTIFIER, yytext()); }
    {Integer}           { return symbol(Type.INTEGER, yytext()); }
    {WhiteSpace}        {}
}

<<EOF>>                 { return symbol(Type.EOF); }
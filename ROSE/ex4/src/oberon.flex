import exceptions.*;
%%

%public
%class OberonScanner
%line
%column
%type Symbol
%caseless
%scanerror OberonException

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, String value) throws OberonException {
        if (type == Type.IDENTIFIER && yytext().length() > 24) {
            throw new IllegalIdentifierLengthException(getErrorString());
        } else if (type == Type.INTEGER && yytext().length() > 12) {
            throw new IllegalIntegerException(getErrorString());
        }
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }

    private String oct2dec(String num) {
        return String.valueOf(Integer.parseInt(num, 8));
    }

    private String getErrorString() {
        return yytext() + "  line: " + Integer.toString(yyline + 1)+ "       column: " + Integer.toString(yycolumn+1);
    }
%}

Alphanum        = [:letter:] | [:digit:]
Identifier      = [:letter:]{Alphanum}*
Integer         = [:digit:]+
OctalInteger    = 0[:digit:]+
WhiteSpace      = [ \t\n\r]*
Comment         = "(*" ~"*)"
IllegalNumber   = ({Integer} | {OctalInteger}){Identifier}

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

    "+"                             { return symbol(Type.PLUS, yytext()); }
    "-"                             { return symbol(Type.MINUS, yytext()); }
    "*"                             { return symbol(Type.TIMES, yytext()); }
    ";"                             { return symbol(Type.SEMI, yytext()); }
    ","                             { return symbol(Type.COMMA, yytext()); }
    "("                             { return symbol(Type.LEFTP, yytext()); }
    ")"                             { return symbol(Type.RIGHTP, yytext()); }
    "["                             { return symbol(Type.LEFTB, yytext()); }
    "]"                             { return symbol(Type.RIGHTB, yytext()); }
    "."                             { return symbol(Type.DOT, yytext()); }
    ":"                             { return symbol(Type.COLON, yytext()); }
    
    "="                             { return symbol(Type.EQ, yytext()); }
    "#"                             { return symbol(Type.NE, yytext()); }
    "<"                             { return symbol(Type.LT, "&lt"); }
    "<="                            { return symbol(Type.LE, "&le"); }
    ">"                             { return symbol(Type.GT, "&gt"); }
    ">="                            { return symbol(Type.GE, "&ge"); }
    ":="                            { return symbol(Type.ASSIGN, yytext()); }
}

<YYINITIAL> {
    {Identifier}        { return symbol(Type.IDENTIFIER, yytext()); }
    {OctalInteger}      { 
        String dec;
        try {
            dec = oct2dec(yytext());
        } catch (Exception e) {
            throw new IllegalOctalException(getErrorString());
        }
        return symbol(Type.INTEGER, dec);
    }
    {Integer}           { return symbol(Type.INTEGER, yytext()); }
    {WhiteSpace}        {}
    {Comment}           {}
    {IllegalNumber}     { throw new IllegalIntegerException(getErrorString()); }
    "(*"                { throw new MismatchedCommentException(getErrorString()); }
    .                   { throw new IllegalSymbolException(getErrorString()); }
}

<<EOF>>                 { return symbol(Type.EOF); }

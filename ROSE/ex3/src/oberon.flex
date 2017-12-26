import java_cup.runtime.*;

%%

%public
%class OberonScanner
%line
%cup
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

    private String oct2dec(String num) {
        return String.valueOf(Integer.parseInt(num, 8));
    }
%}

Alphanum        = [:letter:] | [:digit:]
Identifier      = [:letter:]{Alphanum}*
Integer         = [:digit:]+
OctalInteger    = 0[:digit:]+
WhiteSpace      = [ \t\n\r]*
Comment         = "(*" ~"*)"

%%
<YYINITIAL> {
    "if"                            { return symbol(sym.IF); }
    "else"                          { return symbol(sym.ELSE); }
    "elsif"                         { return symbol(sym.ELSIF); }
    "then"                          { return symbol(sym.THEN); }
    "end"                           { return symbol(sym.END); }
    "array"                         { return symbol(sym.ARRAY); }
    "var"                           { return symbol(sym.VAR); }
    "of"                            { return symbol(sym.OF); }
    "while"                         { return symbol(sym.WHILE); }
    "do"                            { return symbol(sym.DO); }
    "record"                        { return symbol(sym.RECORD); }
    "begin"                         { return symbol(sym.BEGIN); }
    "module"                        { return symbol(sym.MODULE); }
    "procedure"                     { return symbol(sym.PROCEDURE); }
    "const"                         { return symbol(sym.CONST); }
    "mod"                           { return symbol(sym.MOD); }
    "div"                           { return symbol(sym.DIV); }
    "or"                            { return symbol(sym.OR); }

    "+"                             { return symbol(sym.PLUS); }
    "-"                             { return symbol(sym.MINUS); }
    "*"                             { return symbol(sym.TIMES); }
    ";"                             { return symbol(sym.SEMI); }
    ","                             { return symbol(sym.COMMA); }
    "("                             { return symbol(sym.LEFTP); }
    ")"                             { return symbol(sym.RIGHTP); }
    "["                             { return symbol(sym.LEFTB); }
    "]"                             { return symbol(sym.RIGHTB); }
    "."                             { return symbol(sym.DOT); }
    ":"                             { return symbol(sym.COLON); }
    
    "="                             { return symbol(sym.EQ); }
    "#"                             { return symbol(sym.NE); }
    "<"                             { return symbol(sym.LT); }
    "<="                            { return symbol(sym.LE); }
    ">"                             { return symbol(sym.GT); }
    ">="                            { return symbol(sym.GE); }
    ":="                            { return symbol(sym.ASSIGN); }
}

<YYINITIAL> {
    {Identifier}        { return symbol(sym.IDENTIFIER, yytext()); }
    {OctalInteger}      { return symbol(sym.INTEGER, oct2dec(yytext())); }
    {Integer}           { return symbol(sym.INTEGER, yytext()); }
    {WhiteSpace}        {}
    {Comment}           {}
}

<<EOF>>                 { return symbol(sym.EOF); }

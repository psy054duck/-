import exceptions.*;
import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
%%

%cup
%public
%class OberonScanner
%line
%column
%caseless
%scanerror OberonException

%{
    ComplexSymbolFactory sf = new ComplexSymbolFactory();
    private java_cup.runtime.Symbol token(int type) {
        int left = yyline + 1;
        int right = yycolumn + 1;
        return new java_cup.runtime.Symbol(type, left, right, Symbol.terminalNames[type]);
    }

    private java_cup.runtime.Symbol token(int type, String value) throws OberonException {
        if (type == Symbol.IDENTIFIER && yytext().length() > 24) {
            throw new IllegalIdentifierLengthException(getErrorString());
        } else if (type == Symbol.INTEGER && yytext().length() > 12) {
            throw new IllegalIntegerException(getErrorString());
        }
        int left = yyline + 1;
        int right = yycolumn + 1;       
        return new java_cup.runtime.Symbol(type, left, right, value);
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
    "if"                            { return token(Symbol.IF); }
    "else"                          { return token(Symbol.ELSE); }
    "elsif"                         { return token(Symbol.ELSIF); }
    "then"                          { return token(Symbol.THEN); }
    "end"                           { return token(Symbol.END); }
    "array"                         { return token(Symbol.ARRAY); }
    "var"                           { return token(Symbol.VAR); }
    "of"                            { return token(Symbol.OF); }
    "while"                         { return token(Symbol.WHILE); }
    "do"                            { return token(Symbol.DO); }
    "record"                        { return token(Symbol.RECORD); }
    "begin"                         { return token(Symbol.BEGIN); }
    "module"                        { return token(Symbol.MODULE); }
    "procedure"                     { return token(Symbol.PROCEDURE); }
    "const"                         { return token(Symbol.CONST); }
    "mod"                           { return token(Symbol.MOD); }
    "div"                           { return token(Symbol.DIV); }
    "or"                            { return token(Symbol.OR); }

    "+"                             { return token(Symbol.PLUS, yytext()); }
    "-"                             { return token(Symbol.MINUS, yytext()); }
    "*"                             { return token(Symbol.TIMES, yytext()); }
    ";"                             { return token(Symbol.SEMI, yytext()); }
    ","                             { return token(Symbol.COMMA, yytext()); }
    "("                             { return token(Symbol.LEFTP, yytext()); }
    ")"                             { return token(Symbol.RIGHTP, yytext()); }
    "["                             { return token(Symbol.LEFTB, yytext()); }
    "]"                             { return token(Symbol.RIGHTB, yytext()); }
    "."                             { return token(Symbol.DOT, yytext()); }
    ":"                             { return token(Symbol.COLON, yytext()); }
    
    "="                             { return token(Symbol.EQ, yytext()); }
    "#"                             { return token(Symbol.NE, yytext()); }
    "<"                             { return token(Symbol.LT, "&lt"); }
    "<="                            { return token(Symbol.LE, "&le"); }
    ">"                             { return token(Symbol.GT, "&gt"); }
    ">="                            { return token(Symbol.GE, "&ge"); }
    ":="                            { return token(Symbol.ASSIGN, yytext()); }
}

<YYINITIAL> {
    {Identifier}        { return token(Symbol.IDENTIFIER, yytext()); }
    {OctalInteger}      { 
        String dec;
        try {
            dec = oct2dec(yytext());
        } catch (Exception e) {
            throw new IllegalOctalException(getErrorString());
        }
        return token(Symbol.INTEGER, dec);
    }
    {Integer}           { return token(Symbol.INTEGER, yytext()); }
    {WhiteSpace}        {}
    {Comment}           {}
    {IllegalNumber}     { throw new IllegalIntegerException(getErrorString()); }
    "(*"                { throw new MismatchedCommentException(getErrorString()); }
    .                   { throw new IllegalSymbolException(getErrorString()); }
}

<<EOF>>                 { return token(Symbol.EOF); }

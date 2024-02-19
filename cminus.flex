/*
  Commented By: Christopher Lopes
  File Name: cminus.flex
  To Create: > jflex cminus.flex

  and then after the parser is created
  > javac CMinusScanner2.java
*/

/* --------------------------Usercode Section------------------------ */

package source;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

%%

/* -----------------Options and Declarations Section----------------- */

/*
   The name of the class JFlex will create will be CMinusScanner2.
   Will write the code to the file CMinusScanner2.java.
*/
%class CMinusScanner2 
%implements Scanner

/*
  The current line number can be accessed with the variable yyline
  and the current column number with the variable yycolumn.
*/
%state COMMENT
%type Token
%unicode

/*
  Declarations

  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the CMinusScanner2 class source.
  Here you declare member variables and functions that are used inside
  scanner actions.
*/
%{
   private Token nextToken;

   public CMinusScanner2(String filename) throws IOException, DFAException {
      this(new BufferedReader(new FileReader(filename)));
      nextToken = scanToken();
   }
   
   public Token getNextToken() throws IOException, DFAException {
      Token returnToken = nextToken;
      if (nextToken.type != Token.TokenType.EOF) {
         nextToken = scanToken();
      }
      return returnToken;
   }
    
   public Token viewNextToken() {
      return nextToken;
   }

   private Token scanToken() throws IOException {
      return yylex();
   }

   public static void main(String[] args) throws Exception {
      Scanner scanner = new CMinusScanner2("code/test_all.cm");
      Token curr = new Token(null);
      while (curr.type != Token.TokenType.EOF) {
         curr = scanner.getNextToken();
         System.out.println(curr.type + "   |   " + curr.data);
      }
   }
%}


/*
  Macro Declarations

  These declarations are regular expressions that will be used latter
  in the Lexical Rules Section.
*/

/* A line terminator is a \r (carriage return), \n (line feed), or
   \r\n. */
LineTerminator = \r|\n|\r\n

/* A literal integer is is a number beginning with a number between
   one and nine followed by zero or more numbers between zero and nine
   or just a zero.  */

digit = [0-9]
number = {digit}+
letter = [a-zA-Z]
identifier = {letter}+
id_num_error = {letter}+{digit} | {digit}+{letter}
whitespace = ({LineTerminator} | [ \t\f])+

%%
/* ------------------------Lexical Rules Section---------------------- */

/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. */

   /* YYINITIAL is the state at which the lexer begins scanning.  So
   these regular expressions will only be matched if the scanner is in
   the start state YYINITIAL. */

<YYINITIAL> {
   "if"                 { return new Token(Token.TokenType.IF); }
   "else"               { return new Token(Token.TokenType.ELSE); }
   "while"              { return new Token(Token.TokenType.WHILE); }
   "return"             { return new Token(Token.TokenType.RETURN); }
   "int"                { return new Token(Token.TokenType.INT); }
   "void"               { return new Token(Token.TokenType.VOID); }

   ";"                  { return new Token(Token.TokenType.SEMI); }
   ","                  { return new Token(Token.TokenType.COMMA); }
   "["                  { return new Token(Token.TokenType.L_BRACK); }
   "]"                  { return new Token(Token.TokenType.R_BRACK); }
   "("                  { return new Token(Token.TokenType.L_PAREN); }
   ")"                  { return new Token(Token.TokenType.R_PAREN); }
   "{"                  { return new Token(Token.TokenType.L_CURLY); }
   "}"                  { return new Token(Token.TokenType.R_CURLY); }
   "=="                 { return new Token(Token.TokenType.EQ); }
   "!="                 { return new Token(Token.TokenType.NEQ); }
   "="                  { return new Token(Token.TokenType.ASSIGN); }
   ">="                 { return new Token(Token.TokenType.GTE); }
   ">"                  { return new Token(Token.TokenType.GT); }
   "<="                 { return new Token(Token.TokenType.LTE); }
   "<"                  { return new Token(Token.TokenType.LT); }
   "+"                  { return new Token(Token.TokenType.PLUS); }
   "-"                  { return new Token(Token.TokenType.MINUS); }
   "*"                  { return new Token(Token.TokenType.MULT); }
   "/"                  { return new Token(Token.TokenType.DIV); }

   "/*"                 { yybegin(COMMENT); }
   

   {id_num_error}       { return new Token(Token.TokenType.ERR, yytext()); }
   {number}             { return new Token(Token.TokenType.NUM, Integer.valueOf(yytext()));}
   {identifier}         { return new Token(Token.TokenType.ID, yytext()); }

    /* Don't do anything if whitespace is found */
    {whitespace}       { /* just skip what was found, do nothing */; }

    <<EOF>>            { return new Token(Token.TokenType.EOF); }
}

<COMMENT> {
   "*/"                 { yybegin(YYINITIAL); }
   <<EOF>>              {yybegin(YYINITIAL); return new Token(Token.TokenType.ERR);}
   [^]                  { /* Do Nothing */ }
}

/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */

[^]                    { return new Token(Token.TokenType.ERR, yytext()); }
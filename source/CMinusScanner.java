package source;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class CMinusScanner implements Scanner {
    public enum DFAState {
        START,
        
        IN_ID,
        IN_NUM,
        IN_DIV,
        IN_COMMENT,
        IN_COMMENT_OUT,
        IN_LT,
        IN_GT,
        IN_EQ,
        IN_EXCL,
        
        DONE }
    
    private BufferedReader inFile;
    private Token nextToken;

    public CMinusScanner(String filename) throws FileNotFoundException, IOException, DFAException {
        inFile = new BufferedReader(new FileReader(filename));
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

    public Token scanToken() throws IOException, DFAException {       
        Token returnToken = null;
        String currData = "";
        boolean EOF = false;
        char currChar;
        DFAState currState = DFAState.START;
        //DFA switch statement
        while (currState != DFAState.DONE) {
            currChar = (char) getNextChar(inFile); 
            EOF = (currChar == (char) -1);
            switch(currState) {
                case START:
                    if (isSpace(currChar)) {
                        continue;
                    } else if (isDigit(currChar)) {
                        currData += currChar;
                        currState = DFAState.IN_NUM;
                    } else if (isLetter(currChar)) {
                        currData += currChar;
                        currState = DFAState.IN_ID;
                    } else if (!EOF) {
                        switch (currChar) {
                            case '+':
                                returnToken = new Token(Token.TokenType.PLUS);
                                currState = DFAState.DONE;
                                break;
                            case '-':
                                returnToken = new Token(Token.TokenType.MINUS);
                                currState = DFAState.DONE;
                                break;
                            case '*':
                                returnToken = new Token(Token.TokenType.MULT);
                                currState = DFAState.DONE;
                                break;
                            case '/':
                                currState = DFAState.IN_DIV;
                                break;
                            case '<':
                                currState = DFAState.IN_LT;
                                break;
                            case '>':
                                currState = DFAState.IN_GT;
                                break;
                            case '=':
                                currState = DFAState.IN_EQ;
                                break;
                            case '!':
                                currState = DFAState.IN_EXCL;
                                break;
                            case '(':
                                returnToken = new Token(Token.TokenType.L_PAREN);
                                currState = DFAState.DONE;
                                break;
                            case ')':
                                returnToken = new Token(Token.TokenType.R_PAREN);
                                currState = DFAState.DONE;
                                break;
                            case '[':
                                returnToken = new Token(Token.TokenType.L_BRACK);
                                currState = DFAState.DONE;
                                break;
                            case ']':
                                returnToken = new Token(Token.TokenType.R_BRACK);
                                currState = DFAState.DONE;
                                break;
                            case '{':
                                returnToken = new Token(Token.TokenType.L_CURLY);
                                currState = DFAState.DONE;
                                break;
                            case '}':
                                returnToken = new Token(Token.TokenType.R_CURLY);
                                currState = DFAState.DONE;
                                break;
                            case ';':
                                returnToken = new Token(Token.TokenType.SEMI);
                                currState = DFAState.DONE;
                                break;
                            case ',':
                                returnToken = new Token(Token.TokenType.COMMA);
                                currState = DFAState.DONE;
                                break;
                            default:
                                returnToken = new Token(Token.TokenType.ERR);
                                currState = DFAState.DONE;                                
                                break;
                        }
                    } else {
                        returnToken = new Token(Token.TokenType.EOF);
                        currState = DFAState.DONE;
                    }
                    break;
                case IN_ID:
                    if (isSpace(currChar) || isPunctuation(currChar) || EOF) {
                        returnToken = new Token(Token.TokenType.ID, currData);
                        currState = DFAState.DONE;
                        unGetNextChar(inFile);
                    } else if (!isLetter(currChar)) {
                        returnToken = new Token(Token.TokenType.ERR, currData + currChar);
                        currState = DFAState.DONE;
                    } else {
                        currData += currChar;
                    }
                    break;
                case IN_NUM:
                    if (isSpace(currChar) || isPunctuation(currChar) || EOF) {
                        returnToken = new Token(Token.TokenType.NUM, currData);
                        currState = DFAState.DONE;
                        unGetNextChar(inFile);
                    } else if (!isDigit(currChar)) {
                        returnToken = new Token(Token.TokenType.ERR, currData + currChar);
                        currState = DFAState.DONE;
                    } else {
                        currData += currChar;
                    }
                    break;
                case IN_DIV:
                    if (currChar == '*') {
                        currState = DFAState.IN_COMMENT;
                    } else {
                        returnToken = new Token(Token.TokenType.DIV);
                        currState = DFAState.DONE;
                        unGetNextChar(inFile);
                    }
                    break;
                case IN_COMMENT:
                    if (EOF) {
                        //TODO - might need to be an error token if expecting end of comment
                        returnToken = new Token(Token.TokenType.EOF);
                        currState = DFAState.DONE;
                    } else if (currChar == '*') {
                        currState = DFAState.IN_COMMENT_OUT;
                    }
                    break;
                case IN_COMMENT_OUT:
                    if (EOF) {
                        //TODO - might need to be an error token if expecting end of comment
                        returnToken = new Token(Token.TokenType.EOF);
                        currState = DFAState.DONE;
                    } else if (currChar == '/') {
                        currState = DFAState.START;
                    } else if (currChar != '*') {
                        currState = DFAState.IN_COMMENT;
                    } 
                    break;
                case IN_LT:
                    if (currChar == '=') {
                        returnToken = new Token(Token.TokenType.LTE);
                        currState = DFAState.DONE;
                    } else {
                        unGetNextChar(inFile);
                        returnToken = new Token(Token.TokenType.LT);
                        currState = DFAState.DONE;
                    }
                    break;
                case IN_GT:
                    if (currChar == '=') {
                        returnToken = new Token(Token.TokenType.GTE);
                        currState = DFAState.DONE;
                    } else {
                        returnToken = new Token(Token.TokenType.GT);
                        currState = DFAState.DONE;
                        unGetNextChar(inFile);
                    }
                    break;
                case IN_EQ:
                    if (currChar == '=') {
                        returnToken = new Token(Token.TokenType.EQ);
                        currState = DFAState.DONE;
                    } else {
                        unGetNextChar(inFile);
                        returnToken = new Token(Token.TokenType.ASSIGN);
                        currState = DFAState.DONE;
                    }
                    break;
                case IN_EXCL:
                    if (currChar == '=') {
                        returnToken = new Token(Token.TokenType.NEQ);
                        currState = DFAState.DONE;
                    } else {
                        returnToken = new Token(Token.TokenType.ERR);
                        currState = DFAState.DONE;
                        unGetNextChar(inFile);
                    }
                    break;
                case DONE:
                    break;
                default:
                    throw new DFAException("OUTSIDE OF POSSIBLE DFA STATES!");
            } 
        }

        //sees if it matches a keyword
        switch (currData) {
            case "if":
                returnToken = new Token(Token.TokenType.IF);
                break;
            case "else":
                returnToken = new Token(Token.TokenType.ELSE);
                break;
            case "while":
                returnToken = new Token(Token.TokenType.WHILE);
                break;
            case "return":
                returnToken = new Token(Token.TokenType.RETURN);
                break;
            case "int":
                returnToken = new Token(Token.TokenType.INT);
                break;
            case "void":
                returnToken = new Token(Token.TokenType.VOID);
                break;
            default:
                break;
        }
        return returnToken;  
    } 

    private static boolean isLetter(char c) {
        return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z');
    }
    
    private static boolean isDigit(char c) {
        return ('0' <= c && c <= '9');
    }

    private static boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\r';
    }

    private static boolean isPunctuation(char c) {
        return c == ';' || c == ',' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' 
            || c == '}' || c == '+' || c == '-' || c == '/' || c == '*' || c == '=' || c == '!' 
            || c == '>' || c == '<'; 
    }

    private static int getNextChar(BufferedReader inFile) throws IOException {
        inFile.mark(1);
        return inFile.read();
    }

    private static void unGetNextChar(BufferedReader inFile) throws IOException {
        inFile.reset();
    }
}
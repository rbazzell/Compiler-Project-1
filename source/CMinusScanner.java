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
        IN_COMMENT,
        
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
        char currChar;
        DFAState currState = DFAState.START;
        //DFA switch statement
        while (currState != DFAState.DONE) { 
            switch(currState) {
                case START:
                    if (viewNextChar(inFile) == -1) {
                        returnToken = new Token(Token.TokenType.EOF);
                        currState = DFAState.DONE;
                    }
                    currChar = (char) getNextChar(inFile);
                    if (Character.isSpaceChar(currChar)) {
                        continue;
                    } else if (isDigit(currChar)) {
                        currData += currChar;
                        currState = DFAState.IN_NUM;
                    } else if (isLetter(currChar)) {
                        currData += currChar;
                        currState = DFAState.IN_ID;
                    } else {
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
                                if (viewNextChar(inFile) == '*') {
                                    currChar = (char) getNextChar(inFile);
                                    currState = DFAState.IN_COMMENT;
                                } else {
                                    returnToken = new Token(Token.TokenType.DIV);
                                    currState = DFAState.DONE;
                                }
                                break;
                            case '<':
                                if (viewNextChar(inFile) == '=') {
                                    currChar = (char) getNextChar(inFile);
                                    returnToken = new Token(Token.TokenType.LTE);
                                    currState = DFAState.DONE;
                                } else {
                                    returnToken = new Token(Token.TokenType.LT);
                                    currState = DFAState.DONE;
                                }
                                break;
                            case '>':
                                if (viewNextChar(inFile) == '=') {
                                    currChar = (char) getNextChar(inFile);
                                    returnToken = new Token(Token.TokenType.GTE);
                                    currState = DFAState.DONE;
                                } else {
                                        returnToken = new Token(Token.TokenType.GT);
                                    currState = DFAState.DONE;
                                }
                                break;
                            case '=':
                                if (viewNextChar(inFile) == '=') {
                                    currChar = (char) getNextChar(inFile);
                                    returnToken = new Token(Token.TokenType.EQ);
                                    currState = DFAState.DONE;
                                } else {
                                    returnToken = new Token(Token.TokenType.ASSIGN);
                                    currState = DFAState.DONE;
                                }
                                break;
                            default:
                                returnToken = new Token(Token.TokenType.ERR);
                                currState = DFAState.DONE;                                
                                break;
                            }
                        }
                    break;
                case IN_ID:
                    currChar = (char) viewNextChar(inFile);
                    if (Character.isSpaceChar(currChar) || currChar == ',' || currChar == ';') {
                        returnToken = new Token(Token.TokenType.ID, currData);
                        currState = DFAState.DONE;
                    } else if (!isLetter(currChar)) {
                        returnToken = new Token(Token.TokenType.ERR);
                        currState = DFAState.DONE;
                    } else {
                        currChar = (char) getNextChar(inFile);
                        currData += currChar;
                    }
                    break;
                case IN_NUM:
                    currChar = (char) viewNextChar(inFile);
                    if (Character.isSpaceChar(currChar) || currChar == ',' || currChar == ';') {
                        returnToken = new Token(Token.TokenType.ID, currData);
                        currState = DFAState.DONE;
                    } else if (!isDigit(currChar)) {
                        returnToken = new Token(Token.TokenType.ERR);
                        currState = DFAState.DONE;
                    } else {
                        currChar = (char) getNextChar(inFile);
                        currData += currChar;
                    }
                    break;
                case IN_COMMENT:
                    if (viewNextChar(inFile) == -1) {
                        returnToken = new Token(Token.TokenType.EOF);
                        currState = DFAState.DONE;
                    }
                    currChar = (char) getNextChar(inFile);
                    char nextChar = (char) viewNextChar(inFile);
                    if ((currChar == '*') && (nextChar == '/')) {
                        currChar = (char) getNextChar(inFile);
                        currState = DFAState.START;
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
            case "else":
                returnToken = new Token(Token.TokenType.ELSE);
            case "while":
                returnToken = new Token(Token.TokenType.WHILE);
            case "return":
                returnToken = new Token(Token.TokenType.RETURN);
            case "int":
                returnToken = new Token(Token.TokenType.INT);
            case "void":
                returnToken = new Token(Token.TokenType.VOID);
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

    private static int getNextChar(BufferedReader inFile) throws IOException {
        return inFile.read();
    }

    private static int viewNextChar(BufferedReader inFile) throws IOException{
        int returnChar;
        inFile.mark(1);
        returnChar = inFile.read();
        inFile.reset();
        return returnChar;
    }
}

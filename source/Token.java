package source;
public class Token {
    public enum TokenType {
        ID,            //VARS & LITERALS
        NUM, 

        IF,            //KEYWORDS
        ELSE,
        WHILE,
        RETURN,
        INT,
        VOID,
        
        SEMI,          //SPECIAL CHARACTERS
        COMMA,
        L_BRACK,
        R_BRACK,
        L_PAREN,
        R_PAREN,
        L_CURLY,
        R_CURLY,
        
        ASSIGN,        //OPERATORS
        GT,
        GTE,
        LT,
        LTE,
        EQ,
        NEQ,
        PLUS,
        MINUS,
        MULT,
        DIV,

        EOF,            //FILE TOKENS
        ERR
    }

    public TokenType type;
    public Object data;

    public Token(TokenType tokenType){
        this(tokenType,null);
    }
    
    public Token(TokenType tokenType, Object tokenData){
        type = tokenType;
        data = tokenData;
    }
}

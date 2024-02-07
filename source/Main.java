package source;

class Main {
    public static void main(String[] args) throws Exception {
        CMinusScanner scanner = new CMinusScanner("code/test_all.cm");
        Token curr = new Token(null);
        while (curr.type != Token.TokenType.EOF) {
            curr = scanner.getNextToken();
            System.out.println(curr.type + "   |   " + curr.data);
        }
    }

}

//Yes debug make me :)
public class Parser
{
    public static final int OP         = 10;    // +  -  *  /
    public static final int RELOP      = 11;    // <  >  <=  >=  ...
    public static final int LPAREN     = 12;    // (
    public static final int RPAREN     = 13;    // )
    public static final int SEMI       = 14;    // ;
    public static final int COMMA      = 15;    // ,
    public static final int INT        = 16;    // int
    public static final int NUM        = 17;    // number
    public static final int ID         = 18;    // identifier
    public static final int PRINT      = 19;    // print
    public static final int FUNC = 20; // func
    public static final int VOID = 21; // void
    public static final int VAR = 22; // var
    public static final int IF = 23; // if
    public static final int BEGIN = 24; // begin
    public static final int END = 25; // end
    public static final int WHILE = 26; // while
    public static final int THEN = 27; // then
    public static final int ELSE = 28; // else
    public static final int TYPEOF = 29; // ::
    public static final int ASSIGN = 30; // :=

    Compiler         compiler;
    Lexer            lexer;     // lexer.yylex() returns token-name
    public ParserVal yylval;    // yylval contains token-attribute

    public Parser(java.io.Reader r, Compiler compiler) throws Exception
    {
        this.compiler = compiler;
        this.lexer    = new Lexer(r, this);
    }

    // 1. parser call lexer.yylex that should return (token-name, token-attribute)
    // 2. lexer
    //    a. assign token-attribute to yyparser.yylval
    //       token attribute can be lexeme, line number, colume, etc.
    //    b. return token-id defined in Parser as a token-name
    // 3. parser print the token on console
    //    if there was an error (-1) in lexer, then print error message
    // 4. repeat until EOF (0) is reached
    public int yyparse() throws Exception
    {


        int count = 0;
        while ( true )
        {
            //System.out.println("Parse Count: " + ++count);
            int token = lexer.yylex();  // get next token-name
            Object attr = yylval.obj;   // get      token-attribute
            String tokenname = "";
            switch(token) {
                case 0:
                    System.out.println("Success!");
                    return 0;
                case -1:
                    System.out.println("Error! There is a lexical error at " + lexer.lineno + ":" + lexer.column + ".");
                    return -1;
                case 10: tokenname = "OP"; break;
                case 11: tokenname = "RELOP"; break;
                case 12: tokenname = "LPAREN"; break;
                case 13: tokenname = "RPAREN"; break;
                case 14: tokenname = "SEMI"; break;
                case 15: tokenname = "Comma"; break;
                case 16: tokenname = "INT"; break;
                case 17: tokenname = "NUM"; break;
                case 18: tokenname = "ID"; break;
                case 19: tokenname = "PRINT"; break;
                case 20: tokenname = "FUNC"; break;
                case 21: tokenname = "VOID"; break;
                case 22: tokenname = "VAR"; break;
                case 23: tokenname = "IF"; break;
                case 24: tokenname = "BEGIN"; break;
                case 25: tokenname = "END"; break;
                case 26: tokenname = "WHILE"; break;
                case 27: tokenname = "THEN"; break;
                case 28: tokenname = "ELSE"; break;
                case 29: tokenname = "TYPEOF"; break;
                case 30: tokenname = "ASSIGN"; break;
            }

            System.out.println("<" + tokenname + ", token-attr:\"" + attr + "\", " + lexer.lineno + ":" + lexer.column + ">");
        }
    }
}

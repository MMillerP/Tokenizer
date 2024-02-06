import javax.swing.text.html.parser.ParserDelegator;

import static java.lang.Thread.sleep;

public class Lexer
{
    private static final char EOF        =  0;

    private Parser         yyparser; // parent parser object
    private java.io.Reader reader;   // input stream
    public int             lineno;   // line number
    public int             column;   // column
    public int             tempColumn; // temporary column number to save end of token loc while column is sent for parser
    public int             next;
    public char            c;


    public Lexer(java.io.Reader reader, Parser yyparser) throws Exception
    {
        this.reader   = reader;
        this.yyparser = yyparser;
        lineno = 1;
        column = 0;
        tempColumn = column;
        next = 0;
        c = Character.MIN_VALUE;
    }

    public char NextChar() throws Exception
    {
        // http://tutorials.jenkov.com/java-io/readers-writers.html
        int data = reader.read();
        if(data == -1)
        {
            return EOF;
        }
        return (char)data;
    }
    public int Fail()
    {
        return -1;
    }

    // * If yylex reach to the end of file, return  0
    // * If there is an lexical error found, return -1
    // * If a proper lexeme is determined, return token <token-id, token-attribute> as follows:
    //   1. set token-attribute into yyparser.yylval
    //   2. return token-id defined in Parser
    //   token attribute can be lexeme, line number, colume, etc.
    public int yylex() throws Exception
    {
        int state = 0;
        int count = 0;

        // if next == 1, c does not iterate forward, if next == 0, c iterates forward
        if(next==1){
            next = 0;
        }
        else{
            c = NextChar();
        }

        column = column + 1;
        if (tempColumn > column) {
            column = tempColumn;
            tempColumn = 0;
        }

        while(true)
        {
            //System.out.println("Lexer Count: " + ++count);
            //sleep(1000);
            switch(c)
            {
                case ' ':
                    c = NextChar(); column = column + 1; tempColumn = tempColumn + 1; continue; // skip whitespaces
                case ';':
                    yyparser.yylval = new ParserVal((Object)";");   // set token-attribute to yyparser.yylval
                    return Parser.SEMI;                             // return token-name
                case '(':
                    yyparser.yylval = new ParserVal((Object)"(");
                    return Parser.LPAREN;
                case ')':
                    yyparser.yylval = new ParserVal((Object)")");
                    return Parser.RPAREN;
                case ':': //checks all cases where tokens start with ':'
                    c = NextChar();
                    tempColumn = column + 2;
                    if(c == '='){
                        yyparser.yylval = new ParserVal((Object)":=");
                        return Parser.ASSIGN;
                    }
                    else if(c == ':'){
                        yyparser.yylval = new ParserVal((Object)"::");
                        return Parser.TYPEOF;
                    }
                    return Fail();
                case '+':
                    yyparser.yylval = new ParserVal((Object)"+");
                    return Parser.OP;
                case '-':
                    yyparser.yylval = new ParserVal((Object)"-");
                    return Parser.OP;
                case '*':
                    yyparser.yylval = new ParserVal((Object)"*");
                    return Parser.OP;
                case '/':
                    yyparser.yylval = new ParserVal((Object) "/");
                    return Parser.OP;
                case ',':
                    yyparser.yylval = new ParserVal((Object) ",");
                    return Parser.COMMA;
                case '=':
                    yyparser.yylval = new ParserVal((Object)"=");
                    return Parser.RELOP;
                case 9:
                    c = NextChar(); column = column + 1; tempColumn = tempColumn + 1; continue;
                case '>':
                    c = NextChar();
                    if(c == '=') {
                        tempColumn = column + 2;
                        yyparser.yylval = new ParserVal((Object)">=");
                        return Parser.RELOP;
                    }
                    else{
                        tempColumn = column + 1;
                        next = 1;
                        yyparser.yylval = new ParserVal((Object) ">");
                        return Parser.RELOP;
                    }
                case '<':
                    c = NextChar();
                    if(c == '=') {
                        tempColumn = column + 2;
                        yyparser.yylval = new ParserVal((Object)"<=");
                        return Parser.RELOP;
                    }
                    else if(c== '>'){
                        tempColumn = column + 2;
                        yyparser.yylval = new ParserVal((Object)"<>");
                        return Parser.RELOP;
                    }
                    else{
                        tempColumn = column + 1;
                        next = 1;
                        yyparser.yylval = new ParserVal((Object) "<");
                        return Parser.RELOP;
                    }
                case '.':
                    return Fail();
                case '_':
                    return Fail();
                default:
                    if(Character.isLetter(c)){
                        String keyword = "";
                        tempColumn = column;
                        while(Character.isLetter(c) || c=='_' || Character.isDigit(c)){
                            keyword = keyword + c;
                            c = NextChar();
                            tempColumn = tempColumn + 1;
                        }
                        next = 1;
                        switch(keyword){
                            default:
                                yyparser.yylval = new ParserVal((Object) keyword);
                                return Parser.ID;
                            case "int":
                                yyparser.yylval = new ParserVal((Object) "int");
                                return Parser.INT;
                            case "print":
                                yyparser.yylval = new ParserVal((Object) "print");
                                return Parser.PRINT;
                            case "var":
                                yyparser.yylval = new ParserVal((Object) "var");
                                return Parser.VAR;
                            case "func":
                                yyparser.yylval = new ParserVal((Object) "func");
                                return Parser.FUNC;
                            case "if":
                                yyparser.yylval = new ParserVal((Object) "if");
                                return Parser.IF;
                            case "then":
                                yyparser.yylval = new ParserVal((Object) "then");
                                return Parser.THEN;
                            case "else":
                                yyparser.yylval = new ParserVal((Object) "else");
                                return Parser.ELSE;
                            case "while":
                                yyparser.yylval = new ParserVal((Object) "while");
                                return Parser.WHILE;
                            case "void":
                                yyparser.yylval = new ParserVal((Object) "void");
                                return Parser.VOID;
                            case "begin":
                                yyparser.yylval = new ParserVal((Object) "begin");
                                return Parser.BEGIN;
                            case "end":
                                yyparser.yylval = new ParserVal((Object) "end");
                                return Parser.END;
                        }
                    }
                    else if(Character.isDigit(c)){
                        int dot = 0; // if dot is zero, a decimal may be used, if dot is one, a decimal may not be used
                        String keyword = "";

                        tempColumn = column;
                        while(c=='.' || Character.isDigit(c)){
                            if(dot == 0 && c == '.'){
                                dot = 1;
                            }
                            else if(dot == 1 && c == '.'){
                                next = 1;
                                yyparser.yylval = new ParserVal((Object) keyword);
                                return Parser.NUM;
                            }
                            tempColumn = tempColumn + 1;
                            keyword = keyword + c;
                            c = NextChar();
                        }
                        next = 1;
                        yyparser.yylval = new ParserVal((Object) keyword);
                        return Parser.NUM;
                    }
                    column = 1;
                    tempColumn = 1;
                    lineno = lineno + 1;
                    c = NextChar();
                    continue;
                case EOF:
                    return EOF;                                     // return end-of-file symbol
            }
        }
    }
}

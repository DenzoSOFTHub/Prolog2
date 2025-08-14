package it.denzosoft.prolog.parser;

public class Token {
    public enum Type { 
        ATOM, VARIABLE, NUMBER, LPAREN, RPAREN, LBRACKET, RBRACKET, 
        BAR, COMMA, DOT, OPERATOR, EOF, COMMENT, STRING, SEMICOLON,
        FUNCTOR, DEFINE, COLON_DASH, EQ, GT, LT, GTE, LTE, 
        PLUS, MINUS, MULTIPLY, DIVIDE, BACKSLASH, CARET, 
        AT, UNDERSCORE, ERROR
    }
    
    private final Type type;
    private final String value;
    private final int line;
    private final int column;
    
    public Token(Type type, String value) {
        this(type, value, 0, 0);
    }
    
    public Token(Type type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return String.format("%s('%s') line:%d col:%d", type, value, line, column);
    }
}

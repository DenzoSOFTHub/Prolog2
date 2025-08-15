package it.denzosoft.prolog.parser;

public class Token {
    public enum Type { 
        ATOM, VARIABLE, NUMBER, LPAREN, RPAREN, LBRACKET, RBRACKET, 
        BAR, COMMA, DOT, SEMICOLON, EOF, ERROR, STRING,
        DEFINE, EQ, GT, LT, GTE, LTE, EQEQ, NEQ,
        PLUS, MINUS, MULTIPLY, DIVIDE, BACKSLASH, CARET, 
        CUT, COLON, IS
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
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Token)) return false;
        Token other = (Token) obj;
        return type == other.type && value.equals(other.value);
    }
    
    @Override
    public int hashCode() {
        return type.hashCode() * 31 + value.hashCode();
    }
}

package it.denzosoft.prolog.parser;

import java.io.IOException;
import java.io.Reader;


public class Lexer {
    private final Reader reader;
    private int currentChar;
    private int position = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(Reader reader) throws IOException {
        this.reader = reader;
        this.currentChar = reader.read();
    }

    /**
     * Returns the next token from the input.
     */
    public Token nextToken() throws IOException {
        skipWhitespace();
        
        if (currentChar == -1) {
            return new Token(Token.Type.EOF, "", line, column);
        }
        
        Token token = readToken();
        return token != null ? token : new Token(Token.Type.ERROR, String.valueOf((char)currentChar), line, column);
    }
    
    private void skipWhitespace() throws IOException {
        while (currentChar != -1 && Character.isWhitespace(currentChar)) {
            if (currentChar == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            currentChar = reader.read();
        }
    }
    
    private Token readToken() throws IOException {
        int startLine = line;
        int startColumn = column;
        
        // Handle numbers
        if (Character.isDigit(currentChar)) {
            StringBuilder sb = new StringBuilder();
            while (currentChar != -1 && (Character.isDigit(currentChar) || currentChar == '.')) {
                sb.append((char) currentChar);
                currentChar = reader.read();
                column++;
            }
            return new Token(Token.Type.NUMBER, sb.toString(), startLine, startColumn);
        }
        
        // Handle atoms, variables, and operators
        if (Character.isLetter(currentChar) || currentChar == '_' || currentChar == '\'') {
            StringBuilder sb = new StringBuilder();
            boolean isQuoted = currentChar == '\'';
            
            if (isQuoted) {
                currentChar = reader.read();
                column++;
                while (currentChar != -1 && currentChar != '\'') {
                    sb.append((char) currentChar);
                    currentChar = reader.read();
                    column++;
                }
                if (currentChar == '\'') {
                    currentChar = reader.read();
                    column++;
                    return new Token(Token.Type.ATOM, sb.toString(), startLine, startColumn);
                } else {
                    return new Token(Token.Type.ERROR, "Unterminated quoted atom", startLine, startColumn);
                }
            }
            
            while (currentChar != -1 && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
                sb.append((char) currentChar);
                currentChar = reader.read();
                column++;
            }
            
            String value = sb.toString();
            
            // Check if it's a variable (starts with uppercase) or atom
            if (Character.isUpperCase(value.charAt(0))) {
                return new Token(Token.Type.VARIABLE, value, startLine, startColumn);
            } else {
                return new Token(Token.Type.ATOM, value, startLine, startColumn);
            }
        }
        
        // Handle operators and special characters
        char ch = (char) currentChar;
        currentChar = reader.read();
        column++;
        
        switch (ch) {
            case '(': return new Token(Token.Type.LPAREN, "(", startLine, startColumn);
            case ')': return new Token(Token.Type.RPAREN, ")", startLine, startColumn);
            case '[': return new Token(Token.Type.LBRACKET, "[", startLine, startColumn);
            case ']': return new Token(Token.Type.RBRACKET, "]", startLine, startColumn);
            case '|': return new Token(Token.Type.BAR, "|", startLine, startColumn);
            case ',': return new Token(Token.Type.COMMA, ",", startLine, startColumn);
            case '.': return new Token(Token.Type.DOT, ".", startLine, startColumn);
            case ';': return new Token(Token.Type.SEMICOLON, ";", startLine, startColumn);
            case '+': return new Token(Token.Type.PLUS, "+", startLine, startColumn);
            case '-': return new Token(Token.Type.MINUS, "-", startLine, startColumn);
            case '*': return new Token(Token.Type.MULTIPLY, "*", startLine, startColumn);
            case '/': return new Token(Token.Type.DIVIDE, "/", startLine, startColumn);
            case '^': return new Token(Token.Type.CARET, "^", startLine, startColumn);
            case '\\': return new Token(Token.Type.BACKSLASH, "\\", startLine, startColumn);
            case '=': 
                if (currentChar == '<') {
                    currentChar = reader.read();
                    column++;
                    return new Token(Token.Type.LTE, "=<", startLine, startColumn);
                }
                return new Token(Token.Type.EQ, "=", startLine, startColumn);
            case '>': 
                if (currentChar == '=') {
                    currentChar = reader.read();
                    column++;
                    return new Token(Token.Type.GTE, ">=", startLine, startColumn);
                }
                return new Token(Token.Type.GT, ">", startLine, startColumn);
            case '<':
                return new Token(Token.Type.LT, "<", startLine, startColumn);
            case ':':
                if (currentChar == '-') {
                    currentChar = reader.read();
                    column++;
                    return new Token(Token.Type.DEFINE, ":-", startLine, startColumn);
                }
                return new Token(Token.Type.ERROR, ":", startLine, startColumn);
            default: 
                return new Token(Token.Type.ERROR, String.valueOf(ch), startLine, startColumn);
        }
    }
}

package it.denzosoft.prolog.parser;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;


public class LexerTest {
    
    @Test
    public void testAtomToken() throws IOException {
        Lexer lexer = new Lexer(new StringReader("hello"));
        Token token = lexer.nextToken();
        assertEquals(Token.Type.ATOM, token.getType());
        assertEquals("hello", token.getValue());
    }
    
    @Test
    public void testVariableToken() throws IOException {
        Lexer lexer = new Lexer(new StringReader("X"));
        Token token = lexer.nextToken();
        assertEquals(Token.Type.VARIABLE, token.getType());
        assertEquals("X", token.getValue());
    }
    
    @Test
    public void testNumberToken() throws IOException {
        Lexer lexer = new Lexer(new StringReader("42"));
        Token token = lexer.nextToken();
        assertEquals(Token.Type.NUMBER, token.getType());
        assertEquals("42", token.getValue());
    }
    
    @Test
    public void testDotToken() throws IOException {
        Lexer lexer = new Lexer(new StringReader("."));
        Token token = lexer.nextToken();
        assertEquals(Token.Type.DOT, token.getType());
        assertEquals(".", token.getValue());
    }
    
    @Test
    public void testDefineToken() throws IOException {
        Lexer lexer = new Lexer(new StringReader(":-"));
        Token token = lexer.nextToken();
        assertEquals(Token.Type.DEFINE, token.getType());
        assertEquals(":-", token.getValue());
    }
    
    @Test
    public void testQuotedAtom() throws IOException {
        Lexer lexer = new Lexer(new StringReader("'hello world'"));
        Token token = lexer.nextToken();
        assertEquals(Token.Type.ATOM, token.getType());
        assertEquals("hello world", token.getValue());
    }
    
    @Test
    public void testListTokens() throws IOException {
        Lexer lexer = new Lexer(new StringReader("[1,2,3]"));
        Token token = lexer.nextToken();
        assertEquals(Token.Type.LBRACKET, token.getType());
        token = lexer.nextToken();
        assertEquals(Token.Type.NUMBER, token.getType());
        assertEquals("1", token.getValue());
    }
}

package it.denzosoft.prolog.parser;

import org.junit.Test;
import static org.junit.Assert.*;

import it.denzosoft.prolog.model.*;
import java.io.IOException;
import java.io.StringReader;


public class ParserTest {
    
    @Test
    public void testParseAtom() throws IOException {
        Term term = Parser.parseTermFromString("hello");
        assertTrue(term instanceof Atom);
        assertEquals("hello", ((Atom)term).getValue());
    }
    
    @Test
    public void testParseVariable() throws IOException {
        Term term = Parser.parseTermFromString("X");
        assertTrue(term instanceof Variable);
        assertEquals("X", ((Variable)term).getName());
    }
    
    @Test
    public void testParseNumber() throws IOException {
        Term term = Parser.parseTermFromString("42");
        assertTrue(term instanceof Number);
        assertEquals(42.0, ((Number)term).getValue(), 0.001);
    }
    
    @Test
    public void testParseStructure() throws IOException {
        Term term = Parser.parseTermFromString("parent(tom, bob)");
        assertTrue(term instanceof Struct);
        Struct struct = (Struct) term;
        assertEquals("parent", struct.getFunctor().getValue());
        assertEquals(2, struct.getArity());
    }
    
    @Test
    public void testParseList() throws IOException {
        Term term = Parser.parseTermFromString("[1,2,3]");
        assertTrue(term instanceof List);
    }
    
    @Test
    public void testParseFact() throws IOException {
        Clause clause = Parser.parseClauseFromString("likes(mary, food).");
        assertNotNull(clause);
        assertNotNull(clause.getHead());
        assertNull(clause.getBody());
    }
    
    @Test
    public void testParseRule() throws IOException {
        Clause clause = Parser.parseClauseFromString("grandparent(X, Z) :- parent(X, Y), parent(Y, Z).");
        assertNotNull(clause);
        assertNotNull(clause.getHead());
        assertNotNull(clause.getBody());
    }
}

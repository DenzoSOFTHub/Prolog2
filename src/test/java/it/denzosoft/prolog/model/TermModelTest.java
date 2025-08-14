package it.denzosoft.prolog.model;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;


public class TermModelTest {
    
    @Test
    public void testAtom() {
        Atom atom = new Atom("hello");
        assertEquals("hello", atom.getValue());
        assertEquals(Term.Type.ATOM, atom.getType());
        assertFalse(atom.containsUnboundVariables());
        assertSame(atom, atom.copy(new HashMap<>()));
    }
    
    @Test
    public void testNumber() {
        Number number = new Number(42);
        assertEquals(42.0, number.getValue(), 0.001);
        assertEquals(Term.Type.NUMBER, number.getType());
        assertFalse(number.containsUnboundVariables());
        assertSame(number, number.copy(new HashMap<>()));
    }
    
    @Test
    public void testVariable() {
        Variable var = new Variable("X");
        assertEquals("X", var.getName());
        assertEquals(Term.Type.VARIABLE, var.getType());
        assertTrue(var.containsUnboundVariables());
        assertFalse(var.isBound());
        assertNull(var.getBinding());
    }
    
    @Test
    public void testVariableBinding() {
        Variable var = new Variable("X");
        Atom value = new Atom("hello");
        var.bind("X", value);
        assertTrue(var.isBound());
        assertEquals(value, var.getBinding());
        assertEquals(value, var.copy(new HashMap<>()));
    }
    
    @Test
    public void testStructure() throws Exception {
        Atom functor = new Atom("f");
        Term[] args = {new Atom("a"), new Variable("X")};
        Struct struct = new Struct(functor, args);
        
        assertEquals(functor, struct.getFunctor());
        assertEquals(2, struct.getArity());
        assertEquals(Term.Type.STRUCTURE, struct.getType());
        assertTrue(struct.containsUnboundVariables());
        
        Map<String, Variable> context = new HashMap<>();
        Term copy = struct.copy(context);
        assertNotSame(struct, copy);
        assertTrue(copy instanceof Struct);
    }
    
    @Test
    public void testList() {
        Atom empty = new Atom("[]");
        Term head = new Atom("a");
        List list = new List(head, empty);
        
        assertEquals(head, list.getHead());
        assertEquals(empty, list.getTail());
        assertEquals(Term.Type.LIST, list.getType());
        assertTrue(list.isEmpty() == false); // This list has one element
        
        Map<String, Variable> context = new HashMap<>();
        Term copy = list.copy(context);
        assertNotSame(list, copy);
        assertTrue(copy instanceof List);
    }
}

package it.denzosoft.prolog.engine;

import org.junit.Test;
import static org.junit.Assert.*;

import it.denzosoft.prolog.model.*;
import java.util.HashMap;
import java.util.Map;


public class UnifierTest {
    
    @Test
    public void testUnifyAtoms() {
        Atom a = new Atom("hello");
        Atom b = new Atom("hello");
        Trail trail = new Trail();
        Map<String, Term> substitution = new HashMap<>();
        assertTrue(Unifier.unify(a, b, substitution, trail));
    }
    
    @Test
    public void testUnifyDifferentAtoms() {
        Atom a = new Atom("hello");
        Atom b = new Atom("world");
        Trail trail = new Trail();
        Map<String, Term> substitution = new HashMap<>();
        assertFalse(Unifier.unify(a, b, substitution, trail));
    }
    
    @Test
    public void testUnifyVariableWithAtom() {
        Variable var = new Variable("X");
        Atom atom = new Atom("hello");
        Trail trail = new Trail();
        Map<String, Term> substitution = new HashMap<>();
        assertTrue(Unifier.unify(var, atom, substitution, trail));
        assertEquals(atom, substitution.get("X"));
    }
    
    @Test
    public void testUnifyNumbers() {
        Number a = new Number(42);
        Number b = new Number(42);
        Trail trail = new Trail();
        Map<String, Term> substitution = new HashMap<>();
        assertTrue(Unifier.unify(a, b, substitution, trail));
    }
    
    @Test
    public void testUnifyStructures() throws Exception {
        Term a = Parser.parseTermFromString("f(X, a)");
        Term b = Parser.parseTermFromString("f(b, Y)");
        Trail trail = new Trail();
        Map<String, Term> substitution = new HashMap<>();
        assertTrue(Unifier.unify(a, b, substitution, trail));
        assertEquals(new Atom("b"), substitution.get("X"));
        assertEquals(new Atom("a"), substitution.get("Y"));
    }
}

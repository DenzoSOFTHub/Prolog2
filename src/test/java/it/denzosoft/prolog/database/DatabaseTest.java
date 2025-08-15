package it.denzosoft.prolog.database;

import org.junit.Test;
import static org.junit.Assert.*;

import it.denzosoft.prolog.model.*;
import java.util.List;



public class DatabaseTest {
    
    @Test
    public void testAddAndFindClause() throws Exception {
        Database db = new Database();
        Atom head = new Atom("a");
        Clause clause = new Clause(head, null);
        db.addClause(clause);
        
        List<Clause> clauses = db.findClauses(head);
        assertEquals(1, clauses.size());
        assertEquals(clause, clauses.get(0));
    }
    
    @Test
    public void testRemoveClause() throws Exception {
        Database db = new Database();
        Atom head = new Atom("a");
        Clause clause = new Clause(head, null);
        db.addClause(clause);
        
        assertTrue(db.removeClause(clause));
        assertEquals(0, db.getClauses().size());
        // Also check that findClauses returns empty
        List<Clause> clauses = db.findClauses(head);
        assertEquals(0, clauses.size());
    }
    
    @Test
    public void testClear() throws Exception {
        Database db = new Database();
        db.addClause(new Clause(new Atom("a"), null));
        db.addDynamicClause(new Clause(new Atom("b"), null));
        
        db.clear();
        assertEquals(0, db.getClauses().size());
        assertEquals(0, db.getDynamicClauses().size());
        // Check that index is also cleared
        List<Clause> clauses = db.findClauses(new Atom("a"));
        assertEquals(0, clauses.size());
    }
}

package it.denzosoft.prolog.query;

import org.junit.Test;
import static org.junit.Assert.*;

import it.denzosoft.prolog.database.Database;
import it.denzosoft.prolog.model.*;
import it.denzosoft.prolog.parser.Parser;
import java.util.List;
import java.util.Map;





public class QueryTest {
    
    @Test
    public void testSimpleFactQuery() throws Exception {
        Database db = new Database();
        Clause fact = Parser.parseClauseFromString("likes(mary, food).");
        db.addClause(fact);
        
        Term goal = Parser.parseTermFromString("likes(mary, food)");
        Query query = new Query(goal, db);
        List<Map<String, Term>> solutions = query.solve();
        
        assertEquals(1, solutions.size());
    }
    
    @Test
    public void testVariableQuery() throws Exception {
        Database db = new Database();
        db.addClause(Parser.parseClauseFromString("likes(mary, food)."));
        db.addClause(Parser.parseClauseFromString("likes(mary, wine)."));
        
        Term goal = Parser.parseTermFromString("likes(mary, X)");
        Query query = new Query(goal, db);
        List<Map<String, Term>> solutions = query.solve();
        
        assertEquals(2, solutions.size());
        assertNotNull(solutions.get(0).get("X"));
        assertNotNull(solutions.get(1).get("X"));
    }
    
    @Test
    public void testRuleQuery() throws Exception {
        Database db = new Database();
        // Add facts
        db.addClause(Parser.parseClauseFromString("parent(tom, bob)."));
        db.addClause(Parser.parseClauseFromString("parent(bob, pat)."));
        
        // Add rule
        db.addClause(Parser.parseClauseFromString("grandparent(X, Z) :- parent(X, Y), parent(Y, Z)."));
        
        Term goal = Parser.parseTermFromString("grandparent(tom, pat)");
        Query query = new Query(goal, db);
        List<Map<String, Term>> solutions = query.solve();
        
        assertEquals(1, solutions.size());
    }
    
    @Test
    public void testArithmeticQuery() throws Exception {
        Database db = new Database();
        Term goal = Parser.parseTermFromString("X is 2 + 3");
        Query query = new Query(goal, db);
        List<Map<String, Term>> solutions = query.solve();
        
        assertEquals(1, solutions.size());
        Term xValue = solutions.get(0).get("X");
        assertTrue(xValue instanceof it.denzosoft.prolog.model.Number);
        assertEquals(5.0, ((it.denzosoft.prolog.model.Number)xValue).getValue(), 0.001);
    }
}

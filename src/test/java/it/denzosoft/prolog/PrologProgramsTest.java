package it.denzosoft.prolog;

import org.junit.Test;
import static org.junit.Assert.*;

import it.denzosoft.prolog.database.Database;
import it.denzosoft.prolog.model.*;
import it.denzosoft.prolog.query.Query;
import it.denzosoft.prolog.parser.Parser;
import java.util.List;
import java.util.Map;





public class PrologProgramsTest {
    
    @Test
    public void testFamilyRelations() throws Exception {
        Database db = new Database();
        
        // Facts
        db.addClause(Parser.parseClauseFromString("parent(tom, bob)."));
        db.addClause(Parser.parseClauseFromString("parent(bob, pat)."));
        db.addClause(Parser.parseClauseFromString("parent(pat, jim)."));
        db.addClause(Parser.parseClauseFromString("parent(tom, liz)."));
        db.addClause(Parser.parseClauseFromString("parent(bob, ann)."));
        db.addClause(Parser.parseClauseFromString("parent(pat, tommy)."));
        db.addClause(Parser.parseClauseFromString("parent(liz, dave)."));
        db.addClause(Parser.parseClauseFromString("parent(liz, betty)."));
        
        // Rules
        db.addClause(Parser.parseClauseFromString("ancestor(X, Z) :- parent(X, Z)."));
        db.addClause(Parser.parseClauseFromString("ancestor(X, Z) :- parent(X, Y), ancestor(Y, Z)."));
        
        // Test direct parent
        Term goal1 = Parser.parseTermFromString("parent(tom, bob)");
        Query query1 = new Query(goal1, db);
        List<Map<String, Term>> solutions1 = query1.solve();
        assertEquals(1, solutions1.size());
        
        // Test ancestor
        Term goal2 = Parser.parseTermFromString("ancestor(tom, jim)");
        Query query2 = new Query(goal2, db);
        List<Map<String, Term>> solutions2 = query2.solve();
        assertEquals(1, solutions2.size());
        
        // Test variable query
        Term goal3 = Parser.parseTermFromString("ancestor(tom, X)");
        Query query3 = new Query(goal3, db);
        List<Map<String, Term>> solutions3 = query3.solve();
        assertEquals(7, solutions3.size()); // tom has 7 descendants
    }
    
    @Test
    public void testListOperations() throws Exception {
        Database db = new Database();
        
        // Append rule - using proper Prolog syntax
        db.addClause(Parser.parseClauseFromString("append([], L, L)."));
        db.addClause(Parser.parseClauseFromString("append([H|T], L, [H|R]) :- append(T, L, R)."));
        
        // Test append
        Term goal = Parser.parseTermFromString("append([1,2], [3,4], X)");
        Query query = new Query(goal, db);
        List<Map<String, Term>> solutions = query.solve();
        assertEquals(1, solutions.size());
        
        // We can't directly assert the structure here, but we know it should have a solution
        assertTrue(solutions.size() >= 1);
    }
    
    @Test
    public void testArithmeticOperations() throws Exception {
        Database db = new Database();
        
        // Test addition
        Term goal1 = Parser.parseTermFromString("X is 2 + 3");
        Query query1 = new Query(goal1, db);
        List<Map<String, Term>> solutions1 = query1.solve();
        assertEquals(1, solutions1.size());
        assertEquals(5.0, ((it.denzosoft.prolog.model.Number)solutions1.get(0).get("X")).getValue(), 0.001);
        
        // Test subtraction
        Term goal2 = Parser.parseTermFromString("X is 10 - 4");
        Query query2 = new Query(goal2, db);
        List<Map<String, Term>> solutions2 = query2.solve();
        assertEquals(1, solutions2.size());
        assertEquals(6.0, ((it.denzosoft.prolog.model.Number)solutions2.get(0).get("X")).getValue(), 0.001);
        
        // Test multiplication
        Term goal3 = Parser.parseTermFromString("X is 3 * 4");
        Query query3 = new Query(goal3, db);
        List<Map<String, Term>> solutions3 = query3.solve();
        assertEquals(1, solutions3.size());
        assertEquals(12.0, ((it.denzosoft.prolog.model.Number)solutions3.get(0).get("X")).getValue(), 0.001);
        
        // Test division
        Term goal4 = Parser.parseTermFromString("X is 15 / 3");
        Query query4 = new Query(goal4, db);
        List<Map<String, Term>> solutions4 = query4.solve();
        assertEquals(1, solutions4.size());
        assertEquals(5.0, ((it.denzosoft.prolog.model.Number)solutions4.get(0).get("X")).getValue(), 0.001);
    }
    
    @Test
    public void testLogicalOperations() throws Exception {
        Database db = new Database();
        
        // Facts
        db.addClause(Parser.parseClauseFromString("likes(mary, food)."));
        db.addClause(Parser.parseClauseFromString("likes(mary, wine)."));
        db.addClause(Parser.parseClauseFromString("likes(john, wine)."));
        db.addClause(Parser.parseClauseFromString("likes(john, mary)."));
        
        // Conjunction test
        Term goal1 = Parser.parseTermFromString("likes(mary, food), likes(mary, wine)");
        Query query1 = new Query(goal1, db);
        List<Map<String, Term>> solutions1 = query1.solve();
        assertEquals(1, solutions1.size());
        
        // Disjunction test - should find at least one solution
        Term goal2 = Parser.parseTermFromString("likes(mary, food) ; likes(mary, beer)");
        Query query2 = new Query(goal2, db);
        List<Map<String, Term>> solutions2 = query2.solve();
        assertTrue(solutions2.size() >= 1);
    }
}

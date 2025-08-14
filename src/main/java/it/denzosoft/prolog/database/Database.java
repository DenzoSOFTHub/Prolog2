package it.denzosoft.prolog.database;

import it.denzosoft.prolog.model.*;
import java.util.ArrayList;
import java.util.List;


public class Database {
    private final List<Clause> clauses = new ArrayList<>();
    private final List<Clause> dynamicClauses = new ArrayList<>();
    
    /**
     * Adds a clause to the database.
     */
    public void addClause(Clause clause) {
        clauses.add(clause);
    }
    
    /**
     * Adds a dynamic clause.
     */
    public void addDynamicClause(Clause clause) {
        dynamicClauses.add(clause);
    }
    
    /**
     * Removes a clause from the database.
     * @return true if clause was removed
     */
    public boolean removeClause(Clause clause) {
        return clauses.remove(clause);
    }
    
    /**
     * Finds clauses that match the given term.
     */
    public List<Clause> findClauses(Term term) {
        List<Clause> matches = new ArrayList<>();
        
        // Search in static clauses
        for (Clause clause : clauses) {
            if (clause.getHead().equals(term)) {
                matches.add(clause);
            }
        }
        
        // Search in dynamic clauses
        for (Clause clause : dynamicClauses) {
            if (clause.getHead().equals(term)) {
                matches.add(clause);
            }
        }
        
        return matches;
    }
    
    /**
     * Clears all clauses.
     */
    public void clear() {
        clauses.clear();
        dynamicClauses.clear();
    }
    
    /**
     * Gets all static clauses.
     */
    public List<Clause> getClauses() {
        return new ArrayList<>(clauses);
    }
    
    /**
     * Gets all dynamic clauses.
     */
    public List<Clause> getDynamicClauses() {
        return new ArrayList<>(dynamicClauses);
    }
}

package it.denzosoft.prolog.database;

import it.denzosoft.prolog.model.*;
import java.util.*;





public class Database {
    private final java.util.List<Clause> clauses = new ArrayList<>();
    private final java.util.List<Clause> dynamicClauses = new ArrayList<>();
    private final Map<String, java.util.List<Clause>> index = new HashMap<>();
    
    /**
     * Adds a clause to the database.
     */
    public void addClause(Clause clause) {
        clauses.add(clause);
        indexClause(clause);
    }
    
    /**
     * Adds a dynamic clause.
     */
    public void addDynamicClause(Clause clause) {
        dynamicClauses.add(clause);
        indexClause(clause);
    }
    
    /**
     * Indexes a clause by its head functor for faster lookup.
     */
    private void indexClause(Clause clause) {
        Term head = clause.getHead();
        String key = getTermKey(head);
        index.computeIfAbsent(key, k -> new ArrayList<>()).add(clause);
    }
    
    /**
     * Generates a key for indexing based on the term's functor and arity.
     */
    private String getTermKey(Term term) {
        if (term instanceof Atom) {
            return ((Atom) term).getValue() + "/0";
        } else if (term instanceof Struct) {
            Struct struct = (Struct) term;
            return struct.getFunctor().getValue() + "/" + struct.getArity();
        }
        return term.getClass().getSimpleName();
    }
    
    /**
     * Removes a clause from the database.
     * @return true if clause was removed
     */
    public boolean removeClause(Clause clause) {
        boolean removed = clauses.remove(clause);
        if (removed) {
            removeFromIndex(clause);
        }
        return removed;
    }
    
    /**
     * Removes a clause from the index.
     */
    private void removeFromIndex(Clause clause) {
        Term head = clause.getHead();
        String key = getTermKey(head);
        java.util.List<Clause> clauseList = index.get(key);
        if (clauseList != null) {
            clauseList.remove(clause);
            if (clauseList.isEmpty()) {
                index.remove(key);
            }
        }
    }
    
    /**
     * Finds clauses that match the given term.
     */
    public java.util.List<Clause> findClauses(Term term) {
        String key = getTermKey(term);
        java.util.List<Clause> indexedClauses = index.get(key);
        
        if (indexedClauses != null) {
            return new ArrayList<>(indexedClauses);
        }
        
        // Fallback to linear search if not indexed
        java.util.List<Clause> matches = new ArrayList<>();
        
        // Search in static clauses
        for (Clause clause : clauses) {
            if (matchesHead(term, clause.getHead())) {
                matches.add(clause);
            }
        }
        
        // Search in dynamic clauses
        for (Clause clause : dynamicClauses) {
            if (matchesHead(term, clause.getHead())) {
                matches.add(clause);
            }
        }
        
        return matches;
    }
    
    /**
     * Checks if two terms have the same functor and arity (for indexing purposes).
     */
    private boolean matchesHead(Term query, Term head) {
        if (query instanceof Atom && head instanceof Atom) {
            return ((Atom) query).getValue().equals(((Atom) head).getValue());
        }
        
        if (query instanceof Struct && head instanceof Struct) {
            Struct queryStruct = (Struct) query;
            Struct headStruct = (Struct) head;
            return queryStruct.getFunctor().getValue().equals(headStruct.getFunctor().getValue()) &&
                   queryStruct.getArity() == headStruct.getArity();
        }
        
        return false;
    }
    
    /**
     * Clears all clauses.
     */
    public void clear() {
        clauses.clear();
        dynamicClauses.clear();
        index.clear();
    }
    
    /**
     * Gets all static clauses.
     */
    public java.util.List<Clause> getClauses() {
        return new ArrayList<>(clauses);
    }
    
    /**
     * Gets all dynamic clauses.
     */
    public java.util.List<Clause> getDynamicClauses() {
        return new ArrayList<>(dynamicClauses);
    }
}

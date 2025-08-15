package it.denzosoft.prolog.model;

public interface Term {
    /**
     * Creates a copy of the term with variables renamed.
     * @param context a map for variable renaming
     * @return a new copy of the term
     */
    Term copy(java.util.Map<String, Variable> context);
    
    /**
     * Binds a variable in the term to a value.
     * @param varName the variable name to bind
     * @param value the value to bind
     */
    void bind(String varName, Term value);
    
    /**
     * Checks if this term contains an unbound variable.
     * @return true if unbound variables exist
     */
    boolean containsUnboundVariables();
    
    /**
     * Represents the type of term.
     */
    enum Type { ATOM, NUMBER, VARIABLE, STRUCTURE, LIST }
    
    /**
     * Gets the type of this term.
     */
    Type getType();
}

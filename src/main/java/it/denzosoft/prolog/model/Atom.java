package it.denzosoft.prolog.model;

import java.util.Map;


public class Atom implements Term {
    private final String value;
    
    public Atom(String value) {
        this.value = value;
    }
    
    @Override
    public Term copy(Map<String, Variable> context) {
        return this;
    }
    
    @Override
    public void bind(String varName, Term value) {
        // Atoms cannot be bound
    }
    
    @Override
    public boolean containsUnboundVariables() {
        return false;
    }
    
    @Override
    public Type getType() {
        return Type.ATOM;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Atom)) return false;
        Atom other = (Atom) obj;
        return value.equals(other.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

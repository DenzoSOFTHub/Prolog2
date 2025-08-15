package it.denzosoft.prolog.model;

import java.util.Map;



public class Variable implements Term {
    private final String name;
    private Term boundTo = null;
    
    public Variable(String name) {
        this.name = name;
    }
    
    @Override
    public Term copy(Map<String, Variable> context) {
        if (boundTo != null) {
            return boundTo.copy(context);
        }
        
        // Create a new variable if not already in context
        if (context.containsKey(name)) {
            return context.get(name);
        }
        
        Variable newVar = new Variable(name);
        context.put(name, newVar);
        return newVar;
    }
    
    @Override
    public void bind(String varName, Term value) {
        if (name.equals(varName)) {
            this.boundTo = value;
        }
    }
    
    // Method to unbind a variable (for backtracking)
    public void unbind() {
        this.boundTo = null;
    }
    
    @Override
    public boolean containsUnboundVariables() {
        return boundTo == null;
    }
    
    @Override
    public Type getType() {
        return Type.VARIABLE;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isBound() {
        return boundTo != null;
    }
    
    public Term getBinding() {
        return boundTo;
    }
    
    @Override
    public String toString() {
        return isBound() ? getBinding().toString() : name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Variable)) return false;
        Variable other = (Variable) obj;
        return name.equals(other.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

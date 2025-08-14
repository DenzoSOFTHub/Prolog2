package it.denzosoft.prolog.engine;

import it.denzosoft.prolog.model.Variable;
import java.util.Stack;


public class Trail {
    private final Stack<Variable> trail = new Stack<>();
    
    /**
     * Pushes a variable onto the trail.
     */
    public void push(Variable var) {
        trail.push(var);
    }
    
    /**
     * Undo all bindings since the last mark.
     * @return the mark value for the current state
     */
    public int mark() {
        return trail.size();
    }
    
    /**
     * Undoes bindings up to the given mark.
     */
    public void undo(int mark) {
        while (trail.size() > mark) {
            Variable var = trail.pop();
            var.bind(var.getName(), null); // Unbind
        }
    }
    
    /**
     * Clears the trail.
     */
    public void clear() {
        trail.clear();
    }
}

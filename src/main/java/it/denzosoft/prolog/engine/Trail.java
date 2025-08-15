package it.denzosoft.prolog.engine;

import it.denzosoft.prolog.model.Variable;
import it.denzosoft.prolog.model.Term;
import java.util.ArrayList;
import java.util.List;





public class Trail {
    private final List<Variable> trail = new ArrayList<>();
    private final List<TermBinding> bindings = new ArrayList<>();
    
    /**
     * Pushes a variable onto the trail.
     */
    public void push(Variable var) {
        trail.add(var);
        if (var.isBound()) {
            bindings.add(new TermBinding(var, var.getBinding()));
        } else {
            bindings.add(new TermBinding(var, null));
        }
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
        for (int i = trail.size() - 1; i >= mark; i--) {
            TermBinding binding = bindings.get(i);
            Variable var = trail.get(i);
            // Restore the variable to its previous state
            if (binding.value == null) {
                // Variable was unbound before, so unbind it now
                var.unbind();
            } else {
                // Variable was bound before, so bind it to the previous value
                var.bind(var.getName(), binding.value);
            }
        }
        if (mark < trail.size()) {
            trail.subList(mark, trail.size()).clear();
            bindings.subList(mark, bindings.size()).clear();
        }
    }
    
    /**
     * Clears the trail.
     */
    public void clear() {
        trail.clear();
        bindings.clear();
    }
    
    /**
     * Helper class to store variable bindings
     */
    private static class TermBinding {
        final Variable variable;
        final Term value;
        
        TermBinding(Variable variable, Term value) {
            this.variable = variable;
            this.value = value;
        }
    }
}

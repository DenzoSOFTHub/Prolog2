package it.denzosoft.prolog.engine;

import it.denzosoft.prolog.PrologConfig;
import it.denzosoft.prolog.model.*;
import java.util.Map;


public class Unifier {
    
    /**
     * Unifies two terms.
     * @param a the first term
     * @param b the second term
     * @param substitution the current variable substitution
     * @param trail the trail for undoing bindings
     * @return true if unification succeeds
     */
    public static boolean unify(Term a, Term b, Map<String, Term> substitution, Trail trail) {
        PrologConfig.trace("Attempting to unify: " + a + " with " + b);
        
        // Apply substitutions first
        a = substitute(a, substitution);
        b = substitute(b, substitution);
        
        // Handle variables
        if (a instanceof Variable) {
            if (b instanceof Variable) {
                boolean result = ((Variable) a).getName().equals(((Variable) b).getName());
                PrologConfig.trace("Unifying variables: " + a + " and " + b + " -> " + result);
                return result;
            }
            return bind((Variable) a, b, substitution, trail);
        }
        
        if (b instanceof Variable) {
            return bind((Variable) b, a, substitution, trail);
        }
        
        // Handle numbers and atoms
        if (a.getClass() != b.getClass()) {
            PrologConfig.trace("Different classes, unification failed: " + a.getClass() + " vs " + b.getClass());
            return false;
        }
        
        // Atoms
        if (a instanceof Atom) {
            boolean result = a.equals(b);
            PrologConfig.trace("Unifying atoms: " + a + " and " + b + " -> " + result);
            return result;
        }
        
        // Numbers
        if (a instanceof it.denzosoft.prolog.model.Number) {
            it.denzosoft.prolog.model.Number numA = (it.denzosoft.prolog.model.Number) a;
            it.denzosoft.prolog.model.Number numB = (it.denzosoft.prolog.model.Number) b;
            boolean result = Double.compare(numA.getValue(), numB.getValue()) == 0;
            PrologConfig.trace("Unifying numbers: " + a + " and " + b + " -> " + result);
            return result;
        }
        
        // Lists
        if (a instanceof List && b instanceof List) {
            List listA = (List) a;
            List listB = (List) b;
            
            // Handle empty list case
            if (listA.isEmpty() && listB.isEmpty()) {
                PrologConfig.trace("Both lists are empty, unification succeeded");
                return true;
            }
            
            // Recursively unify head and tail
            boolean result = unify(listA.getHead(), listB.getHead(), substitution, trail) &&
                   unify(listA.getTail(), listB.getTail(), substitution, trail);
            PrologConfig.trace("Unifying lists: " + a + " and " + b + " -> " + result);
            return result;
        }
        
        // Structures
        if (a instanceof Struct && b instanceof Struct) {
            Struct structA = (Struct) a;
            Struct structB = (Struct) b;
            
            if (!structA.getFunctor().equals(structB.getFunctor()) ||
                structA.getArity() != structB.getArity()) {
                PrologConfig.trace("Structure functors or arities don't match");
                return false;
            }
            
            // Recursively unify args
            for (int i = 0; i < structA.getArity(); i++) {
                if (!unify(structA.getArgs()[i], structB.getArgs()[i], substitution, trail)) {
                    PrologConfig.trace("Failed to unify argument " + i + " of structures");
                    return false;
                }
            }
            
            PrologConfig.trace("Structures unified successfully: " + a + " and " + b);
            return true;
        }
        
        PrologConfig.trace("Unification failed for unknown reason");
        return false;
    }
    
    // Substitute variables in a term with their bindings
    private static Term substitute(Term term, Map<String, Term> substitution) {
        if (term instanceof Variable) {
            Variable var = (Variable) term;
            if (var.isBound()) {
                Term result = substitution.get(var.getName());
                PrologConfig.trace("Substituting variable " + var + " with " + result);
                return result;
            }
            return var;
        }
        
        if (term instanceof List) {
            List list = (List) term;
            return new List(
                substitute(list.getHead(), substitution),
                substitute(list.getTail(), substitution)
            );
        }
        
        if (term instanceof Struct) {
            Struct struct = (Struct) term;
            Term[] newArgs = new Term[struct.getArity()];
            for (int i = 0; i < struct.getArity(); i++) {
                newArgs[i] = substitute(struct.getArgs()[i], substitution);
            }
            return new Struct(struct.getFunctor(), newArgs);
        }
        
        return term; // Atoms and numbers are not substituted
    }
    
    // Bind a variable to a value
    private static boolean bind(Variable var, Term value, Map<String, Term> substitution, Trail trail) {
        if (!var.isBound()) {
            trail.push(var);
            substitution.put(var.getName(), value);
            PrologConfig.debug("Binding variable " + var.getName() + " to " + value);
            return true;
        }
        // If already bound, unify the binding with the new value
        PrologConfig.debug("Variable " + var.getName() + " already bound, unifying with " + value);
        return Unifier.unify(var.getBinding(), value, substitution, trail);
    }
}

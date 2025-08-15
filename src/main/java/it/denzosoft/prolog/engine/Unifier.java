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
        
        // Apply substitutions first - optimized version
        a = substitute(a, substitution);
        b = substitute(b, substitution);
        
        // Handle variables
        if (a instanceof Variable) {
            Variable varA = (Variable) a;
            if (b instanceof Variable) {
                Variable varB = (Variable) b;
                if (varA.getName().equals(varB.getName())) {
                    PrologConfig.trace("Unifying identical variables: " + a + " and " + b + " -> true");
                    return true;
                }
                return bind(varA, b, substitution, trail);
            }
            return bind(varA, b, substitution, trail);
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
            Term[] argsA = structA.getArgs();
            Term[] argsB = structB.getArgs();
            for (int i = 0; i < argsA.length; i++) {
                if (!unify(argsA[i], argsB[i], substitution, trail)) {
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
    
    // Substitute variables with their bindings
    private static Term substitute(Term term, Map<String, Term> substitution) {
        if (term instanceof Variable) {
            Variable var = (Variable) term;
            Term result = substitution.get(var.getName());
            if (result != null) {
                PrologConfig.trace("Substituting variable " + var.getName() + " with " + result);
                // Recursive substitution to handle chained bindings
                return substitute(result, substitution);
            }
            return var;
        }
        
        // For complex terms, we might need to substitute inside them
        return term;
    }
    
    // Bind a variable to a value
    private static boolean bind(Variable var, Term value, Map<String, Term> substitution, Trail trail) {
        // Occurs check - prevent circular bindings
        if (occurs(var, value, substitution)) {
            PrologConfig.trace("Occurs check failed for " + var.getName() + " in " + value);
            return false;
        }
        
        trail.push(var);
        substitution.put(var.getName(), value);
        PrologConfig.debug("Binding variable " + var.getName() + " to " + value);
        return true;
    }
    
    // Check if a variable occurs in a term (occurs check)
    private static boolean occurs(Variable var, Term term, Map<String, Term> substitution) {
        if (term instanceof Variable) {
            Variable termVar = (Variable) term;
            if (termVar.getName().equals(var.getName())) {
                return true;
            }
            // Check if the variable is bound to something that contains var
            Term binding = substitution.get(termVar.getName());
            if (binding != null) {
                return occurs(var, binding, substitution);
            }
            return false;
        }
        
        if (term instanceof Struct) {
            Struct struct = (Struct) term;
            for (Term arg : struct.getArgs()) {
                if (occurs(var, arg, substitution)) {
                    return true;
                }
            }
        }
        
        if (term instanceof List) {
            List list = (List) term;
            return occurs(var, list.getHead(), substitution) || occurs(var, list.getTail(), substitution);
        }
        
        return false;
    }
}

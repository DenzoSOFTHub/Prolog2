package it.denzosoft.prolog.query;

import it.denzosoft.prolog.PrologConfig;
import it.denzosoft.prolog.model.*;
import it.denzosoft.prolog.database.Database;
import it.denzosoft.prolog.engine.Trail;
import it.denzosoft.prolog.engine.Unifier;
import java.util.*;


public class Query {
    private final Term goal;
    private final Database database;
    private final Map<String, Term> substitution = new HashMap<>();
    private final Trail trail = new Trail();
    private int callDepth = 0; // For trace indentation
    
    public Query(Term goal, Database database) {
        this.goal = goal;
        this.database = database;
    }
    
    /**
     * Executes the query and returns solutions.
     */
    public java.util.List<Map<String, Term>> solve() {
        PrologConfig.debug("Starting query execution for: " + goal);
        java.util.List<Map<String, Term>> solutions = new ArrayList<>();
        solve(goal, solutions);
        PrologConfig.debug("Query execution completed with " + solutions.size() + " solutions");
        return solutions;
    }
    
    private boolean solve(Term term, java.util.List<Map<String, Term>> solutions) {
        String indent = "  ".repeat(callDepth);
        PrologConfig.trace(indent + "Solving: " + term);
        
        if (term instanceof Struct) {
            Struct struct = (Struct) term;
            Atom functor = struct.getFunctor();
            Term[] args = struct.getArgs();
            
            // Handle built-in predicates
            if (functor.getValue().equals("is") && args.length == 2) {
                PrologConfig.trace(indent + "Handling 'is' operator");
                solveIsOperator(args[0], args[1], solutions);
                return true;
            }
            
            if (functor.getValue().equals(",") && args.length == 2) {
                // Conjunction: solve first part, then remaining
                PrologConfig.trace(indent + "Handling conjunction");
                return solveConjunction(args[0], args[1], solutions);
            }
            
            if (functor.getValue().equals(";") && args.length == 2) {
                // Disjunction: solve either part
                PrologConfig.trace(indent + "Handling disjunction");
                return solveDisjunction(args[0], args[1], solutions);
            }
            
            // Check user-defined predicates
            java.util.List<Clause> clauses = database.findClauses(term);
            PrologConfig.trace(indent + "Found " + clauses.size() + " matching clauses");
            
            boolean foundSolution = false;
            for (Clause clause : clauses) {
                PrologConfig.trace(indent + "Trying clause: " + clause);
                
                // Save state for backtracking
                Map<String, Term> saveSubstitution = new HashMap<>(substitution);
                int trailMark = trail.mark();
                
                try {
                    // Unify goal with clause head
                    if (Unifier.unify(term, clause.getHead(), substitution, trail)) {
                        if (clause.getBody() == null) {
                            // Fact - solution found
                            PrologConfig.trace(indent + "  Matched fact, solution found");
                            solutions.add(new HashMap<>(substitution));
                            foundSolution = true;
                        } else {
                            // Recurse on body
                            PrologConfig.trace(indent + "  Matched rule, solving body: " + clause.getBody());
                            callDepth++;
                            if (solve(clause.getBody(), solutions)) {
                                foundSolution = true;
                            }
                            callDepth--;
                        }
                    } else {
                        PrologConfig.trace(indent + "  Unification failed");
                    }
                } finally {
                    // Backtrack by restoring substitution and trail
                    trail.undo(trailMark);
                    substitution.clear();
                    substitution.putAll(saveSubstitution);
                    PrologConfig.trace(indent + "  Backtracked");
                }
            }
            return foundSolution;
        }
        PrologConfig.trace(indent + "Cannot solve term: " + term);
        return false;
    }
    
    private boolean solveConjunction(Term left, Term right, java.util.List<Map<String, Term>> solutions) {
        String indent = "  ".repeat(callDepth);
        PrologConfig.trace(indent + "Solving conjunction: " + left + " , " + right);
        
        // Save state for backtracking
        Map<String, Term> saveSubstitution = new HashMap<>(substitution);
        int trailMark = trail.mark();
        
        try {
            // Solve left part
            java.util.List<Map<String, Term>> leftSolutions = new ArrayList<>();
            if (solve(left, leftSolutions)) {
                PrologConfig.trace(indent + "Left part succeeded with " + leftSolutions.size() + " solutions");
                boolean foundSolution = false;
                // For each solution of left, solve right
                for (Map<String, Term> leftSolution : leftSolutions) {
                    // Save current state
                    Map<String, Term> midSubstitution = new HashMap<>(substitution);
                    int midTrailMark = trail.mark();
                    
                    try {
                        // Apply left solution
                        substitution.putAll(leftSolution);
                        PrologConfig.trace(indent + "  Applying left solution and solving right: " + right);
                        
                        // Solve right part
                        if (solve(right, solutions)) {
                            foundSolution = true;
                        }
                    } finally {
                        // Restore state after solving right
                        trail.undo(midTrailMark);
                        substitution.clear();
                        substitution.putAll(midSubstitution);
                        PrologConfig.trace(indent + "  Backtracked from right part");
                    }
                }
                return foundSolution;
            }
            PrologConfig.trace(indent + "Left part failed");
            return false;
        } finally {
            // Backtrack to original state
            trail.undo(trailMark);
            substitution.clear();
            substitution.putAll(saveSubstitution);
            PrologConfig.trace(indent + "Backtracked from conjunction");
        }
    }
    
    private boolean solveDisjunction(Term left, Term right, java.util.List<Map<String, Term>> solutions) {
        String indent = "  ".repeat(callDepth);
        PrologConfig.trace(indent + "Solving disjunction: " + left + " ; " + right);
        
        // Save state for backtracking
        Map<String, Term> saveSubstitution = new HashMap<>(substitution);
        int trailMark = trail.mark();
        
        boolean foundSolution = false;
        
        try {
            // Try left part
            PrologConfig.trace(indent + "Trying left part: " + left);
            if (solve(left, solutions)) {
                foundSolution = true;
                PrologConfig.trace(indent + "Left part succeeded");
            }
            
            // Backtrack
            trail.undo(trailMark);
            substitution.clear();
            substitution.putAll(saveSubstitution);
            PrologConfig.trace(indent + "Backtracked after left part");
            
            // Try right part
            PrologConfig.trace(indent + "Trying right part: " + right);
            if (solve(right, solutions)) {
                foundSolution = true;
                PrologConfig.trace(indent + "Right part succeeded");
            }
        } finally {
            // Restore original state
            trail.undo(trailMark);
            substitution.clear();
            substitution.putAll(saveSubstitution);
            PrologConfig.trace(indent + "Backtracked from disjunction");
        }
        
        return foundSolution;
    }
    
    private void solveIsOperator(Term dest, Term expr, java.util.List<Map<String, Term>> solutions) {
        String indent = "  ".repeat(callDepth);
        PrologConfig.trace(indent + "Solving 'is' operator: " + dest + " is " + expr);
        
        // Evaluate the expression
        Term result = evaluateExpression(expr);
        
        if (result != null) {
            PrologConfig.trace(indent + "Expression evaluated to: " + result);
            
            // Save state for backtracking
            Map<String, Term> saveSubstitution = new HashMap<>(substitution);
            int trailMark = trail.mark();
            
            try {
                // Bind the destination to the result
                if (Unifier.unify(dest, result, substitution, trail)) {
                    PrologConfig.trace(indent + "Binding successful");
                    solutions.add(new HashMap<>(substitution));
                } else {
                    PrologConfig.trace(indent + "Binding failed");
                }
            } finally {
                // Backtrack
                trail.undo(trailMark);
                substitution.clear();
                substitution.putAll(saveSubstitution);
                PrologConfig.trace(indent + "Backtracked from 'is' operator");
            }
        } else {
            PrologConfig.trace(indent + "Expression evaluation failed");
        }
    }
    
    private Term evaluateExpression(Term expr) {
        String indent = "  ".repeat(callDepth);
        PrologConfig.trace(indent + "Evaluating expression: " + expr);
        
        if (expr instanceof it.denzosoft.prolog.model.Number) {
            PrologConfig.trace(indent + "  Expression is a number: " + expr);
            return expr;
        }
        
        if (expr instanceof Variable) {
            Variable var = (Variable) expr;
            if (var.isBound()) {
                PrologConfig.trace(indent + "  Variable " + var + " is bound to " + var.getBinding());
                return evaluateExpression(var.getBinding());
            }
            // Unbound variables in expressions cause errors
            PrologConfig.trace(indent + "  Variable " + var + " is unbound, evaluation failed");
            return null;
        }
        
        if (expr instanceof Struct) {
            Struct struct = (Struct) expr;
            Atom functor = struct.getFunctor();
            Term[] args = struct.getArgs();
            
            // Evaluate arguments first
            Term[] evaluatedArgs = new Term[args.length];
            for (int i = 0; i < args.length; i++) {
                evaluatedArgs[i] = evaluateExpression(args[i]);
                if (evaluatedArgs[i] == null) {
                    PrologConfig.trace(indent + "  Failed to evaluate argument " + i);
                    return null;
                }
            }
            
            // Simple arithmetic operators
            switch (functor.getValue()) {
                case "+":
                    if (args.length != 2) {
                        PrologConfig.trace(indent + "  Invalid number of arguments for + operator");
                        return null;
                    }
                    it.denzosoft.prolog.model.Number left = (it.denzosoft.prolog.model.Number) evaluatedArgs[0];
                    it.denzosoft.prolog.model.Number right = (it.denzosoft.prolog.model.Number) evaluatedArgs[1];
                    Term result = new it.denzosoft.prolog.model.Number(left.getValue() + right.getValue());
                    PrologConfig.trace(indent + "  Evaluated " + left + " + " + right + " = " + result);
                    return result;
                    
                case "-":
                    if (args.length == 1) { // Unary minus
                        it.denzosoft.prolog.model.Number operand = (it.denzosoft.prolog.model.Number) evaluatedArgs[0];
                        Term unaryResult = new it.denzosoft.prolog.model.Number(-operand.getValue());
                        PrologConfig.trace(indent + "  Evaluated -" + operand + " = " + unaryResult);
                        return unaryResult;
                    } else if (args.length == 2) { // Binary minus
                        it.denzosoft.prolog.model.Number leftOp = (it.denzosoft.prolog.model.Number) evaluatedArgs[0];
                        it.denzosoft.prolog.model.Number rightOp = (it.denzosoft.prolog.model.Number) evaluatedArgs[1];
                        Term binaryResult = new it.denzosoft.prolog.model.Number(leftOp.getValue() - rightOp.getValue());
                        PrologConfig.trace(indent + "  Evaluated " + leftOp + " - " + rightOp + " = " + binaryResult);
                        return binaryResult;
                    }
                    PrologConfig.trace(indent + "  Invalid number of arguments for - operator");
                    return null;
                    
                case "*":
                    if (args.length != 2) {
                        PrologConfig.trace(indent + "  Invalid number of arguments for * operator");
                        return null;
                    }
                    it.denzosoft.prolog.model.Number leftMul = (it.denzosoft.prolog.model.Number) evaluatedArgs[0];
                    it.denzosoft.prolog.model.Number rightMul = (it.denzosoft.prolog.model.Number) evaluatedArgs[1];
                    Term mulResult = new it.denzosoft.prolog.model.Number(leftMul.getValue() * rightMul.getValue());
                    PrologConfig.trace(indent + "  Evaluated " + leftMul + " * " + rightMul + " = " + mulResult);
                    return mulResult;
                    
                case "/":
                    if (args.length != 2) {
                        PrologConfig.trace(indent + "  Invalid number of arguments for / operator");
                        return null;
                    }
                    it.denzosoft.prolog.model.Number leftDiv = (it.denzosoft.prolog.model.Number) evaluatedArgs[0];
                    it.denzosoft.prolog.model.Number rightDiv = (it.denzosoft.prolog.model.Number) evaluatedArgs[1];
                    if (rightDiv.getValue() == 0) {
                        PrologConfig.trace(indent + "  Division by zero");
                        return null;
                    }
                    Term divResult = new it.denzosoft.prolog.model.Number(leftDiv.getValue() / rightDiv.getValue());
                    PrologConfig.trace(indent + "  Evaluated " + leftDiv + " / " + rightDiv + " = " + divResult);
                    return divResult;
                    
                default:
                    PrologConfig.trace(indent + "  Unknown operator: " + functor.getValue());
                    return null;
            }
        }
        
        PrologConfig.trace(indent + "  Cannot evaluate expression: " + expr);
        return null;
    }
    
    /**
     * Gets the current substitution.
     */
    public Map<String, Term> getSubstitution() {
        return new HashMap<>(substitution);
    }
}

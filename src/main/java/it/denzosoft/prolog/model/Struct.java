package it.denzosoft.prolog.model;

import java.util.Arrays;
import java.util.Map;


public class Struct implements Term {
    private final Atom functor;
    private final Term[] args;
    
    public Struct(Atom functor, Term... args) {
        this.functor = functor;
        this.args = args.clone();
    }
    
    @Override
    public Term copy(Map<String, Variable> context) {
        Term[] newArgs = new Term[args.length];
        for (int i = 0; i < args.length; i++) {
            newArgs[i] = args[i].copy(context);
        }
        return new Struct(functor, newArgs);
    }
    
    @Override
    public void bind(String varName, Term value) {
        for (Term arg : args) {
            arg.bind(varName, value);
        }
    }
    
    @Override
    public boolean containsUnboundVariables() {
        for (Term arg : args) {
            if (arg.containsUnboundVariables()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Type getType() {
        return Type.STRUCTURE;
    }
    
    public Atom getFunctor() {
        return functor;
    }
    
    public Term[] getArgs() {
        return args.clone();
    }
    
    public int getArity() {
        return args.length;
    }
    
    @Override
    public String toString() {
        if (args.length == 0) {
            return functor.toString();
        }
        
        StringBuilder sb = new StringBuilder(functor.toString()).append("(");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(args[i].toString());
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Struct)) return false;
        
        Struct other = (Struct) obj;
        if (!functor.equals(other.functor)) return false;
        if (args.length != other.args.length) return false;
        
        for (int i = 0; i < args.length; i++) {
            if (!args[i].equals(other.args[i])) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = functor.hashCode();
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}

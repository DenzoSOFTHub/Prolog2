package it.denzosoft.prolog.model;

import java.util.Map;


public class Number implements Term {
    private final double value;
    
    public Number(double value) {
        this.value = value;
    }
    
    public Number(int value) {
        this((double) value);
    }
    
    @Override
    public Term copy(Map<String, Variable> context) {
        return this;
    }
    
    @Override
    public void bind(String varName, Term value) {
        // Numbers cannot be bound
    }
    
    @Override
    public boolean containsUnboundVariables() {
        return false;
    }
    
    @Override
    public Type getType() {
        return Type.NUMBER;
    }
    
    public double getValue() {
        return value;
    }
    
    public boolean isInteger() {
        return value == (int) value;
    }
    
    @Override
    public String toString() {
        return isInteger() ? String.valueOf((int) value) : String.valueOf(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Number)) return false;
        return Double.compare(value, ((Number) obj).value) == 0;
    }
    
    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }
}

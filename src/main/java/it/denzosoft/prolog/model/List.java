package it.denzosoft.prolog.model;

import java.util.Map;


public class List implements Term {
    private final Term head;
    private final Term tail;
    
    public List(Term head, Term tail) {
        this.head = head;
        this.tail = tail;
    }
    
    @Override
    public Term copy(Map<String, Variable> context) {
        return new List(
            head.copy(context),
            tail.copy(context)
        );
    }
    
    @Override
    public void bind(String varName, Term value) {
        head.bind(varName, value);
        tail.bind(varName, value);
    }
    
    @Override
    public boolean containsUnboundVariables() {
        return head.containsUnboundVariables() || tail.containsUnboundVariables();
    }
    
    @Override
    public Type getType() {
        return Type.LIST;
    }
    
    public Term getHead() {
        return head;
    }
    
    public Term getTail() {
        return tail;
    }
    
    public boolean isEmpty() {
        return tail instanceof Atom && ((Atom) tail).getValue().equals("[]");
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[" + head);
        Term current = tail;
        while (current instanceof List && !((List) current).isEmpty()) {
            List list = (List) current;
            sb.append(", ").append(list.head);
            current = list.tail;
        }
        
        if (!(current instanceof Atom) || !((Atom) current).getValue().equals("[]")) {
            sb.append(" | ").append(current);
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof List)) return false;
        
        List other = (List) obj;
        return head.equals(other.head) && tail.equals(other.tail);
    }
    
    @Override
    public int hashCode() {
        int result = head.hashCode();
        result = 31 * result + tail.hashCode();
        return result;
    }
}

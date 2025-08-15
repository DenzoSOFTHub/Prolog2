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
        return head instanceof Atom && ((Atom) head).getValue().equals("[]") &&
               tail instanceof Atom && ((Atom) tail).getValue().equals("[]");
    }
    
    @Override
    public String toString() {
        // Handle empty list case properly
        if (head instanceof Atom && ((Atom) head).getValue().equals("[]") &&
            tail instanceof Atom && ((Atom) tail).getValue().equals("[]")) {
            return "[]";
        }
        
        if (tail instanceof Atom && ((Atom) tail).getValue().equals("[]")) {
            return "[" + head.toString() + "]";
        }
        
        StringBuilder sb = new StringBuilder("[" + head);
        Term current = tail;
        while (current instanceof List) {
            List list = (List) current;
            if (list.isEmpty()) {
                sb.append("]");
                return sb.toString();
            }
            if (list.tail instanceof Atom && ((Atom) list.tail).getValue().equals("[]")) {
                sb.append(", ").append(list.head).append("]");
                return sb.toString();
            }
            sb.append(", ").append(list.head);
            current = list.tail;
        }
        
        // If we get here, we have a list with a non-list tail
        sb.append(" | ").append(current).append("]");
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

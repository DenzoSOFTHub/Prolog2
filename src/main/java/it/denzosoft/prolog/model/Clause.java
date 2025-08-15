package it.denzosoft.prolog.model;

public class Clause {
    private final Term head;
    private final Term body;

    public Clause(Term head, Term body) {
        this.head = head;
        this.body = body;
    }

    public Term getHead() {
        return head;
    }

    public Term getBody() {
        return body;
    }

    @Override
    public String toString() {
        if (body == null) {
            return head.toString() + ".";
        }
        return head.toString() + " :- " + body.toString() + ".";
    }
}

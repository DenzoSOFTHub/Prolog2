package it.denzosoft.prolog.parser;

import it.denzosoft.prolog.model.*;
import java.io.IOException;
import java.util.ArrayList;
import java.io.StringReader;
import java.util.List;


public class Parser {
    private final Lexer lexer;
    private Token currentToken;
    
    public Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }
    
    public Term parseTerm() throws IOException {
        skipComments();
        
        if (currentToken.getType() == Token.Type.VARIABLE) {
            String varName = currentToken.getValue();
            advance();
            return new Variable(varName);
        }
        
        if (currentToken.getType() == Token.Type.NUMBER) {
            String numStr = currentToken.getValue();
            advance();
            return new it.denzosoft.prolog.model.Number(Double.parseDouble(numStr));
        }
        
        if (currentToken.getType() == Token.Type.ATOM) {
            // Check for string
            String atomValue = currentToken.getValue();
            if (atomValue.startsWith("'") && atomValue.endsWith("'")) {
                advance();
                return new Atom(atomValue.substring(1, atomValue.length() - 1));
            }
            
            advance();
            
            // Check if it's a structure or list
            if (currentToken.getType() == Token.Type.LPAREN) {
                advance(); // '('
                List<Term> args = parseArguments();
                expect(Token.Type.RPAREN);
                return new Struct(new Atom(atomValue), args.toArray(new Term[0]));
            }
            
            return new Atom(atomValue);
        }
        
        if (currentToken.getType() == Token.Type.LBRACKET) {
            return parseList();
        }
        
        if (currentToken.getType() == Token.Type.EOF) {
            throw new IOException("Unexpected end of input");
        }
        
        throw new IOException("Unexpected token: " + currentToken);
    }
    
    private java.util.List<Term> parseArguments() throws IOException {
        java.util.List<Term> args = new ArrayList<>();
        skipComments();
        
        if (currentToken.getType() != Token.Type.RPAREN) {
            args.add(parseTerm());
            
            while (true) {
                skipComments();
                if (currentToken.getType() != Token.Type.COMMA) {
                    break;
                }
                advance(); // ','
                args.add(parseTerm());
            }
        }
        
        return args;
    }
    
    private Term parseList() throws IOException {
        advance(); // '['
        skipComments();
        
        if (currentToken.getType() == Token.Type.RBRACKET) {
            advance(); // ']'
            return new it.denzosoft.prolog.model.List(new Atom("[]"), new Atom("[]"));
        }
        
        Term head = parseTerm();
        skipComments();
        
        if (currentToken.getType() == Token.Type.BAR) {
            advance(); // '|'
            Term tail = parseTerm();
            expect(Token.Type.RBRACKET);
            return new it.denzosoft.prolog.model.List(head, tail);
        }
        
        if (currentToken.getType() == Token.Type.RBRACKET) {
            advance(); // ']'
            return new it.denzosoft.prolog.model.List(head, new Atom("[]"));
        }
        
        // Multiple elements: collect them
        java.util.List<Term> elements = new ArrayList<>();
        elements.add(head);
        
        while (true) {
            skipComments();
            if (currentToken.getType() == Token.Type.BAR || 
                currentToken.getType() == Token.Type.RBRACKET) {
                break;
            }
            
            elements.add(parseTerm());
            skipComments();
        }
        
        if (currentToken.getType() == Token.Type.BAR) {
            advance(); // '|'
            Term tail = parseTerm();
            expect(Token.Type.RBRACKET);
            
            // Build the list structure
            Term list = tail;
            for (int i = elements.size() - 1; i >= 0; i--) {
                list = new it.denzosoft.prolog.model.List(elements.get(i), list);
            }
            return list;
        }
        
        expect(Token.Type.RBRACKET);
        
        // Build the list structure with null-terminated list
        Term list = new Atom("[]");
        for (int i = elements.size() - 1; i >= 0; i--) {
            list = new it.denzosoft.prolog.model.List(elements.get(i), list);
        }
        return list;
    }
    
    private void expect(Token.Type type) throws IOException {
        if (currentToken.getType() != type) {
            throw new IOException("Expected " + type + " but found " + currentToken);
        }
        advance();
    }
    
    private void advance() throws IOException {
        currentToken = lexer.nextToken();
    }
    
    private void skipComments() throws IOException {
        // We don't have comments in this lexer yet
    }
    
    public Clause parseClause() throws IOException {
        Term head = parseTerm();
        skipComments();
        
        if (currentToken.getType() == Token.Type.DOT) {
            advance();
            return new Clause(head, null); // Fact
        }
        
        expect(Token.Type.DEFINE);
        skipComments();
        
        Term body = parseTerm();
        expect(Token.Type.DOT);
        return new Clause(head, body);
    }
    
    // Add a helper method to parse from string
    public static Term parseTermFromString(String input) throws IOException {
        Lexer lexer = new Lexer(new StringReader(input));
        Parser parser = new Parser(lexer);
        return parser.parseTerm();
    }
    
    // Add a helper method to parse clause from string
    public static Clause parseClauseFromString(String input) throws IOException {
        Lexer lexer = new Lexer(new StringReader(input));
        Parser parser = new Parser(lexer);
        return parser.parseClause();
    }
}

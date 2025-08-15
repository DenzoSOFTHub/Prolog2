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
        return parseTerm900(); // Start with lowest precedence
    }
    
    // Parser with precedence handling
    // 900: , (conjunction)
    private Term parseTerm900() throws IOException {
        Term term = parseTerm800();
        skipComments();
        
        if (currentToken.getType() == Token.Type.COMMA) {
            advance(); // ','
            Term right = parseTerm900(); // Right associative
            return new Struct(new Atom(","), term, right);
        }
        
        return term;
    }
    
    // 800: ; (disjunction)
    private Term parseTerm800() throws IOException {
        Term term = parseTerm700();
        skipComments();
        
        if (currentToken.getType() == Token.Type.SEMICOLON) {
            advance(); // ';'
            Term right = parseTerm800(); // Right associative
            return new Struct(new Atom(";"), term, right);
        }
        
        return term;
    }
    
    // 700: =, \=, ==, \==, @<, @=<, @>, @>=, =.., is, =:=, =\=, <, =<, >, >=
    private Term parseTerm700() throws IOException {
        Term term = parseTerm500();
        skipComments();
        
        if (currentToken.getType() == Token.Type.EQ) {
            advance(); // '='
            Term right = parseTerm700();
            return new Struct(new Atom("="), term, right);
        } else if (currentToken.getType() == Token.Type.DEFINE) {
            advance(); // ':-'
            Term right = parseTerm900(); // Body should be at conjunction level
            return new Struct(new Atom(":-"), term, right);
        } else if (currentToken.getType() == Token.Type.IS) {
            advance(); // 'is'
            Term right = parseTerm700();
            return new Struct(new Atom("is"), term, right);
        } else if (currentToken.getType() == Token.Type.LT) {
            advance(); // '<'
            Term right = parseTerm700();
            return new Struct(new Atom("<"), term, right);
        } else if (currentToken.getType() == Token.Type.LTE) {
            advance(); // '=<'
            Term right = parseTerm700();
            return new Struct(new Atom("=<"), term, right);
        } else if (currentToken.getType() == Token.Type.GT) {
            advance(); // '>'
            Term right = parseTerm700();
            return new Struct(new Atom(">"), term, right);
        } else if (currentToken.getType() == Token.Type.GTE) {
            advance(); // '>='
            Term right = parseTerm700();
            return new Struct(new Atom(">="), term, right);
        }
        
        return term;
    }
    
    // 500: +, -, /\, \/
    private Term parseTerm500() throws IOException {
        Term term = parseTerm400();
        skipComments();
        
        while (currentToken.getType() == Token.Type.PLUS || currentToken.getType() == Token.Type.MINUS) {
            if (currentToken.getType() == Token.Type.PLUS) {
                advance(); // '+'
                Term right = parseTerm400();
                term = new Struct(new Atom("+"), term, right);
            } else if (currentToken.getType() == Token.Type.MINUS) {
                advance(); // '-'
                Term right = parseTerm400();
                term = new Struct(new Atom("-"), term, right);
            }
            skipComments();
        }
        
        return term;
    }
    
    // 400: *, /, //
    private Term parseTerm400() throws IOException {
        Term term = parseTerm200();
        skipComments();
        
        while (currentToken.getType() == Token.Type.MULTIPLY || currentToken.getType() == Token.Type.DIVIDE) {
            if (currentToken.getType() == Token.Type.MULTIPLY) {
                advance(); // '*'
                Term right = parseTerm200();
                term = new Struct(new Atom("*"), term, right);
            } else if (currentToken.getType() == Token.Type.DIVIDE) {
                advance(); // '/'
                Term right = parseTerm200();
                term = new Struct(new Atom("/"), term, right);
            }
            skipComments();
        }
        
        return term;
    }
    
    // 200: **
    private Term parseTerm200() throws IOException {
        Term term = parsePrimary();
        skipComments();
        
        if (currentToken.getType() == Token.Type.CARET) {
            advance(); // '^'
            Term right = parseTerm200(); // Right associative
            return new Struct(new Atom("**"), term, right);
        }
        
        return term;
    }
    
    // Primary expressions
    private Term parsePrimary() throws IOException {
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
        
        if (currentToken.getType() == Token.Type.LPAREN) {
            advance(); // '('
            Term term = parseTerm();
            expect(Token.Type.RPAREN);
            return term;
        }
        
        if (currentToken.getType() == Token.Type.CUT) {
            advance(); // '!'
            return new Atom("!");
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
            args.add(parseTerm900()); // Use conjunction level for arguments
            
            while (true) {
                skipComments();
                if (currentToken.getType() != Token.Type.COMMA) {
                    break;
                }
                advance(); // ','
                args.add(parseTerm900()); // Use conjunction level for arguments
            }
        }
        
        return args;
    }
    
    private Term parseList() throws IOException {
        advance(); // '['
        skipComments();
        
        if (currentToken.getType() == Token.Type.RBRACKET) {
            advance(); // ']'
            return new Atom("[]");
        }
        
        Term head = parseTerm900(); // Parse at conjunction level
        skipComments();
        
        if (currentToken.getType() == Token.Type.BAR) {
            advance(); // '|'
            Term tail = parseTerm900(); // Parse at conjunction level
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
            if (currentToken.getType() == Token.Type.COMMA) {
                advance(); // ','
                elements.add(parseTerm900()); // Parse at conjunction level
                skipComments();
            } else if (currentToken.getType() == Token.Type.BAR) {
                advance(); // '|'
                Term tail = parseTerm900(); // Parse at conjunction level
                expect(Token.Type.RBRACKET);
                
                // Build the list structure
                Term list = tail;
                for (int i = elements.size() - 1; i >= 0; i--) {
                    list = new it.denzosoft.prolog.model.List(elements.get(i), list);
                }
                return list;
            } else if (currentToken.getType() == Token.Type.RBRACKET) {
                advance(); // ']'
                break;
            } else {
                throw new IOException("Expected comma, bar, or right bracket but found: " + currentToken);
            }
        }
        
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
        
        if (currentToken.getType() == Token.Type.DEFINE) {
            advance(); // ':-'
            Term body = parseTerm900(); // Parse body at conjunction level
            expect(Token.Type.DOT);
            return new Clause(head, body);
        }
        
        throw new IOException("Expected '.' or ':-' but found " + currentToken);
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

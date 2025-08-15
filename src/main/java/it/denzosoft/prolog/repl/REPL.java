package it.denzosoft.prolog.repl;

import it.denzosoft.prolog.PrologConfig;
import it.denzosoft.prolog.model.*;
import it.denzosoft.prolog.database.Database;
import it.denzosoft.prolog.query.Query;
import it.denzosoft.prolog.parser.*;
import java.io.*;
import java.util.*;


public class REPL {
    private final Database database = new Database();
    private final Scanner scanner = new Scanner(System.in);
    
    public void start() {
        System.out.println("Prolog Interpreter v1.0");
        System.out.println("Type 'halt.' to exit.");
        System.out.println("Debug commands: 'debug on.', 'debug off.', 'trace on.', 'trace off.'");
        
        while (true) {
            try {
                System.out.print("?- ");
                String line = scanner.nextLine().trim();
                
                if (line.equals("halt.")) {
                    break;
                }
                
                if (line.isEmpty()) {
                    continue;
                }
                
                // Handle debug/trace commands
                if (line.equals("debug on.")) {
                    PrologConfig.setDebugEnabled(true);
                    System.out.println("Debug mode enabled.");
                    continue;
                }
                
                if (line.equals("debug off.")) {
                    PrologConfig.setDebugEnabled(false);
                    System.out.println("Debug mode disabled.");
                    continue;
                }
                
                if (line.equals("trace on.")) {
                    PrologConfig.setTraceEnabled(true);
                    System.out.println("Trace mode enabled.");
                    continue;
                }
                
                if (line.equals("trace off.")) {
                    PrologConfig.setTraceEnabled(false);
                    System.out.println("Trace mode disabled.");
                    continue;
                }
                
                // Parse and execute
                execute(line);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Goodbye!");
    }
    
    private void execute(String code) throws Exception {
        // Tokenize and parse
        Lexer lexer = new Lexer(new StringReader(code));
        Parser parser = new Parser(lexer);
        
        if (code.endsWith(".")) {
            // Parse as clause
            Clause clause = parser.parseClause();
            database.addClause(clause);
            System.out.println("OK.");
        } else {
            // Parse as query
            Term goal = parser.parseTerm();
            if (!goal.containsUnboundVariables()) {
                // Execute query
                Query query = new Query(goal, database);
                java.util.List<Map<String, Term>> solutions = query.solve();
                
                if (solutions.isEmpty()) {
                    System.out.println("No");
                } else {
                    for (Map<String, Term> solution : solutions) {
                        System.out.println("Yes");
                        for (Map.Entry<String, Term> entry : solution.entrySet()) {
                            System.out.println(entry.getKey() + " = " + entry.getValue());
                        }
                    }
                }
            } else {
                System.out.println("Error: Query contains unbound variables");
            }
        }
    }
    
    public static void main(String[] args) {
        new REPL().start();
    }
}

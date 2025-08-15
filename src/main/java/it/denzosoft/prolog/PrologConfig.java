package it.denzosoft.prolog;

public class PrologConfig {
    private static boolean debugEnabled = false;
    private static boolean traceEnabled = false;
    
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
    
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }
    
    public static boolean isTraceEnabled() {
        return traceEnabled;
    }
    
    public static void setTraceEnabled(boolean enabled) {
        traceEnabled = enabled;
    }
    
    /**
     * Prints debug message if debug is enabled.
     */
    public static void debug(String message) {
        if (debugEnabled) {
            System.err.println("[DEBUG] " + message);
        }
    }
    
    /**
     * Prints trace message if trace is enabled.
     */
    public static void trace(String message) {
        if (traceEnabled) {
            System.err.println("[TRACE] " + message);
        }
    }
}

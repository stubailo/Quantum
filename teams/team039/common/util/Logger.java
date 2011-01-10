package team039.common.util;

import battlecode.common.*;

public class Logger {
    
    /**
     * Prints raw string
     * @param s     string to be printed.
     */
    public static void debug_print(String s) {
        System.out.println(s);
    }
    
    /**
     * Prints exception stack trace.
     * @param   e   Exception
     */
    public static void debug_printExceptionMessage(Exception e) {
        e.printStackTrace();
    }

}

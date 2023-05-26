package com.se.hw.common;

import java.io.PrintStream;

public class TestUtil {
    public static void log(String s) {
        System.out.println("\033[32mBACKEND_INFO: " + s);
    }
}

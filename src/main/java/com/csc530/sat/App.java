package com.csc530.sat;

import java.nio.file.Paths;

import com.csc530.cnf.CNFParser;

public class App {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: sat <file.cnf>");
        } else {
            try {
                System.out.println("System is satifisable when");
                CNFParser.fromFile(Paths.get(args[0])).satisifyAll().stream()
                        .forEach(System.out::println);

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
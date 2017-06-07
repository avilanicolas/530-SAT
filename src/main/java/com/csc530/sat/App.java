package com.csc530.sat;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.csc530.cnf.CNFParser;
import com.csc530.smtlib.SmtLibRunner;

public class App {

    private static final String USAGE = "Usage: sat [--smt-lib] <file.cnf>";

    private static enum SourceType {
        CNF,
        SMT_LIB;
    }

    private static class Config {
        public SourceType type;
        public Path file;
    }

    /**
     * Parses the command line arguments into a configuration object.
     * Returns null if the provided arguments are not valid.
     */
    private static Config parseConfig(String[] args) {
        Config config = new Config();
        config.type = SourceType.CNF;
        for (int pos = 0; pos < args.length; pos++) {
            if ("--smt-lib".equals(args[pos])) {
                config.type = SourceType.SMT_LIB;
            } else {
                if (config.file == null) {
                    try {
                        config.file = Paths.get(args[pos]);
                    } catch (InvalidPathException ex) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
        if (config.file == null) {
            return null;
        }
        return config;
    }

    public static void main(String[] args) {
        Config config = parseConfig(args);
        if (config == null) {
            System.err.println(USAGE);
        } else {
            switch (config.type) {
            case CNF:
                try {
                    System.out.println("System is satifisable when");
                    CNFParser.fromFile(config.file).satisifyAll()
                            .forEach(System.out::println);

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                break;
            case SMT_LIB:
                try {
                    SmtLibRunner runner = new SmtLibRunner();
                    runner.runSmtLibFile(config.file);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                break;
            }
        }
    }
}
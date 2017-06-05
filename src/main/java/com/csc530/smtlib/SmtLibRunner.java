package com.csc530.smtlib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.smtlib.CharSequenceReader;
import org.smtlib.ICommand;
import org.smtlib.IParser;
import org.smtlib.IResponse;
import org.smtlib.ISource;
import org.smtlib.SMT;

/**
 * This class uses the jSMTLIB library to run SMT-LIB commands with the
 * DecisionDiagram data structure working as a backend.
 */
public class SmtLibRunner {
    private final SMT smt;
    private final SimpleSolver solver;

    public SmtLibRunner() {
        smt = new SMT();
        solver = new SimpleSolver(smt.smtConfig);
    }

    private String runCommand(ICommand command) {
        IResponse result = command.execute(solver);
        return smt.smtConfig.defaultPrinter.toString(result);
    }

    /** Interactively runs the smt-lib parser */
    public void runSmtLibInteractive() throws IOException, IParser.ParserException {
        ISource source = smt.smtConfig.smtFactory.createSource(smt.smtConfig, System.in,
                null);
        IParser parser = smt.smtConfig.smtFactory.createParser(smt.smtConfig, source);

        while (!parser.isEOD()) {
            ICommand command = parser.parseCommand();
            System.out.println(runCommand(command));
        }
    }

    /** Runs all commands from a file. Prints results to stdout */
    public void runSmtLibFile(Path filePath) throws IOException, IParser.ParserException {
        ISource source = smt.smtConfig.smtFactory.createSource(smt.smtConfig,
                new File(filePath.toUri()));
        IParser parser = smt.smtConfig.smtFactory.createParser(smt.smtConfig, source);

        while (!parser.isEOD()) {
            ICommand command = parser.parseCommand();
            System.out.print(smt.smtConfig.defaultPrinter.toString(command));
            System.out.print(" -> ");
            System.out.println(runCommand(command));
        }
    }

    /** Runs a single SMT-LIB command, and returns the result */
    public String runSmtLibCommand(String command)
            throws IOException, IParser.ParserException {
        ISource source = smt.smtConfig.smtFactory.createSource(
                new CharSequenceReader(new java.io.StringReader(command)), null);
        IParser parser = smt.smtConfig.smtFactory.createParser(smt.smtConfig, source);

        ICommand smtLibCommand = parser.parseCommand();

        if (!parser.isEOD()) {
            throw new RuntimeException("Expected just one command");
        }

        return runCommand(smtLibCommand);
    }
}

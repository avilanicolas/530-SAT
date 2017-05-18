package com.csc530.smtlib;

import java.io.File;
import java.io.IOException;

import org.smtlib.ICommand;
import org.smtlib.IParser;
import org.smtlib.IResponse;
import org.smtlib.ISource;
import org.smtlib.SMT;

public class SmtLibParser {
    private final SMT smt;
    private final SimpleSolver solver;

    public SmtLibParser() {
        smt = new SMT();
        solver = new SimpleSolver(smt.smtConfig);
    }

    public void handleCommand(ICommand command) {
        IResponse result = command.execute(solver);
        System.out.println(smt.smtConfig.defaultPrinter.toString(command) + " -> " + result);
    }

    public void runSmtLibFile(String fileName) throws IOException, IParser.ParserException {
        ISource source = smt.smtConfig.smtFactory.createSource(smt.smtConfig, new File(fileName));
        IParser parser = smt.smtConfig.smtFactory.createParser(smt.smtConfig,source);

        while (!parser.isEOD()) {
            ICommand command = parser.parseCommand();
            handleCommand(command);
        }
    }

    public static void main(String[] args) throws IOException, IParser.ParserException {
        SmtLibParser parser = new SmtLibParser();
        parser.runSmtLibFile("smt_lib_files/qf_uf_test_satisfiable.txt");
    }
}

package com.csc530.smtlib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class SmtLibRunnerTest {

    private static final String SATISFIABLE = "sat";
    private static final String UNSATISFIABLE = "unsat";

    @Test
    public void testBooleanDeclaration() {
        SmtLibRunner runner = new SmtLibRunner();
        try {
            runner.runSmtLibCommand("(declare-fun a () Bool)");
            runner.runSmtLibCommand("(declare-fun b () Bool)");
            runner.runSmtLibCommand("(declare-fun c () Bool)");
            runner.runSmtLibCommand("(assert a)");
            runner.runSmtLibCommand("(assert (not b))");
            runner.runSmtLibCommand("(assert c)");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSimpleBooleanFunctions() {
        SmtLibRunner runner = new SmtLibRunner();
        try {
            runner.runSmtLibCommand("(declare-fun a () Bool)");
            runner.runSmtLibCommand("(declare-fun b () Bool)");
            runner.runSmtLibCommand("(assert a)");
            runner.runSmtLibCommand("(assert (not b))");
            runner.runSmtLibCommand("(assert (and a b))");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), UNSATISFIABLE);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        runner = new SmtLibRunner();
        try {
            runner.runSmtLibCommand("(declare-fun a () Bool)");
            runner.runSmtLibCommand("(declare-fun b () Bool)");
            runner.runSmtLibCommand("(assert a)");
            runner.runSmtLibCommand("(assert (not b))");
            runner.runSmtLibCommand("(assert (or a b))");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        runner = new SmtLibRunner();
        try {
            runner.runSmtLibCommand("(declare-fun a () Bool)");
            runner.runSmtLibCommand("(declare-fun b () Bool)");
            runner.runSmtLibCommand("(assert a)");
            runner.runSmtLibCommand("(assert (not b))");
            runner.runSmtLibCommand("(assert (xor a b))");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testOtherBooleanFunctions() {
        SmtLibRunner runner = new SmtLibRunner();
        try {
            runner.runSmtLibCommand("(declare-fun a () Bool)");
            runner.runSmtLibCommand("(declare-fun b () Bool)");
            runner.runSmtLibCommand("(assert a)");
            runner.runSmtLibCommand("(assert (not b))");
            runner.runSmtLibCommand("(assert (=> a b))");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), UNSATISFIABLE);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        runner = new SmtLibRunner();
        try {
            runner.runSmtLibCommand("(declare-fun a () Bool)");
            runner.runSmtLibCommand("(declare-fun b () Bool)");
            runner.runSmtLibCommand("(assert a)");
            runner.runSmtLibCommand("(assert (not b))");
            runner.runSmtLibCommand("(assert (ite a (not a) a))");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), UNSATISFIABLE);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        runner = new SmtLibRunner();
        try {
            runner.runSmtLibCommand("(declare-fun a () Bool)");
            runner.runSmtLibCommand("(declare-fun b () Bool)");
            runner.runSmtLibCommand("(assert a)");
            runner.runSmtLibCommand("(assert (not b))");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
            runner.runSmtLibCommand("(assert (=> b a))");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
            runner.runSmtLibCommand("(assert (=> a b))");
            assertEquals(runner.runSmtLibCommand("(check-sat)"), UNSATISFIABLE);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}

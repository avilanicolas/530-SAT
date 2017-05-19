package com.csc530.smtlib;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SmtLibRunnerTest {

    private static final String SATISFIABLE = "sat";
    private static final String UNSATISFIABLE = "unsat";

    private SmtLibRunner runner;

    @Before
    public void initialize() throws Exception {
        runner = new SmtLibRunner();
        runner.runSmtLibCommand("(declare-fun t () Bool)");
        runner.runSmtLibCommand("(declare-fun f () Bool)");
        runner.runSmtLibCommand("(assert t)");
        runner.runSmtLibCommand("(assert (not f))");
    }

    @Test
    public void testBaseline() throws Exception {
        assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
    }

    @Test
    public void testDeclarations() throws Exception {
        runner.runSmtLibCommand("(declare-fun c () Bool)");
        runner.runSmtLibCommand("(assert c)");
        assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
    }

    @Test
    public void testAnd() throws Exception {
        runner.runSmtLibCommand("(assert (and t f))");
        assertEquals(runner.runSmtLibCommand("(check-sat)"), UNSATISFIABLE);
    }

    @Test
    public void testOr() throws Exception {
        runner.runSmtLibCommand("(assert (or t f))");
        assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
    }

    @Test
    public void testXor() throws Exception {
        runner.runSmtLibCommand("(assert (xor t f))");
        assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
    }

    @Test
    public void testImplies() throws Exception {
        runner.runSmtLibCommand("(assert (=> f t))");
        assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
        runner.runSmtLibCommand("(assert (=> t f))");
        assertEquals(runner.runSmtLibCommand("(check-sat)"), UNSATISFIABLE);
    }

    @Test
    public void testIte() throws Exception {
        runner.runSmtLibCommand("(assert (ite f t t))");
        assertEquals(runner.runSmtLibCommand("(check-sat)"), SATISFIABLE);
        runner.runSmtLibCommand("(assert (ite t (not t) t))");
        assertEquals(runner.runSmtLibCommand("(check-sat)"), UNSATISFIABLE);
    }
}

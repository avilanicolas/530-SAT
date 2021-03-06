package com.csc530.sat;

import java.util.Map;

import com.csc530.sat.condition.bool.BooleanCondition;
import com.csc530.sat.type.DDType;
import com.csc530.sat.type.bool.BooleanDDType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void demo() {
        // DecisionDiagrams are a way to reduce boolean equations to a tree
        // representation
        // A single diagram is simplified on construction to easily check if it
        // is
        // unsatisfiable

        // They reason over variables:
        Variable varA = new Variable("a", Boolean.class);
        Variable varB = new Variable("b", Boolean.class);
        Variable varC = new Variable("c", Boolean.class);
        Variable varD = new Variable("d", Boolean.class);

        // This is the boolean equation of a single term: a
        DecisionDiagram a = DecisionDiagramNode.of(varA, BooleanCondition.isTrue());

        // a = true is a solution
        assertTrue(a.satisfies(assignment(varA, true)));
        // a = false is not
        assertFalse(a.satisfies(assignment(varA, false)));

        // It can be negated to be: (not a)
        DecisionDiagram notA = a.not();
        assertFalse(notA.satisfies(assignment(varA, true)));
        assertTrue(notA.satisfies(assignment(varA, false)));

        DecisionDiagram b = DecisionDiagramNode.of(varB, BooleanCondition.isTrue());

        // (and a b)
        DecisionDiagram aAndB = a.and(b);
        assertTrue(aAndB.satisfies(assignment(varA, true, varB, true)));
        assertFalse(aAndB.satisfies(assignment(varA, true, varB, false)));
        assertFalse(aAndB.satisfies(assignment(varA, false, varB, true)));
        assertFalse(aAndB.satisfies(assignment(varA, false, varB, false)));

        // (or a b)
        DecisionDiagram aOrB = a.or(b);
        assertTrue(aOrB.satisfies(assignment(varA, true, varB, true)));
        assertTrue(aOrB.satisfies(assignment(varA, true, varB, false)));
        assertTrue(aOrB.satisfies(assignment(varA, false, varB, true)));
        assertFalse(aOrB.satisfies(assignment(varA, false, varB, false)));
        // you get the picture

        DecisionDiagram c = DecisionDiagramNode.of(varC, BooleanCondition.isTrue());

        // this is trivially unsatisfiable, and the DecisionDiagram can easily
        // reduce this
        // to a single leaf
        DecisionDiagram unsatisfiable = c.and(c.not());
        assertTrue(DecisionDiagramLeaf.UNSATISFIABLE == unsatisfiable);

        // this simplification can be seen here:
        assertTrue(DecisionDiagramLeaf.UNSATISFIABLE == aAndB.and(unsatisfiable));

        // It's negation is always satisfiable:
        assertTrue(DecisionDiagramLeaf.SATISFIABLE == unsatisfiable.not());

        // And it works just like the boolean literal true:
        assertEquals(aAndB, aAndB.and(unsatisfiable.not()));

        // We can construct more complex expressions:
        // (xor a b) === (or (and a (not b)) (and b (not a)))
        DecisionDiagram aXorB = a.xor(b);
        assertFalse(aXorB.satisfies(assignment(varA, true, varB, true)));
        assertTrue(aXorB.satisfies(assignment(varA, true, varB, false)));
        assertTrue(aXorB.satisfies(assignment(varA, false, varB, true)));
        assertFalse(aXorB.satisfies(assignment(varA, false, varB, false)));

        assertTrue(aXorB.not().satisfies(assignment(varA, true, varB, true)));
        assertFalse(aXorB.not().satisfies(assignment(varA, true, varB, false)));
        assertFalse(aXorB.not().satisfies(assignment(varA, false, varB, true)));
        assertTrue(aXorB.not().satisfies(assignment(varA, false, varB, false)));

        // Important to note: DD's are not supposed to be commutative
        // The order they're constructed in yields different, yet equivalent
        // trees.
        DecisionDiagram bAndA = b.and(a);

        // they are different
        assertFalse(aAndB.equals(bAndA));

        // yet they are also equivalent
        assertTrue(bAndA.satisfies(assignment(varA, true, varB, true)));
        assertFalse(bAndA.satisfies(assignment(varA, true, varB, false)));
        assertFalse(bAndA.satisfies(assignment(varA, false, varB, true)));
        assertFalse(bAndA.satisfies(assignment(varA, false, varB, false)));

        // Note on satisfaction: for an assignment to be satisfiable, the
        // assignment must
        // contain a collection of variable assignments which can be used to
        // traverse to a
        // SATISFIABLE node.

        // This assignment can arrive at a leaf node:
        assertTrue(bAndA.satisfies(assignment(varA, true, varC, false, varB, true)));
        // This assignment can't
        assertFalse(bAndA.satisfies(assignment(varC, false, varB, true)));

        // But reducible expressions can be satisfied by any assignment
        // Because they reduce to either SATISFIABLE or UNSATISFIABLE
        assertTrue(bAndA.or(aXorB.or(aXorB.not())).satisfies(assignment(varC, false)));
        assertTrue(bAndA.or(aXorB.or(aXorB.not())).satisfies(assignment()));

        DecisionDiagram d = DecisionDiagramNode.of(varD, BooleanCondition.isTrue());
        DecisionDiagram complex = aAndB.and(c.or(d.xor(a)));

        // If assignments are known, they can be reduced using
        // DecisionDiagram#assume
        // which constrains a diagram by assuming a variable's value
        assertEquals(DecisionDiagramLeaf.UNSATISFIABLE,
                complex.assume(varA, BooleanDDType.valueOf(false)));

        assertFalse(complex.satisfies(
                // Unsatisfiable because C is needed to determine satisfiability
                // for this
                // expression
                assignment(varA, true, varB, true, varD, false)));

        assertTrue(complex.assume(varC, BooleanDDType.valueOf(false)).satisfies(
                // C is no longer needed in this assignment
                assignment(varA, true, varB, true, varD, false)));

        // You could also reduce a decision diagram to just your unknown
        // variables
        assertEquals(c,
                complex.assume(varA, BooleanDDType.valueOf(true))
                        .assume(varB, BooleanDDType.valueOf(true))
                        .assume(varD, BooleanDDType.valueOf(true)));
        // Or reduce it to trivially satisfiable or unsatisfiable
        assertEquals(DecisionDiagramLeaf.SATISFIABLE,
                complex.assume(varA, BooleanDDType.valueOf(true))
                        .assume(varB, BooleanDDType.valueOf(true))
                        .assume(varC, BooleanDDType.valueOf(true))
                        .assume(varD, BooleanDDType.valueOf(true)));

        System.out.println("Demo completed successfully");
    }

    @SuppressWarnings("rawtypes")
    private Map<Variable, DDType> assignment(final Object... assignments) {
        Preconditions.checkArgument(assignments.length % 2 == 0);

        ImmutableMap.Builder<Variable, DDType> map = ImmutableMap.builder();
        for (int i = 0; i < assignments.length;) {
            map.put((Variable) assignments[i++],
                    BooleanDDType.valueOf((Boolean) assignments[i++]));
        }

        return map.build();
    }
}

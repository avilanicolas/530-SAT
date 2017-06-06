package com.csc530.sat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.junit.Test;

import com.csc530.sat.type.integer.IntegerDDType;
import com.google.common.collect.ImmutableMap;

public class VariableEQTheoryTest {
    private static final Variable<Integer> X = new Variable<>("x", Integer.class);
    private static final Variable<Integer> Y = new Variable<>("y", Integer.class);
    private static final Variable<Integer> W = new Variable<>("w", Integer.class);
    private static final Variable<Integer> Z = new Variable<>("z", Integer.class);

    @Test
    public void testEQTheory() {
        DecisionDiagramNode<Integer> xeqy = DecisionDiagramNode.eq(X, Y,
                IntegerDDType.create(2), IntegerDDType.create(4));
        DecisionDiagramNode<Integer> wneqz = DecisionDiagramNode.neq(W, Z,
                IntegerDDType.create(2), IntegerDDType.create(4));
        
        DecisionDiagram xeqyORwneqz = xeqy.or(wneqz);

        assertTrue(xeqy.satisfies(ImmutableMap.of(X, 3, Y, 3).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));

        assertFalse(xeqy.satisfies(ImmutableMap.of(X, 3, Y, 5).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));

        assertTrue(wneqz.satisfies(ImmutableMap.of(W, 3, Z, 5).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));

        assertTrue(xeqyORwneqz.satisfies(ImmutableMap.of(X, 3, Y, 3, W, 3, Z, 3).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));

        assertTrue(xeqyORwneqz.satisfies(ImmutableMap.of(X, 3, Y, 21, W, 4, Z, 3).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));

        assertFalse(xeqyORwneqz.satisfies(ImmutableMap.of(X, 3, Y, 4, W, 3, Z, 3).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));
    }

}

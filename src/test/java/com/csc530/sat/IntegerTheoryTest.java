package com.csc530.sat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.junit.Test;

import com.csc530.sat.condition.bool.BooleanCondition;
import com.csc530.sat.condition.integer.IntegerEQCondition;
import com.csc530.sat.type.bool.BooleanDDType;
import com.csc530.sat.type.integer.IntegerDDType;
import com.google.common.collect.ImmutableMap;

public class IntegerTheoryTest {
    private static final SMTVariable<Integer> X = new SMTVariable<>("x", Integer.class);
    private static final SMTVariable<Integer> Y = new SMTVariable<>("y", Integer.class);
    private static final SMTVariable<Boolean> Z = new SMTVariable<>("z", Boolean.class);
    private static final SMTVariable<Boolean> W = new SMTVariable<>("w", Boolean.class);

    @Test
    public void testIntEQTheory() {
        DecisionDiagramNode<Integer> xDiagram = DecisionDiagramNode.of(X,
                IntegerEQCondition.create(3));
        DecisionDiagramNode<Integer> yDiagram = DecisionDiagramNode.of(Y,
                IntegerEQCondition.create(4));
        DecisionDiagram XAndY = xDiagram.and(yDiagram);

        assertTrue(XAndY.satisfies(ImmutableMap.of(X, 3, Y, 4).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));
        assertTrue(XAndY.not().satisfies(ImmutableMap.of(X, 3, Y, 42).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));
        assertTrue(XAndY.not().satisfies(ImmutableMap.of(X, 34, Y, 4).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));
        assertTrue(XAndY.not().satisfies(ImmutableMap.of(X, 34, Y, 44).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> IntegerDDType.create(entry.getValue())))));
    }

    @Test
    public void testMixedTheory() {
        DecisionDiagramNode<Integer> xDiagram = DecisionDiagramNode.of(X,
                IntegerEQCondition.create(3));
        DecisionDiagramNode<Integer> yDiagram = DecisionDiagramNode.of(Y,
                IntegerEQCondition.create(4));
        DecisionDiagramNode<Boolean> zDiagram = DecisionDiagramNode.of(Z,
                BooleanCondition.isTrue());
        DecisionDiagramNode<Boolean> wDiagram = DecisionDiagramNode.of(W,
                BooleanCondition.isTrue());
        DecisionDiagram XorYandZorW = xDiagram.or(yDiagram).and(zDiagram.or(wDiagram));

        assertTrue(XorYandZorW.satisfies(ImmutableMap.of(
                X, IntegerDDType.create(3),
                Y, IntegerDDType.create(43),
                Z, BooleanDDType.valueOf(false),
                W, BooleanDDType.valueOf(true))));
        assertFalse(XorYandZorW.satisfies(ImmutableMap.of(
                X, IntegerDDType.create(3),
                Y, IntegerDDType.create(43),
                Z, BooleanDDType.valueOf(false),
                W, BooleanDDType.valueOf(false))));
    }

}

package com.csc530.sat;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.csc530.sat.branch.Decision;
import com.csc530.sat.condition.DDCondition;
import com.csc530.sat.type.DDType;
import com.google.common.collect.ImmutableMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A decision diagram representing a boolean equation. This diagram attempts to
 * simplify its structure when it can by removing variables from the diagram
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({ "rawtypes", "unchecked" })
@Getter
public class DecisionDiagramNode<T> implements DecisionDiagram {
    private final SMTVariable<T> primaryVariable;
    private final LinkedHashSet<SMTVariable> variables;
    private final Decision<T> branches;

    public static <T> DecisionDiagramNode<T> of(final SMTVariable<T> variable,
            final DDCondition<T> condition) {
        LinkedHashSet<SMTVariable<?>> vars = new LinkedHashSet<>();
        vars.add(variable);
        return of(variable, vars, condition);
    }

    public static DecisionDiagramNode of(final SMTVariable variable,
            final LinkedHashSet<SMTVariable<?>> variables, final DDCondition condition) {
        return new DecisionDiagramNode(variable, variables,
                Decision.builder()
                        .primaryVariable(variable)
                        .condition(condition)
                        .trueBranch(DecisionDiagramLeaf.SATISFIABLE)
                        .falseBranch(DecisionDiagramLeaf.UNSATISFIABLE).build());
    }

    @Override
    public DecisionDiagram or(final DecisionDiagram diagram) {
        return merge(diagram, (d1, d2) -> d1.or(d2));
    }

    @Override
    public DecisionDiagram and(final DecisionDiagram diagram) {
        return merge(diagram, (d1, d2) -> d1.and(d2));
    }

    private DecisionDiagram merge(final DecisionDiagram diagram,
            final BiFunction<DecisionDiagram, DecisionDiagram, DecisionDiagram> merge) {
        if (diagram instanceof DecisionDiagramLeaf) {
            return merge.apply(diagram, this);
        }

        DecisionDiagramNode otherNode = (DecisionDiagramNode) diagram;
        LinkedHashSet<SMTVariable> vars = new LinkedHashSet<>();
        vars.addAll(variables);
        vars.addAll(otherNode.variables);
        if (primaryVariable.equals(otherNode.primaryVariable)) {
            DecisionDiagram mergedTrue = merge.apply(
                    branches.getTrueBranch(),
                    otherNode.getBranches().getTrueBranch()
                            .assume(primaryVariable,
                                    branches.getCondition().satisifier()));
            DecisionDiagram mergedFalse = merge.apply(
                    branches.getFalseBranch(),
                    otherNode.getBranches().getFalseBranch()
                            .assume(primaryVariable,
                                    branches.getCondition().unSatisifier()));

            if (canSimplify(mergedTrue, mergedFalse)) {
                return mergedTrue;
            }

            return new DecisionDiagramNode(primaryVariable, vars,
                    branches.toBuilder()
                            .trueBranch(mergedTrue)
                            .falseBranch(mergedFalse)
                            .build());
        }

        DecisionDiagram mergedTrue = merge.apply(
                branches.getTrueBranch(),
                otherNode.assume(primaryVariable, branches.getCondition().satisifier()));
        DecisionDiagram mergedFalse = merge.apply(
                branches.getFalseBranch(),
                otherNode.assume(primaryVariable,
                        branches.getCondition().unSatisifier()));

        if (canSimplify(mergedTrue, mergedFalse)) {
            return mergedTrue;
        }

        return new DecisionDiagramNode(primaryVariable, vars,
                branches.toBuilder()
                        .trueBranch(mergedTrue)
                        .falseBranch(mergedFalse)
                        .build());
    }

    @Override
    public DecisionDiagram not() {
        return new DecisionDiagramNode(primaryVariable, variables, branches.toBuilder()
                .trueBranch(branches.getTrueBranch().not())
                .falseBranch(branches.getFalseBranch().not())
                .build());
    }

    @Override
    public boolean satisfies(final Map<SMTVariable, DDType> assignment) {
        // This tree cannot possibly be satisfied if the assignment is empty
        if (assignment.isEmpty()) {
            return false;
        }

        // Or if it doesn't contain this node's variable!
        if (!assignment.containsKey(primaryVariable)) {
            return false;
        }

        DecisionDiagram branch = branches.getBranch(assignment.get(primaryVariable));

        // If the assignment does contain the variable, remove it from the
        // assignment
        // and recursively check the branch children
        Map<SMTVariable, DDType> reducedAssignment = assignment.keySet().stream()
                .filter(k -> !primaryVariable.equals(k))
                .collect(Collectors.toMap(k -> k, assignment::get));

        return branch.satisfies(reducedAssignment);
    }

    @Override
    public DecisionDiagram assume(final SMTVariable variable, final DDType value) {
        if (!variables.contains(variable)) {
            return this;
        }

        if (primaryVariable.equals(variable)) {
            return branches.getBranch(value);
        }

        DecisionDiagram trueBranchAssumed = branches.getTrueBranch().assume(variable,
                value);
        DecisionDiagram falsedBranchAssumed = branches.getFalseBranch().assume(variable,
                value);

        if (canSimplify(trueBranchAssumed, falsedBranchAssumed)) {
            return trueBranchAssumed;
        }

        LinkedHashSet<SMTVariable<?>> vars = new LinkedHashSet<>();
        variables.stream()
                .filter(v -> !v.equals(variable))
                .forEach(vars::add);

        return new DecisionDiagramNode(primaryVariable, vars,
                branches.toBuilder()
                        .trueBranch(trueBranchAssumed)
                        .falseBranch(falsedBranchAssumed)
                        .build());
    }

    private boolean canSimplify(final DecisionDiagram d1, final DecisionDiagram d2) {
        return d1 instanceof DecisionDiagramLeaf && d2 instanceof DecisionDiagramLeaf
                && d1 == d2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || !(o instanceof DecisionDiagramNode)) {
            return false;
        }

        DecisionDiagramNode other = (DecisionDiagramNode) o;
        return primaryVariable.equals(other.primaryVariable)
                && variables.equals(other.variables)
                && branches.getTrueBranch().equals(other.getBranches().getTrueBranch())
                && branches.getFalseBranch().equals(other.getBranches().getFalseBranch());
    }

    /**
     * Provides a JSON string representation to easily use in debugging.
     */
    @Override
    public String toString() {
        return String.format("{\"%s\": {\"true\": %s, \"false\": %s}}", primaryVariable,
                branches.getTrueBranch(), branches.getFalseBranch());
    }

    @Override
    public Stream<Map<SMTVariable, DDType>> satisifyAll() {
        Stream.Builder<Map<SMTVariable, DDType>> builder = Stream.builder();
        if (branches.getTrueBranch() instanceof DecisionDiagramLeaf) {
            if (((DecisionDiagramLeaf) branches.getTrueBranch())
                    .equals(DecisionDiagramLeaf.SATISFIABLE)) {
                builder.add(
                        ImmutableMap.of(primaryVariable,
                                branches.getCondition().satisifier()));

            }

        } else {
            branches.getTrueBranch().satisifyAll()
                    .forEach(
                            sat -> builder.add(ImmutableMap.<SMTVariable, DDType>builder()
                                    .put(primaryVariable,
                                            branches.getCondition().satisifier())
                                    .putAll(sat).build()));
        }
        if (branches.getFalseBranch() instanceof DecisionDiagramLeaf) {
            if (((DecisionDiagramLeaf) branches.getFalseBranch())
                    .equals(DecisionDiagramLeaf.SATISFIABLE)) {
                builder.add(ImmutableMap.of(primaryVariable,
                        branches.getCondition().unSatisifier()));
            }

        } else {
            branches.getFalseBranch().satisifyAll()
                    .forEach(
                            sat -> builder.add(ImmutableMap.<SMTVariable, DDType>builder()
                                    .put(primaryVariable,
                                            branches.getCondition().unSatisifier())
                                    .putAll(sat).build()));
        }
        return builder.build();
    }
}
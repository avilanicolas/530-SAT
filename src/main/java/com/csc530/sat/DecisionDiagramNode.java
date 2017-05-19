package com.csc530.sat;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * A decision diagram representing a boolean equation. This diagram attempts to
 * simplify its structure when it can by removing variables from the diagram
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DecisionDiagramNode implements DecisionDiagram {

    private final SATVariable primaryVariable;
    private final Set<SATVariable> variables;
    private final DecisionDiagram trueBranch;
    private final DecisionDiagram falseBranch;

    public static DecisionDiagramNode of(final SATVariable variable) {
        return of(variable, ImmutableSet.of(variable));
    }

    public static DecisionDiagramNode of(final SATVariable variable,
            final Set<SATVariable> variables) {
        return new DecisionDiagramNode(variable, variables,
                DecisionDiagramLeaf.SATISFIABLE, DecisionDiagramLeaf.UNSATISFIABLE);
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

        if (primaryVariable.equals(otherNode.primaryVariable)) {
            DecisionDiagram mergedTrue = merge.apply(trueBranch,
                    otherNode.trueBranch.assume(primaryVariable, true));
            DecisionDiagram mergedFalse = merge.apply(falseBranch,
                    otherNode.falseBranch.assume(primaryVariable, false));

            if (canSimplify(mergedTrue, mergedFalse)) {
                return mergedTrue;
            }

            return new DecisionDiagramNode(primaryVariable, variables, mergedTrue,
                    mergedFalse);
        }

        DecisionDiagram mergedTrue = merge.apply(trueBranch,
                otherNode.assume(primaryVariable, true));
        DecisionDiagram mergedFalse = merge.apply(falseBranch,
                otherNode.assume(primaryVariable, false));

        if (canSimplify(mergedTrue, mergedFalse)) {
            return mergedTrue;
        }

        return new DecisionDiagramNode(primaryVariable,
                ImmutableSet.<SATVariable>builder().addAll(variables)
                        .addAll(otherNode.variables).build(),
                mergedTrue, mergedFalse);
    }

    @Override
    public DecisionDiagram not() {
        return new DecisionDiagramNode(primaryVariable, variables, trueBranch.not(),
                falseBranch.not());
    }

    @Override
    public boolean satisfies(final Map<SATVariable, Boolean> assignment) {
        // This tree cannot possibly be satisfied if the assignment is empty
        if (assignment.isEmpty()) {
            return false;
        }

        // Or if it doesn't contain this node's variable!
        if (!assignment.containsKey(primaryVariable)) {
            return false;
        }

        DecisionDiagram branch = assignment.get(primaryVariable) ? trueBranch
                : falseBranch;

        // If the assignment does contain the variable, remove it from the
        // assignment
        // and recursively check the branch children
        Map<SATVariable, Boolean> reducedAssignment = assignment.keySet().stream()
                .filter(k -> !primaryVariable.equals(k))
                .collect(Collectors.toMap(k -> k, assignment::get));

        return branch.satisfies(reducedAssignment);
    }

    @Override
    public DecisionDiagram assume(final SATVariable variable, final Boolean value) {
        if (!variables.contains(variable)) {
            return this;
        }

        if (primaryVariable.equals(variable)) {
            return value ? trueBranch : falseBranch;
        }

        DecisionDiagram trueBranchAssumed = trueBranch.assume(variable, value);
        DecisionDiagram falsedBranchAssumed = falseBranch.assume(variable, value);
        if (canSimplify(trueBranchAssumed, falsedBranchAssumed)) {
            return trueBranchAssumed;
        }

        return new DecisionDiagramNode(
                primaryVariable, variables.stream().filter(v -> !v.equals(variable))
                        .collect(Collectors.toSet()),
                trueBranchAssumed, falsedBranchAssumed);
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
                && trueBranch.equals(other.trueBranch)
                && falseBranch.equals(other.falseBranch);
    }

    /**
     * Provides a JSON string representation to easily use in debugging.
     */
    @Override
    public String toString() {
        return String.format("{\"%s\": {\"true\": %s, \"false\": %s}}", primaryVariable,
                trueBranch, falseBranch);
    }

    @Override
    public Stream<Map<SATVariable, Boolean>> satisifyAll() {
        Stream.Builder<Map<SATVariable, Boolean>> builder = Stream.builder();
        if (trueBranch instanceof DecisionDiagramLeaf) {
            if (((DecisionDiagramLeaf) trueBranch)
                    .equals(DecisionDiagramLeaf.SATISFIABLE)) {
                builder.add(ImmutableMap.of(primaryVariable, true));

            }

        } else {
            trueBranch.satisifyAll().forEach(
                    sat -> builder.add(ImmutableMap.<SATVariable, Boolean>builder()
                            .put(primaryVariable, true).putAll(sat).build()));
        }
        if (falseBranch instanceof DecisionDiagramLeaf) {
            if (((DecisionDiagramLeaf) falseBranch)
                    .equals(DecisionDiagramLeaf.SATISFIABLE)) {
                builder.add(ImmutableMap.of(primaryVariable, false));
            }

        } else {
            falseBranch.satisifyAll().forEach(
                    sat -> builder.add(ImmutableMap.<SATVariable, Boolean>builder()
                            .put(primaryVariable, false).putAll(sat).build()));
        }
        return builder.build();
    }

    @Override
    public boolean isSatisfiable() {
        return true;
    }
}
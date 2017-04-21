package com.avilan.sat;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * A decision diagram representing a boolean equation. This diagram attempts to simplify
 * its structure when it can by removing variables from the diagram 
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DecisionDiagramNode implements DecisionDiagram {

   private final SATVariable primaryVariable;
   private final Set<SATVariable> variables;
   private final DecisionDiagram trueBranch;
   private final DecisionDiagram falseBranch;

   public static DecisionDiagramNode of(
         final SATVariable variable) {
      return of(variable, ImmutableSet.of(variable));
   }

   public static DecisionDiagramNode of(
         final SATVariable variable,
         final Set<SATVariable> variables) {
      return new DecisionDiagramNode(
         variable,
         variables,
         DecisionDiagramLeaf.SATISFIABLE,
         DecisionDiagramLeaf.UNSATISFIABLE);
   }

   public DecisionDiagram or(final DecisionDiagram diagram) {
      return merge(diagram, (d1, d2) -> d1.or(d2));
   }

   public DecisionDiagram and(final DecisionDiagram diagram) {
      return merge(diagram, (d1, d2) -> d1.and(d2));
   }

   private DecisionDiagram merge(
         final DecisionDiagram diagram,
         final BiFunction<DecisionDiagram, DecisionDiagram, DecisionDiagram> merge) {
      if (diagram instanceof DecisionDiagramLeaf) {
         return merge.apply(diagram, this);
      }

      DecisionDiagramNode otherNode = (DecisionDiagramNode) diagram;

      if (primaryVariable.equals(otherNode.primaryVariable)) {
         DecisionDiagram mergedTrue =
            merge.apply(trueBranch, otherNode.trueBranch.assume(primaryVariable, true));
         DecisionDiagram mergedFalse =
            merge.apply(falseBranch, otherNode.falseBranch.assume(primaryVariable, false));

         if (canSimplify(mergedTrue, mergedFalse)) {
            return mergedTrue;
         }

         return new DecisionDiagramNode(
            primaryVariable,
            variables,
            mergedTrue,
            mergedFalse);
      }

      DecisionDiagram mergedTrue =
         merge.apply(trueBranch, otherNode.assume(primaryVariable, true));
      DecisionDiagram mergedFalse =
         merge.apply(falseBranch, otherNode.assume(primaryVariable, false));

      if (canSimplify(mergedTrue, mergedFalse)) {
         return mergedTrue;
      }

      return new DecisionDiagramNode(
         primaryVariable,
         ImmutableSet.<SATVariable> builder()
            .addAll(variables)
            .addAll(otherNode.variables)
            .build(),
         mergedTrue,
         mergedFalse);
   }

   public DecisionDiagram not() {
      return new DecisionDiagramNode(
         primaryVariable,
         variables,
         trueBranch.not(),
         falseBranch.not());
   }

   public boolean satisfies(final Map<SATVariable, Boolean> variables) {
      if (!variables.containsKey(primaryVariable)) {
         return trueBranch.satisfies(variables) || falseBranch.satisfies(variables);
      }

      DecisionDiagram branch = variables.get(primaryVariable) ? trueBranch : falseBranch;

      return branch.satisfies(variables);
   }

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
           primaryVariable,
           variables.stream()
              .filter(v -> !v.equals(variable))
              .collect(Collectors.toSet()),
           trueBranchAssumed,
           falsedBranchAssumed);
   }

   private boolean canSimplify(
         final DecisionDiagram d1,
         final DecisionDiagram d2) {
      return d1 instanceof DecisionDiagramLeaf
         && d2 instanceof DecisionDiagramLeaf
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
      return
         primaryVariable.equals(other.primaryVariable)
            && variables.equals(other.variables)
            && trueBranch.equals(other.trueBranch)
            && falseBranch.equals(other.falseBranch);
   }

   @Override
   public String toString() {
      return String.format(
            "{\"%s\": {\"true\": %s, \"false\": %s}}",
            primaryVariable,
            trueBranch,
            falseBranch);
   }
}
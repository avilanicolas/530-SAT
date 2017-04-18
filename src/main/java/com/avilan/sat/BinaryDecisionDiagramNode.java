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
public class BinaryDecisionDiagramNode implements BinaryDecisionDiagram {

   private final SATVariable primaryVariable;
   private final Set<SATVariable> variables;
   private final BinaryDecisionDiagram trueBranch;
   private final BinaryDecisionDiagram falseBranch;

   public static BinaryDecisionDiagramNode of(
         final SATVariable variable) {
      return of(variable, ImmutableSet.of(variable));
   }

   public static BinaryDecisionDiagramNode of(
         final SATVariable variable,
         final Set<SATVariable> variables) {
      return new BinaryDecisionDiagramNode(
         variable,
         variables,
         BinaryDecisionDiagramLeaf.TRUE,
         BinaryDecisionDiagramLeaf.FALSE);
   }

   public BinaryDecisionDiagram or(final BinaryDecisionDiagram diagram) {
      return merge(diagram, (d1, d2) -> d1.or(d2));
   }

   public BinaryDecisionDiagram and(final BinaryDecisionDiagram diagram) {
      return merge(diagram, (d1, d2) -> d1.and(d2));
   }

   private BinaryDecisionDiagram merge(
         final BinaryDecisionDiagram diagram,
         final BiFunction<BinaryDecisionDiagram, BinaryDecisionDiagram, BinaryDecisionDiagram> merge) {
      if (diagram instanceof BinaryDecisionDiagramLeaf) {
         return merge.apply(diagram, this);
      }

      BinaryDecisionDiagramNode otherNode = (BinaryDecisionDiagramNode) diagram;

      if (primaryVariable.equals(otherNode.primaryVariable)) {
         BinaryDecisionDiagram mergedTrue =
            merge.apply(trueBranch, otherNode.trueBranch.assume(primaryVariable, true));
         BinaryDecisionDiagram mergedFalse =
            merge.apply(falseBranch, otherNode.falseBranch.assume(primaryVariable, false));

         if (canSimplify(mergedTrue, mergedFalse)) {
            return mergedTrue;
         }

         return new BinaryDecisionDiagramNode(
            primaryVariable,
            variables,
            mergedTrue,
            mergedFalse);
      }

      BinaryDecisionDiagram mergedTrue =
         merge.apply(trueBranch, otherNode.assume(primaryVariable, true));
      BinaryDecisionDiagram mergedFalse =
         merge.apply(falseBranch, otherNode.assume(primaryVariable, false));

      if (canSimplify(mergedTrue, mergedFalse)) {
         return mergedTrue;
      }

      return new BinaryDecisionDiagramNode(
         primaryVariable,
         ImmutableSet.<SATVariable> builder()
            .addAll(variables)
            .addAll(otherNode.variables)
            .build(),
         mergedTrue,
         mergedFalse);
   }

   public BinaryDecisionDiagram not() {
      return new BinaryDecisionDiagramNode(
         primaryVariable,
         variables,
         trueBranch.not(),
         falseBranch.not());
   }

   public boolean satisfies(final Map<SATVariable, Boolean> variables) {
      if (!variables.containsKey(primaryVariable)) {
         return trueBranch.satisfies(variables) || falseBranch.satisfies(variables);
      }

      BinaryDecisionDiagram branch = variables.get(primaryVariable) ? trueBranch : falseBranch;

      return branch.satisfies(variables);
   }

   public BinaryDecisionDiagram assume(final SATVariable variable, final Boolean value) {
      if (!variables.contains(variable)) {
         return this;
      }

      if (primaryVariable.equals(variable)) {
         return value ? trueBranch : falseBranch;
      }

      BinaryDecisionDiagram trueBranchAssumed = trueBranch.assume(variable, value);
      BinaryDecisionDiagram falsedBranchAssumed = falseBranch.assume(variable, value);
      if (canSimplify(trueBranchAssumed, falsedBranchAssumed)) {
         return trueBranchAssumed;
      }

      return new BinaryDecisionDiagramNode(
           primaryVariable,
           variables.stream()
              .filter(v -> !v.equals(variable))
              .collect(Collectors.toSet()),
           trueBranchAssumed,
           falsedBranchAssumed);
   }

   private boolean canSimplify(
         final BinaryDecisionDiagram d1,
         final BinaryDecisionDiagram d2) {
      return d1 instanceof BinaryDecisionDiagramLeaf
         && d2 instanceof BinaryDecisionDiagramLeaf
         && d1 == d2;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o == null || !(o instanceof BinaryDecisionDiagramNode)) {
         return false;
      }

      BinaryDecisionDiagramNode other = (BinaryDecisionDiagramNode) o;
      return
         primaryVariable.equals(other.primaryVariable)
            && variables.equals(other.variables)
            && trueBranch.equals(other.trueBranch)
            && falseBranch.equals(other.falseBranch);
   }

   @Override
   public String toString() {
      return String.format(
            "[%s, true: %s, false: %s]",
            primaryVariable,
            trueBranch,
            falseBranch);
   }
}
package com.avilan.sat;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryDecisionDiagramNode implements BinaryDecisionDiagram {

   private final SATVariable primaryVariable;
   private final List<SATVariable> variables;
   private final BinaryDecisionDiagram trueBranch;
   private final BinaryDecisionDiagram falseBranch;

   public static BinaryDecisionDiagramNode of(
         final SATVariable variable) {
      return of(variable, ImmutableList.of(variable));
   }

   public static BinaryDecisionDiagramNode of(
         final SATVariable variable,
         final List<SATVariable> variables) {
      return new BinaryDecisionDiagramNode(
         variable,
         variables,
         BinaryDecisionDiagramLeaf.TRUE,
         BinaryDecisionDiagramLeaf.FALSE);
   }

   public BinaryDecisionDiagram and(final BinaryDecisionDiagram diagram) {
      if (diagram instanceof BinaryDecisionDiagramLeaf) {
         return diagram.and(this);
      }

      BinaryDecisionDiagramNode otherNode = (BinaryDecisionDiagramNode) diagram;

      if (primaryVariable.equals(otherNode.primaryVariable)) {
         BinaryDecisionDiagram intersectionTrue = trueBranch.and(otherNode.trueBranch);
         BinaryDecisionDiagram intersectionFalse = falseBranch.and(otherNode.falseBranch);

         if (intersectionTrue instanceof BinaryDecisionDiagramLeaf
               && intersectionFalse instanceof BinaryDecisionDiagramLeaf
               && intersectionTrue == intersectionFalse) {
            return intersectionTrue;
         }

         return new BinaryDecisionDiagramNode(
            primaryVariable,
            variables,
            intersectionTrue,
            intersectionFalse);
      }

      return new BinaryDecisionDiagramNode(
         primaryVariable,
         ImmutableList.<SATVariable> builder()
            .addAll(variables)
            .addAll(otherNode.variables)
            .build(),
         trueBranch.and(diagram.assume(primaryVariable, true)),
         falseBranch.and(diagram.assume(primaryVariable, false)));
   }

   public BinaryDecisionDiagram or(final BinaryDecisionDiagram diagram) {
      if (diagram instanceof BinaryDecisionDiagramLeaf) {
         return diagram.or(this);
      }

      BinaryDecisionDiagramNode otherNode = (BinaryDecisionDiagramNode) diagram;

      if (primaryVariable.equals(otherNode.primaryVariable)) {
         BinaryDecisionDiagram unionTrue = trueBranch.or(otherNode.trueBranch);
         BinaryDecisionDiagram unionFalse = falseBranch.or(otherNode.falseBranch);

         if (unionTrue instanceof BinaryDecisionDiagramLeaf
               && unionFalse instanceof BinaryDecisionDiagramLeaf
               && unionTrue == unionFalse) {
            return unionTrue;
         }

         return new BinaryDecisionDiagramNode(
            primaryVariable,
            variables,
            unionTrue,
            unionFalse);
      }

      return new BinaryDecisionDiagramNode(
         primaryVariable,
         ImmutableList.<SATVariable> builder()
            .addAll(variables)
            .addAll(otherNode.variables)
            .build(),
         trueBranch.or(diagram.assume(primaryVariable, true)),
         falseBranch.or(diagram.assume(primaryVariable, false)));
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

      return new BinaryDecisionDiagramNode(
           primaryVariable,
           variables,
           trueBranch.assume(variable, value),
           falseBranch.assume(variable, value));
   }

   @Override
   public String toString() {
      return String.format(
            "[%s | true: %s, false: %s]",
            primaryVariable,
            trueBranch,
            falseBranch);
   }
}
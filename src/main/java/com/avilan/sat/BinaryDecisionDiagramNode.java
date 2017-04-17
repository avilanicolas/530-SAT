package com.avilan.sat;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryDecisionDiagramNode implements BinaryDecisionDiagram {

   private final SATVariable variable;
   private final List<SATVariable> knownVariables;
   private final BinaryDecisionDiagram trueBranch;
   private final BinaryDecisionDiagram falseBranch;

   public static BinaryDecisionDiagram of(
         final SATVariable variable,
         final List<SATVariable> knownVariables) {
      return new BinaryDecisionDiagramNode(
         variable,
         knownVariables,
         BinaryDecisionDiagramLeaf.TRUE,
         BinaryDecisionDiagramLeaf.FALSE);
   }

   public BinaryDecisionDiagram and(final BinaryDecisionDiagram diagram) {
      if (diagram instanceof BinaryDecisionDiagramLeaf) {
         return diagram.and(this);
      }

      BinaryDecisionDiagramNode otherNode = (BinaryDecisionDiagramNode) diagram;

      if (variable.equals(otherNode.variable)) {
         BinaryDecisionDiagram intersectionTrue = trueBranch.and(otherNode.trueBranch);
         BinaryDecisionDiagram intersectionFalse = falseBranch.and(otherNode.falseBranch);

         if (intersectionTrue instanceof BinaryDecisionDiagramLeaf
               && intersectionFalse instanceof BinaryDecisionDiagramLeaf
               && intersectionTrue == intersectionFalse) {
            return intersectionTrue;
         }

         return new BinaryDecisionDiagramNode(
            variable,
            knownVariables,
            intersectionTrue,
            intersectionFalse);
      }

      return new BinaryDecisionDiagramNode(
         variable,
         knownVariables,
         trueBranch.and(diagram),
         falseBranch.and(diagram));
   }

   public BinaryDecisionDiagram or(final BinaryDecisionDiagram diagram) {
      if (diagram instanceof BinaryDecisionDiagramLeaf) {
         return diagram.or(this);
      }

      BinaryDecisionDiagramNode otherNode = (BinaryDecisionDiagramNode) diagram;

      // x ? T   OR   y ? T    ---> x ? T
      //   : F          : F           : y ? T
      //                                  : F
      // x ? z ? T  OR y ? T   ---> x ? z ? T 
      //       : F       : F              : y ? T
      //   : F                                : F
      //                              : y ? T
      //                                  : F
      // (or (and x z) y)
      if (variable.equals(otherNode.variable)) {
         BinaryDecisionDiagram unionTrue = trueBranch.or(otherNode.trueBranch);
         BinaryDecisionDiagram unionFalse = falseBranch.or(otherNode.falseBranch);

         if (unionTrue instanceof BinaryDecisionDiagramLeaf
               && unionFalse instanceof BinaryDecisionDiagramLeaf
               && unionTrue == unionFalse) {
            return unionTrue;
         }

         return new BinaryDecisionDiagramNode(
            variable,
            knownVariables,
            unionTrue,
            unionFalse);
      }

      return new BinaryDecisionDiagramNode(
         variable,
         knownVariables,
         trueBranch.or(diagram),
         falseBranch.or(diagram));
   }

   public BinaryDecisionDiagram not() {
      return new BinaryDecisionDiagramNode(
         variable,
         knownVariables,
         trueBranch.not(),
         falseBranch.not());
   }

   @Override
   public String toString() {
      return String.format(
            "[%s | true: %s, false: %s]",
            variable,
            trueBranch,
            falseBranch);
   }
}

package com.avilan.sat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryDecisionDiagramNode implements BinaryDecisionDiagram {

   private final SATVariable variable;
   private final BinaryDecisionDiagram trueBranch;
   private final BinaryDecisionDiagram falseBranch;

   public static BinaryDecisionDiagram of(final SATVariable variable) {
      return new BinaryDecisionDiagramNode(
            variable,
            BinaryDecisionDiagramLeaf.TRUE,
            BinaryDecisionDiagramLeaf.FALSE);
   }

   public BinaryDecisionDiagram or(final BinaryDecisionDiagram diagram) {

      if (diagram == BinaryDecisionDiagramLeaf.TRUE) {
         return diagram;
      } else if (diagram == BinaryDecisionDiagramLeaf.FALSE) {
         return this;
      }

      return null;
   }
}

package com.avilan.sat;

import java.util.Map;

public enum BinaryDecisionDiagramLeaf implements BinaryDecisionDiagram {
   TRUE, FALSE;

   public BinaryDecisionDiagram or(final BinaryDecisionDiagram diagram) {
      if (this == TRUE) {
         return TRUE;
      }

      return diagram;
   }

   public BinaryDecisionDiagram and(final BinaryDecisionDiagram diagram) {
      if (this == TRUE) {
         return diagram;
      }

      return FALSE;
   }
   
   public BinaryDecisionDiagram not() {
      if (this == TRUE) {
         return FALSE;
      }

      return TRUE;
   }

   public boolean satisfies(final Map<SATVariable, Boolean> values) {
      return this == TRUE;
   }

   public BinaryDecisionDiagram assume(SATVariable variable, Boolean value) {
      return this;
   }
}
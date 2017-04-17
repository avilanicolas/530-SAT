package com.avilan.sat;

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
}
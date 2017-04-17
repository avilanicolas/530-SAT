package com.avilan.sat;

public interface BinaryDecisionDiagram {
   public BinaryDecisionDiagram or(final BinaryDecisionDiagram diagram);
   public BinaryDecisionDiagram and(final BinaryDecisionDiagram diagram);
   public BinaryDecisionDiagram not();
}
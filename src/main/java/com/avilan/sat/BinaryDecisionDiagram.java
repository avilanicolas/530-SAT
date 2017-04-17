package com.avilan.sat;

import java.util.Map;

public interface BinaryDecisionDiagram {
   public BinaryDecisionDiagram or(final BinaryDecisionDiagram diagram);
   public BinaryDecisionDiagram and(final BinaryDecisionDiagram diagram);
   public BinaryDecisionDiagram not();
   public BinaryDecisionDiagram assume(final SATVariable variable, final Boolean value);
   public boolean satisfies(final Map<SATVariable, Boolean> values);
}
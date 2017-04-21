package com.avilan.sat;

import java.util.Map;

public enum DecisionDiagramLeaf implements DecisionDiagram {
   SATISFIABLE,
   UNSATISFIABLE;

   public DecisionDiagram or(final DecisionDiagram diagram) {
      if (this == SATISFIABLE) {
         return SATISFIABLE;
      }

      return diagram;
   }

   public DecisionDiagram and(final DecisionDiagram diagram) {
      if (this == SATISFIABLE) {
         return diagram;
      }

      return UNSATISFIABLE;
   }
   
   public DecisionDiagram not() {
      if (this == SATISFIABLE) {
         return UNSATISFIABLE;
      }

      return SATISFIABLE;
   }

   public boolean satisfies(final Map<SATVariable, Boolean> values) {
      return this == SATISFIABLE;
   }

   public DecisionDiagram assume(SATVariable variable, Boolean value) {
      return this;
   }

   @Override
   public String toString() {
      return "\"" + name() + "\"";
   }
}
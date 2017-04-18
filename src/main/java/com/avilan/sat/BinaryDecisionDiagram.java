package com.avilan.sat;

import java.util.Map;

/**
 * A data structure which represents a boolean function.
 * {@link https://en.wikipedia.org/wiki/Binary_decision_diagram}
 *
 * Due to the ordering of variables within a diagram, any logical operation on them
 * is not guaranteed to be associative.
 */
public interface BinaryDecisionDiagram {
   /**
    * Performs a logical union on two diagrams.
    */
   public BinaryDecisionDiagram or(final BinaryDecisionDiagram diagram);

   /**
    * Performs a logical intersection on two diagrams.
    */
   public BinaryDecisionDiagram and(final BinaryDecisionDiagram diagram);

   /**
    * Performs a logical negation on this diagram.
    */
   public BinaryDecisionDiagram not();

   /**
    * Constrains a diagram by assuming a value for a variable possibly within the diagram.
    *
    * This can be used to simplify diagrams. For example, a diagram D constructed from the
    * boolean equation:
    *    D(x, y, z) = (x and y) or z
    * Can be simplified if the value of any variable is known. For example,
    * D.assume(z, true) should constrain the diagram to be equivalent to true.
    * D.assume(x, false) should constrain the diagram to be equivalent to z.
    *
    * variable does not need to exist within this diagram to be assumed.
    */
   public BinaryDecisionDiagram assume(final SATVariable variable, final Boolean value);

   /**
    * Determines if this diagram satisfies the assignment of variables in values.
    */
   public boolean satisfies(final Map<SATVariable, Boolean> values);
}
package com.csc530.sat;

import java.util.Map;
import java.util.Set;

/**
 * A data structure which represents a boolean function.
 * {@link https://en.wikipedia.org/wiki/Binary_decision_diagram}
 *
 * Commutativity is not guaranteed on any compositional interface.
 */
public interface DecisionDiagram {
    /**
     * Performs a logical union on two diagrams.
     */
    public DecisionDiagram or(final DecisionDiagram diagram);

    /**
     * Performs a logical intersection on two diagrams.
     */
    public DecisionDiagram and(final DecisionDiagram diagram);

    /**
     * Performs a logical xor of this diagram with another
     */
    public default DecisionDiagram xor(final DecisionDiagram other) {
        return and(other.not()).or(other.and(not()));
    }

    /**
     * Performs a logical negation on this diagram.
     */
    public DecisionDiagram not();


    /**
     * Constrains a diagram by assuming a value for a variable possibly within
     * the diagram.
     *
     * This can be used to simplify diagrams. For example, a diagram D
     * constructed from the boolean equation: D(x, y, z) = (x and y) or z Can be
     * simplified if the value of any variable is known. For example,
     * D.assume(z, true) should constrain the diagram to be equivalent to true
     * D.assume(x, false) should constrain the diagram to be equivalent to z.
     *
     * variable does not need to exist within this diagram to be assumed.
     */
    public DecisionDiagram assume(final SATVariable variable, final Boolean value);

    /**
     * Determines if assignment contains an assignment of variables which
     * satisfies this diagram.
     */
    public boolean satisfies(final Map<SATVariable, Boolean> assignment);

    /**
     * Finds all the possible cases that satisfy this boolean equation. Note
     * that not all variables my be mapped in this representation, for mappings
     * that contain less than the number or variables the variables that are not
     * present do not matter (i.e. they may be either true or false)
     * 
     * 
     * @return all possible mappings of the enclosed sat variables that satisfy
     *         the boolean condition represented herein
     */
    public Set<Map<SATVariable, Boolean>> satisifyAll();
}
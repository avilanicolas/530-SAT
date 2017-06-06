package com.csc530.sat;

import java.util.Map;
import java.util.stream.Stream;

import com.csc530.sat.type.DDType;

@SuppressWarnings("rawtypes")
public enum DecisionDiagramLeaf implements DecisionDiagram {
    SATISFIABLE, UNSATISFIABLE;

    @Override
    public DecisionDiagram or(DecisionDiagram diagram) {
        if (this == SATISFIABLE) {
            return SATISFIABLE;
        }
        return diagram;
    }

    @Override
    public DecisionDiagram and(DecisionDiagram diagram) {
        if (this == SATISFIABLE) {
            return diagram;
        }
        return UNSATISFIABLE;
    }

    @Override
    public DecisionDiagram not() {
        if (this == SATISFIABLE) {
            return UNSATISFIABLE;
        }
        return SATISFIABLE;
    }

    @Override
    public DecisionDiagram assume(Variable variable, DDType value) {
        return this;
    }

    @Override
    public boolean satisfies(Map<Variable, DDType> assignment) {
        return this == SATISFIABLE;
    }

    @Override
    public String toString() {
        return "\"" + name() + "\"";
    }

    @Override
    public Stream<Map<Variable, DDType>> satisifyAll() {
        return Stream.empty();
    }
}
package com.csc530.sat.branch;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.csc530.sat.DecisionDiagram;
import com.csc530.sat.Variable;
import com.csc530.sat.condition.DDCondition;
import com.csc530.sat.type.DDType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Decision<T> {
    private final Variable<T> primaryVariable;
    private final DDCondition<T> condition;
    private final DecisionDiagram trueBranch;
    private final DecisionDiagram falseBranch;

    @Builder(toBuilder = true)
    public static <T> Decision<T> create(Variable<T> primaryVariable,
            DDCondition<T> condition,
            DecisionDiagram trueBranch, DecisionDiagram falseBranch) {
        return new Decision<T>(primaryVariable, condition, trueBranch,
                falseBranch);
    }

    public DecisionDiagram getBranch(DDType<T> value) {
        return condition.satisifies(value) ? trueBranch : falseBranch;
    }

    public Decision<T> not() {
        return toBuilder()
                .condition(condition.not())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof Decision)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        Decision other = (Decision) o;

        return new EqualsBuilder()
                .append(primaryVariable, other.primaryVariable)
                .append(condition, other.condition)
                .append(trueBranch, other.trueBranch)
                .append(falseBranch, other.falseBranch)
                .isEquals();
    }
}

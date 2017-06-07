package com.csc530.sat.condition.variable;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.csc530.sat.condition.DDCondition;
import com.csc530.sat.type.DDPair;
import com.csc530.sat.type.DDType;
import com.google.common.base.Preconditions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VariableNEQ<T> implements DDCondition<Pair<DDType<T>, DDType<T>>> {
    private final DDType<T> first;
    private final DDType<T> second;

    public static <T> VariableNEQ<T> create(DDType<T> first, DDType<T> second) {
        Preconditions.checkArgument(!first.getValue().equals(second.getValue()),
                "values must be different");
        return new VariableNEQ<T>(first, second);
    }

    @Override
    public boolean satisifies(DDType<Pair<DDType<T>, DDType<T>>> value) {
        return !value.getValue().getRight().getValue()
                .equals(value.getValue().getLeft().getValue());
    }

    @Override
    public DDType<Pair<DDType<T>, DDType<T>>> satisifier() {
        return DDPair.create(ImmutablePair.of(first, second));
    }

    @Override
    public DDType<Pair<DDType<T>, DDType<T>>> unSatisifier() {
        return DDPair.create(ImmutablePair.of(first, first));
    }

    @Override
    public DDCondition<Pair<DDType<T>, DDType<T>>> not() {
        return VariableEQ.create(first, second);
    }
}

package com.csc530.sat.type;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegerPair implements DDType<Pair<DDType<Integer>, DDType<Integer>>> {
    private final Pair<DDType<Integer>, DDType<Integer>> value;
    
    public static IntegerPair create(Pair<DDType<Integer>, DDType<Integer>> value) {
        return new IntegerPair(value);
    }

    @Override
    public Pair<DDType<Integer>, DDType<Integer>> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || !(o instanceof IntegerPair)) {
            return false;
        }

        IntegerPair other = (IntegerPair) o;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(value)
                .build()
                .hashCode();
    }
}

package com.csc530.sat.type;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DDPair<T> implements DDType<Pair<DDType<T>, DDType<T>>> {
    private final Pair<DDType<T>, DDType<T>> value;

    /**
     * what do you mean java is type safe?
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static DDPair createUnsafe(Pair pair) {
        return create(pair);
    }

    public static <T> DDPair<T> create(Pair<DDType<T>, DDType<T>> value) {
        return new DDPair<T>(value);
    }

    @Override
    public Pair<DDType<T>, DDType<T>> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || !(o instanceof DDPair)) {
            return false;
        }

        DDPair<?> other = (DDPair<?>) o;
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

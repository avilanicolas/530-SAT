package com.csc530.sat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@ToString
@Getter
public class Variable<C> {
    private String name;
    private Class<C> valueClass;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || !(o instanceof Variable)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        Variable other = (Variable) o;
        return new EqualsBuilder()
                .append(name, other.name)
                .append(valueClass, other.valueClass)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(valueClass)
                .build()
                .hashCode();
    }
}
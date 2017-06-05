package com.csc530.sat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Builder
@AllArgsConstructor
@ToString
public class SMTVariable<C> {
    private String name;
    private Class<C> valueClass;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || !(o instanceof SMTVariable)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        SMTVariable other = (SMTVariable) o;
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
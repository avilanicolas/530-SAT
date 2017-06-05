package com.csc530.sat.type.integer;

import com.csc530.sat.type.DDType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class IntegerDDType implements DDType<Integer> {
    private int value;

    public static IntegerDDType create(int value) {
        return new IntegerDDType(value);
    }

    @Override
    public Integer getValue() {
        return value;
    }

}

package com.csc530.sat.type.bool;

import com.csc530.sat.type.DDType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BooleanDDType implements DDType<Boolean> {
    TRUE {
        @Override
        public Boolean getValue() {
            return true;
        }
    },
    FALSE {
        @Override
        public Boolean getValue() {
            return false;
        }
    };

    public static BooleanDDType valueOf(Boolean b) {
        return b ? TRUE : FALSE;
    }
}

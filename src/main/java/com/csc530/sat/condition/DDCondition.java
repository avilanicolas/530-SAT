package com.csc530.sat.condition;

import com.csc530.sat.type.DDType;

/**
 * 
 * A condition over a type
 *
 * @param <T>
 *            the type class this condition is over
 */
public interface DDCondition<T> {
    /**
     * 
     * @param value
     * @return true if the value satisfies the condition
     */
    public boolean satisifies(DDType<T> value);

    /**
     * 
     * @return a value that satisfies this condition null if no value satisfies
     *         it
     */
    public DDType<T> satisifier();

    /**
     * 
     * @return a value that does not satisfy this condition null if no value
     *         does not satisfy it
     */
    public DDType<T> unSatisifier();

    /**
     * 
     * @return the inverse of this condition
     */
    public DDCondition<T> not();
}

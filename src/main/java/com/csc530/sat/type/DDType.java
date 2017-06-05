package com.csc530.sat.type;

/**
 * A type that the decision diagram can reason about
 *
 * @param <T>
 *            the java type that this type describes
 */
public interface DDType<T> {
    /**
     * 
     * @return the java type value for this type
     */
    public T getValue();
}

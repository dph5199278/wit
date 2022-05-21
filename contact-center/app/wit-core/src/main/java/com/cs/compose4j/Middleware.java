package com.cs.compose4j;

/**
 * The interface Middleware.
 *
 * @param <T> the type parameter
 */
public interface Middleware<T extends AbstractContext> {

    /**
     * Apply.
     *
     * @param var1 the var 1
     * @param var2 the var 2
     */
    void apply(T var1, Functional<T> var2);
}

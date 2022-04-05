package com.cs.compose4j;

import java.util.Deque;

/**
 * The type Functional.
 *
 * @param <T> the type parameter
 */
public class Functional<T extends AbstractContext> {

    /**
     * Instantiates a new Functional.
     *
     * @param context  the context
     * @param deque    the deque
     */
    public Functional(final T context, final Deque<Middleware> deque) {
        this.context = context;
        this.deque = deque;
    }

    private final T context;
    private final Deque<Middleware> deque;

    /**
     * Apply.
     */
    public void apply() {
        if (!deque.isEmpty()) {
            deque.poll().apply(context, this);
        }
    }
}

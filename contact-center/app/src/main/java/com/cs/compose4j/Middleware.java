package com.cs.compose4j;

public interface Middleware<T extends AbstractContext> {
    void apply(T var1, Functional var2);
}

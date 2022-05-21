package com.cs.compose4j;

import com.cs.compose4j.exception.Compose4jRuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Composer.
 *
 * @param <T> the type parameter
 */
public class Composer<T extends AbstractContext> {
    private final List<Middleware<T>> middlewares = new ArrayList<>();

    /**
     * Instantiates a new Composer.
     */
    public Composer() {
    }

    /**
     * 使用中间件
     *
     * @param mwArr 中间件数组
     * @return the composer
     */
    @SafeVarargs
    public final Composer<T> use(Middleware<T>... mwArr) {
        if(null == mwArr || 0 == mwArr.length) {
            return this;
        }
        if(1 == mwArr.length) {
            this.middlewares.add(mwArr[0]);
            return this;
        }

        return use(Arrays.asList(mwArr));
    }

    /**
     * 使用中间件
     *
     * @param mwCollection 中间件集合
     * @return the composer
     */
    public Composer<T> use(Collection<Middleware<T>> mwCollection) {
        if(null == mwCollection || mwCollection.isEmpty()) {
            return this;
        }
        if(1 == mwCollection.size()) {
            this.middlewares.add(mwCollection.iterator().next());
            return this;
        }

        this.middlewares.addAll(mwCollection);
        return this;
    }

    /**
     * 处理上下文
     *
     * @param context 上下文
     * @throws Compose4jRuntimeException Middleware(s) not found
     */
    public void handle(final T context) throws Compose4jRuntimeException {
        if (this.middlewares.size() > 0) {
            new Functional<>(context, new LinkedList<>(this.middlewares)).apply();
        } else {
            throw new Compose4jRuntimeException("Middleware(s) not found");
        }
    }
}

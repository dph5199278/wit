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
    private final List<Middleware> middlewares = new ArrayList<>();

    /**
     * Instantiates a new Composer.
     */
    public Composer() {
    }

    /**
     * 使用中间件
     *
     * @param mw 中间件
     * @return the composer
     */
    public Composer<T> use(Middleware mw) {
        this.middlewares.add(mw);
        return this;
    }

    /**
     * Use composer.
     *
     * @param mwArr the mw arr
     * @return the composer
     */
    public Composer<T> use(Middleware... mwArr) {
        if(null == mwArr || 0 == mwArr.length) {
            return this;
        }
        if(1 == mwArr.length) {
            return use(mwArr[0]);
        }

        return use(Arrays.asList(mwArr));
    }

    /**
     * Use composer.
     *
     * @param mwCollection the mw collection
     * @return the composer
     */
    public Composer<T> use(Collection<Middleware> mwCollection) {
        if(null == mwCollection || mwCollection.isEmpty()) {
            return this;
        }
        if(1 == mwCollection.size()) {
            return use(mwCollection.iterator().next());
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

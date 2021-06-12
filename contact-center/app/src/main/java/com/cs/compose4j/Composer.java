package com.cs.compose4j;

import com.cs.compose4j.exception.Compose4jRuntimeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Composer<T extends AbstractContext> {
    private final List<Middleware> middlewares = new ArrayList();

    public Composer() {
    }

    /**
     * 使用中间件
     * @param mw 中间件
     */
    public void use(Middleware mw) {
        this.middlewares.add(mw);
    }

    /**
     * 处理上下文
     * @param context 上下文
     * @throws Compose4jRuntimeException
     */
    public void handle(final T context) throws Compose4jRuntimeException {
        if (this.middlewares.size() > 0) {
            final Iterator<Middleware> it = new ArrayList<>(this.middlewares).iterator();
            it.next().apply(context, new Functional() {
                public void apply() {
                    if (it.hasNext()) {
                        it.next().apply(context, this);
                    }
                }
            });
        } else {
            throw new Compose4jRuntimeException("Middleware(s) not found");
        }
    }
}

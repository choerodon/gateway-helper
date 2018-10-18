package io.choerodon.gateway.helper.cache;

import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.UsesJava8;

import java.util.concurrent.Callable;

@UsesJava8
public class MultiCache extends AbstractValueAdaptingCache {

    public MultiCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    @Override
    protected Object lookup(Object o) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public <T> T get(Object o, Callable<T> callable) {
        return null;
    }

    @Override
    public void put(Object o, Object o1) {

    }

    @Override
    public ValueWrapper putIfAbsent(Object o, Object o1) {
        return null;
    }

    @Override
    public void evict(Object o) {

    }

    @Override
    public void clear() {

    }
}

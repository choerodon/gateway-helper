package io.choerodon.gateway.helper.cache.multi;

import io.choerodon.gateway.helper.cache.l2.L2Cache;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class MultiL2Cache extends MultiCache {

    private final Cache l2Cache;

    public MultiL2Cache(String name, boolean allowNullValues, L2Cache l2Cache) {
        super(name, allowNullValues);
        this.l2Cache = l2Cache.getCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        return l2Cache.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return l2Cache.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return l2Cache.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
       l2Cache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return l2Cache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
       l2Cache.evict(key);
    }

    @Override
    public void clear() {
      l2Cache.clear();
    }
}

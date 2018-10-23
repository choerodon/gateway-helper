package io.choerodon.gateway.helper.infra.cache.multi;

import org.springframework.cache.Cache;
import org.springframework.cache.support.NullValue;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.UsesJava8;

@UsesJava8
public abstract class MultiCache implements Cache {

    private final String name;

    private final boolean allowNullValues;

    public MultiCache(String name, boolean allowNullValues) {
        this.name = name;
        this.allowNullValues = allowNullValues;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public final Object getNativeCache() {
        return this;
    }

    protected Object fromStoreValue(Object storeValue) {
        if (this.allowNullValues && storeValue == NullValue.INSTANCE) {
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(Object userValue) {
        if (this.allowNullValues && userValue == null) {
            return NullValue.INSTANCE;
        }
        return userValue;
    }

    protected Cache.ValueWrapper toValueWrapper(Object storeValue) {
        return (storeValue != null ? new SimpleValueWrapper(fromStoreValue(storeValue)) : null);
    }
}

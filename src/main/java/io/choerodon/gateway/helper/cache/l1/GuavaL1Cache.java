package io.choerodon.gateway.helper.cache.l1;


import com.google.common.cache.Cache;
import io.choerodon.gateway.helper.cache.bridge.L1ToL2Bridge;
import org.springframework.cache.guava.GuavaCache;

public class GuavaL1Cache extends GuavaCache implements L1Cache {

    private static final String CACHE_TYPE_GUAVA = "guava";

    public GuavaL1Cache(String name, Cache<Object, Object> cache) {
        super(name, cache);
    }

    @Override
    public String type() {
        return CACHE_TYPE_GUAVA;
    }

    @Override
    public L1ToL2Bridge bridge() {
        return null;
    }
}

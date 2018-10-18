package io.choerodon.gateway.helper.cache.l1;

import com.github.benmanes.caffeine.cache.Cache;
import io.choerodon.gateway.helper.cache.bridge.L1ToL2Bridge;
import org.springframework.cache.caffeine.CaffeineCache;

public class CaffeineL1Cache extends CaffeineCache implements L1Cache {

    private static final String CACHE_TYPE_CAFFEINE = "caffeine";

    public CaffeineL1Cache(String name, Cache<Object, Object> cache) {
        super(name, cache);
    }

    @Override
    public String type() {
        return CACHE_TYPE_CAFFEINE;
    }

    @Override
    public L1ToL2Bridge bridge() {
        return null;
    }
}

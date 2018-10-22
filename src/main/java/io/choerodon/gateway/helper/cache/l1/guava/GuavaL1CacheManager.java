package io.choerodon.gateway.helper.cache.l1.guava;

import io.choerodon.gateway.helper.cache.l1.L1Cache;
import io.choerodon.gateway.helper.cache.l1.L1CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.util.StringUtils;

public class GuavaL1CacheManager extends GuavaCacheManager implements L1CacheManager {

    private static final String CACHE_TYPE_GUAVA = "guava";

    @Override
    public L1Cache getL1Cache(String name, String spec) {
        synchronized (this) {
            if (spec != null && StringUtils.hasText(spec)) {
                this.setCacheSpecification(spec);
            }
            Cache cache = this.getCache(name);
            if (cache != null) {
                return new GuavaL1Cache(cache);
            }
            return null;
        }
    }

    public static String type() {
        return CACHE_TYPE_GUAVA;
    }
}

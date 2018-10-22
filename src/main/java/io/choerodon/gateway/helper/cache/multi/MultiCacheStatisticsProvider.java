package io.choerodon.gateway.helper.cache.multi;

import org.springframework.boot.actuate.cache.CacheStatistics;
import org.springframework.boot.actuate.cache.CacheStatisticsProvider;
import org.springframework.cache.CacheManager;

public class MultiCacheStatisticsProvider implements CacheStatisticsProvider<MultiCache>  {

    @Override
    public CacheStatistics getCacheStatistics(CacheManager cacheManager, MultiCache cache) {
        return null;
    }
}

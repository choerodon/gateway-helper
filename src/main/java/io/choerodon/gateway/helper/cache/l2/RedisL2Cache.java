package io.choerodon.gateway.helper.cache.l2;

import io.choerodon.gateway.helper.cache.bridge.L2ToSourceBridge;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisOperations;

public class RedisL2Cache extends RedisCache implements L2Cache {

    private static final String CACHE_TYPE_REDIS = "redis";

    public RedisL2Cache(String name, byte[] prefix, RedisOperations<?, ?> redisOperations, long expiration) {
        super(name, prefix, redisOperations, expiration);
    }

    @Override
    public String type() {
        return CACHE_TYPE_REDIS;
    }

    @Override
    public L2ToSourceBridge bridge() {
        return null;
    }
}

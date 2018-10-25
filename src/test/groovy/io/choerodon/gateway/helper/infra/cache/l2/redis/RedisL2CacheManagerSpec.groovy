package io.choerodon.gateway.helper.infra.cache.l2.redis

import org.springframework.data.redis.cache.RedisCache
import org.springframework.data.redis.core.RedisOperations
import spock.lang.Specification

class RedisL2CacheManagerSpec extends Specification {

    def "test type"() {
        when:
        String result = RedisL2CacheManager.type()

        then:
        result == "redis"
    }

    def "test get L2 Cache"() {
        when:
        def result = new RedisL2CacheManager(Mock(RedisOperations)).getL2Cache("name", "expiration=1800")

        then:
        result != null
        result.getCache() instanceof RedisCache
    }
}

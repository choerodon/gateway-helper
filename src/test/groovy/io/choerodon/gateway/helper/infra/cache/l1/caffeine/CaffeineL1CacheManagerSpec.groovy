package io.choerodon.gateway.helper.infra.cache.l1.caffeine

import org.springframework.cache.caffeine.CaffeineCache
import spock.lang.Specification

class CaffeineL1CacheManagerSpec extends Specification {

    def "test get L1 Cache"() {
        when: '调用getL1Cache'
        def result = new CaffeineL1CacheManager().getL1Cache("test", "initialCapacity=50,maximumSize=500,expireAfterWrite=600s")

        then:
        result != null
        result.getCache() instanceof CaffeineCache
        result.getCache().getName() == 'test'
    }
}
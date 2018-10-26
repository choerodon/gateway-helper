package io.choerodon.gateway.helper.infra.cache.l1.guava


import org.springframework.cache.guava.GuavaCache
import spock.lang.Specification

class GuavaL1CacheManagerSpec extends Specification {

    def "test get L1 Cache"() {
        when:
        def result = new GuavaL1CacheManager().getL1Cache("test", "maximumSize=500,expireAfterAccess=600s")

        then:
        result != null
        result.getCache().getName() == 'test'
        result.getCache() instanceof GuavaCache
    }

    def "test type"() {
        when:
        String result = GuavaL1CacheManager.type()

        then:
        result == "guava"
    }
}
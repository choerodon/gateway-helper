package io.choerodon.gateway.helper.api.filter

import io.choerodon.gateway.helper.domain.CheckRequest
import io.choerodon.gateway.helper.domain.CheckResponse
import io.choerodon.gateway.helper.domain.RequestContext
import org.springframework.cloud.config.client.ZuulRoute
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import spock.lang.Specification

class CollectSpanFilterSpec extends Specification {

    StringRedisTemplate stringRedisTemplate = Mock(StringRedisTemplate)

    CollectSpanFilter collectSpanFilter = new CollectSpanFilter(stringRedisTemplate)

    def "FilterOrder"() {
        when:
        def result = collectSpanFilter.filterOrder()
        then:
        result == 15
    }

    def "ShouldFilter"() {
        when:
        def result = collectSpanFilter.shouldFilter()
        then:
        result == true
    }

    def "Run"() {
        given:
        CheckRequest request = new CheckRequest(null, "url", "get")
        RequestContext requestContext = new RequestContext(request, Mock(CheckResponse))
        ZuulRoute route = Mock(ZuulRoute)
        requestContext.setRoute(route)
        requestContext.setTrueUri("/v1/users/self")
        route.getServiceId() >> "iam-service"

        stringRedisTemplate.hasKey(_) >> false
        stringRedisTemplate.opsForValue() >> Mock(ValueOperations)

        when:
        def result = collectSpanFilter.run(requestContext)

        then:
        result == true

    }
}

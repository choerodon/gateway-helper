package io.choerodon.gateway.helper.api.filter

import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.gateway.helper.api.service.GetUserDetailsService
import io.choerodon.gateway.helper.domain.*
import spock.lang.Specification

class GetUserDetailsFilterSpec extends Specification {

    def "test filter Order"() {
        when:
        int result = new GetUserDetailsFilter(null).filterOrder()

        then:
        result == 40
    }

    def "test should Filter"() {
        when:
        boolean result = new GetUserDetailsFilter(null).shouldFilter(null)

        then:
        result
    }

    def "test run"() {
        given: '创建context'
        def noTokenContext = new RequestContext(
                new CheckRequest(null, "uri", "method"),
                new CheckResponse(jwt: "jwt", message: "message", status: CheckState.SUCCESS_PASS_SITE))
        def hasTokenContext = new RequestContext(
                new CheckRequest('access', "uri", "method"),
                new CheckResponse(jwt: "jwt", message: "message", status: CheckState.SUCCESS_PASS_SITE))
        and: 'mock service'
        def noDetailService = Mock(GetUserDetailsService) {
            getUserDetails(_) >> new CustomUserDetailsWithResult(null, CheckState.PERMISSION_SERVICE_ROUTE)
        }
        def hasDetailService = Mock(GetUserDetailsService) {
            getUserDetails(_) >> new CustomUserDetailsWithResult(new CustomUserDetails('user', 'pass', Collections.emptyList()),
                    CheckState.PERMISSION_SERVICE_ROUTE)
        }
        def getUserDetailsFilter = new GetUserDetailsFilter(noDetailService)

        when: '当accessToken为空'
        def result1 = getUserDetailsFilter.run(noTokenContext)

        then: '验证结果'
        !result1
        noTokenContext.response.status == CheckState.PERMISSION_ACCESS_TOKEN_NULL

        when: '无法获取userDetails'
        def result2 = getUserDetailsFilter.run(hasTokenContext)

        then: '验证结果'
        !result2
        noTokenContext.response.status != null

        when: '获取userDetails'
        getUserDetailsFilter.setGetUserDetailsService(hasDetailService)
        def result3 = getUserDetailsFilter.run(hasTokenContext)

        then: '验证结果'
        result3
        hasTokenContext.customUserDetails != null
    }
}
package io.choerodon.gateway.helper.common.utils

import com.netflix.hystrix.exception.HystrixRuntimeException
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.cloud.config.client.ZuulRoute
import org.springframework.cloud.config.helper.HelperZuulRoutesProperties
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory
import org.springframework.http.client.ClientHttpResponse
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest


/**
 * Created by superlee on 2018/9/20.
 */
@PrepareForTest([RequestContext.class, RequestRibbonForwardUtils.class])
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class RequestRibbonForwardUtilsSpec extends Specification {

    def "BuildCommandContext"() {

        given: "构造request.getHeaderNames()返回对象"
        Set<String> set = new HashSet<>()
        set << "Accept"
        set << "Jwt_Token"
        set << "Host"
        def headerNames = Collections.enumeration(set)

        and: "mock"
        def request = Mock(HttpServletRequest)

        when: "调用"
        def ctx = RequestRibbonForwardUtils.buildCommandContext(request, new ArrayList<RibbonRequestCustomizer>(),
                "gateway-helper", "/iam/v1/users/self?userId=1&name=kangkang")

        then: "预期与结果"
        1 * request.getHeaderNames() >> headerNames
        1 * request.getHeaders(_) >> headerNames
        1 * request.getQueryString() >> queryString
        ctx != result

        where: "条件"
        queryString                                 || result
        "/iam/v1/users/self?userId=1&name=kangkang" || null
        "/iam/v1/users/self?userId=1&name"          || null

    }

    def "BuildZuulRequestUri"() {
        given: "mock"
        def request = Mock(HttpServletRequest)
        PowerMockito.mockStatic(RequestContext.class)
        def ctx = Mock(RequestContext)
        PowerMockito.when(RequestContext.getCurrentContext()).thenReturn(ctx)

        when: "调用"
        def value = RequestRibbonForwardUtils.buildZuulRequestUri(request)

        then: "预期与结果"
        _ * ctx.get(_) >> "/iam/v1/users/self"
        _ * request.getRequestURI() >> "/iam/v1/users/self"
        "/iam/v1/users/self" == value
    }

    def "Forward"() {
        given: "mock"
        def context = Mock(RibbonCommandContext)
        def ribbonCommandFactory = Mock(RibbonCommandFactory)
        def command = Mock(RibbonCommand)
        def exception =
                new HystrixRuntimeException(HystrixRuntimeException.FailureType.COMMAND_EXCEPTION,
                        null, "message", new NullPointerException(), null)

        when: "调用"
        def clientHttpResponse = RequestRibbonForwardUtils.forward(context, ribbonCommandFactory)

        then: "预期与结果"
        (0..1) * ribbonCommandFactory.create(_) >> command
        (0..1) * command.execute() >> Mock(ClientHttpResponse)
        clientHttpResponse != null

        when: "抛异常"
        RequestRibbonForwardUtils.forward(context, ribbonCommandFactory)

        then: "预期与结果"
        (0..1) * ribbonCommandFactory.create(_) >> command
        (0..1) * command.execute() >> { throw exception }
        thrown(IllegalStateException)
    }

    def "GetHelperServiceByUri"() {

        given: "mock"
        def helperZuulRoutesProperties = Mock(HelperZuulRoutesProperties)
        def zuulRoute = Mock(ZuulRoute)
        def map = Mock(Map)
        helperZuulRoutesProperties.getRoutes() >> map
        map.get(_) >> zuulRoute
        zuulRoute.setHelperService('aaa')

        when: "调用"
        def value = RequestRibbonForwardUtils.getHelperServiceByUri(helperZuulRoutesProperties, uri)


        then: "预期与结果"
        value == result

        where: "条件"
        uri       || result
        ""        || null
        "aaa/bbb" || null
//        "aaa/bbb" || "aaa"

    }
}

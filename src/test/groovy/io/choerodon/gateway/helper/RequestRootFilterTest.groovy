package io.choerodon.gateway.helper

import com.netflix.zuul.context.RequestContext
import io.choerodon.gateway.helper.common.utils.RequestRibbonForwardUtils
import io.choerodon.gateway.helper.permission.RequestPermissionFilter
import io.choerodon.gateway.helper.ratelimit.RequestRatelimitFilter
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.cloud.config.client.ZuulRoute
import org.springframework.cloud.config.helper.HelperZuulRoutesProperties
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * Created by superlee on 2018/9/20.
 */
@PrepareForTest([RequestContext.class, RequestRibbonForwardUtils.class])
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class RequestRootFilterTest extends Specification {

    def "Filter"() {
        given: "mock构造方法参数"
        def helperZuulRoutesProperties = Mock(HelperZuulRoutesProperties)
        def requestPermissionFilter = Mock(RequestPermissionFilter)
        def requestRatelimitFilter = Mock(RequestRatelimitFilter)
        def ribbonCommandFactory = Mock(RibbonCommandFactory)
        def request = Mock(HttpServletRequest)
        def route = Mock(ZuulRoute)

        and: "mock静态方法"
        PowerMockito.mockStatic(RequestContext.class)
        def ctx = Mock(RequestContext)
        PowerMockito.when(RequestContext.getCurrentContext()).thenReturn(ctx)

        PowerMockito.mockStatic(RequestRibbonForwardUtils.class)
        def commandContext = Mock(RibbonCommandContext)
        PowerMockito.when(RequestRibbonForwardUtils.buildCommandContext(Mockito.any(), Mockito.anyList(), Mockito.anyString(), Mockito.anyString())).thenReturn(commandContext)
        def clientHttpResponse = Mock(ClientHttpResponse)
        PowerMockito.when(RequestRibbonForwardUtils.forward(commandContext, ribbonCommandFactory)).thenReturn(clientHttpResponse)


        and: "构造RequestRootFilter"
        def requestRootFilter = new RequestRootFilter(helperZuulRoutesProperties, requestPermissionFilter, requestRatelimitFilter, ribbonCommandFactory)

        when: "调用"
        def value = requestRootFilter.filter(request)

        then: "预期与结果"
        (_..2) * request.getRequestURI() >> "/manager/v1/swaggers/resources"
        (_..2) * ctx.get(_) >> "/manager/v1/swaggers/resources"
        (_..2) * helperZuulRoutesProperties.getRoutes().get(_) >> route
        (_..2) * route.getHelperService() >> service
        (_..1) * requestPermissionFilter.permission(_) >> true
        (_..1) * requestRatelimitFilter.through(_) >> true
//        (_..1) * clientHttpResponse.getStatusCode().is2xxSuccessful() >> true
        value == result

        and: "customGatewayHelperFilter"
        PowerMockito.when(RequestRibbonForwardUtils.buildZuulRequestUri(request)).thenReturn("abc")
        PowerMockito.when(RequestRibbonForwardUtils.getHelperServiceByUri(Mockito.any(), Mockito.anyString())).thenReturn("api-gateway")

        when: "调用"
        def value1 = requestRootFilter.filter(request)

        then: "预期与结果"
        1 * clientHttpResponse.getStatusCode() >> HttpStatus.valueOf(200)
        value1 == true

        where: "条件"
        service       || result
        ""            || true
        "api-gateway" || true

    }
}

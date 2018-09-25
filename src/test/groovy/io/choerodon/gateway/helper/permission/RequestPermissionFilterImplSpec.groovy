package io.choerodon.gateway.helper.permission

import io.choerodon.core.iam.ResourceLevel
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.core.oauth.DetailsHelper
import io.choerodon.gateway.helper.common.utils.ZuulPathUtils
import io.choerodon.gateway.helper.permission.domain.PermissionDO
import io.choerodon.gateway.helper.permission.mapper.PermissionMapper
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.powermock.reflect.Whitebox
import org.spockframework.runtime.Sputnik
import org.springframework.cloud.config.client.ZuulRoute
import org.springframework.cloud.config.helper.HelperZuulRoutesProperties
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * Created by superlee on 2018/9/21.
 */
@PrepareForTest([ZuulPathUtils.class, DetailsHelper.class])
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class RequestPermissionFilterImplSpec extends Specification {

    def "Permission"() {
        given: "mock"
        def request = Mock(HttpServletRequest)
        def helperZuulRoutesProperties = Mock(HelperZuulRoutesProperties)
        def permissionProperties = Mock(PermissionProperties)
        def permissionMapper = Mock(PermissionMapper)
        and: "构造RequestPermissionFilterImpl"
        def impl = new RequestPermissionFilterImpl(helperZuulRoutesProperties, permissionProperties, permissionMapper)

        when: "调用直接返回true"
        def value1 = impl.permission(request)

        then: "预期与结果"
        1 * permissionProperties.isEnabled() >> false
        value1 == true

        when: "skipPath直接返回true"
        def value2 = impl.permission(request)

        then: "预期与结果"
        1 * permissionProperties.isEnabled() >> true
        1 * request.getRequestURI() >> "/zuul/iam/v1/upload"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload"]
        value2 == true

        and: "mock工具类"
        PowerMockito.mockStatic(ZuulPathUtils.class)
        PowerMockito.when(ZuulPathUtils.getRoute(request.getRequestURI(), helperZuulRoutesProperties.getRoutes())).thenReturn(null)

        when: "route == null返回false"
        def value3 = impl.permission(request)

        then: "预期与结果"
        1 * permissionProperties.isEnabled() >> true
        2 * request.getRequestURI() >> "/zuul/iam/v1/users"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload"]
        value3 == false

        and: "测试route非空的情况"
        def zuulRoute = Mock(ZuulRoute)
        PowerMockito.when(ZuulPathUtils.getRoute(Mockito.anyString(), Mockito.anyMap())).thenReturn(zuulRoute)
        PowerMockito.when(ZuulPathUtils.getRequestTruePath(Mockito.anyString(), Mockito.anyString())).thenReturn("aaa")
        PowerMockito.mockStatic(DetailsHelper.class)
        def userDetail = Mock(CustomUserDetails)
        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(userDetail)

        when: "调用"
        def value4 = impl.permission(request)

        then: "预期与结果"
        2 * userDetail.getAdmin() >> true
        1 * permissionProperties.isEnabled() >> true
        1 * request.getRequestURI() >> "/zuul/iam/v1/users1"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload1"]
        1 * permissionMapper.selectWithinPermissionsByServiceName(_) >> new ArrayList<>()
        value4 == true

        and: "测试passWithinPermissionBySql return false"
        def permission = new PermissionDO()
        permission.setPath("aaa")
        permission.setMethod("get")
        def list = new ArrayList<PermissionDO>()
        list << permission

        when: "调用"
        def value5 = impl.permission(request)

        then: "预期与结果"
        2 * userDetail.getAdmin() >> true
        1 * permissionProperties.isEnabled() >> true
        1 * request.getRequestURI() >> "/zuul/iam/v1/users1"
        1 * request.getMethod() >> "get"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload1"]
        1 * permissionMapper.selectWithinPermissionsByServiceName(_) >> list
        value5 == false

        when: "details为空的情况"
        def value8 = impl.permission(request)

        then: "预期与结果"
        2 * userDetail.getAdmin() >> false
        1 * userDetail.getUserId() >> null
        1 * permissionProperties.isEnabled() >> true
        1 * request.getRequestURI() >> "/zuul/iam/v1/users1"
        1 * request.getMethod() >> "get"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload1"]
        1 * permissionMapper.selectPublicOrLoginAccessPermissionsByServiceName(_) >> new ArrayList<>()
        value8 == false

        and: "测public接口"
        permission.setLoginAccess(true)

        when: "调用"
        def value6 = impl.permission(request)
        then: "预期与结果"
        2 * userDetail.getAdmin() >> false
        1 * permissionProperties.isEnabled() >> true
        1 * request.getRequestURI() >> "/zuul/iam/v1/users1"
        1 * request.getMethod() >> "get"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload1"]
        1 * permissionMapper.selectPublicOrLoginAccessPermissionsByServiceName(_) >> list
        value6 == true

        when: "缓存测试"
        impl.setPermissionCacheTime(1800000L)
        def value7 = impl.permission(request)
        then: "预期与结果"
        2 * userDetail.getAdmin() >> false
        1 * permissionProperties.isEnabled() >> true
        1 * request.getRequestURI() >> "/zuul/iam/v1/users1"
        1 * request.getMethod() >> "get"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload1"]
        value7 == true
    }

    def "Permission1"() {
        given: ""
        def permission = new PermissionDO()
        permission.setPath("aaa")
        permission.setMethod("get")
        def list = new ArrayList<PermissionDO>()
        permission.setSourceType(ResourceLevel.SITE.value())
        list << permission
        def request = Mock(HttpServletRequest)
        def helperZuulRoutesProperties = Mock(HelperZuulRoutesProperties)
        def permissionProperties = Mock(PermissionProperties)
        def permissionMapper = Mock(PermissionMapper)
        and: "构造RequestPermissionFilterImpl"
        def impl = new RequestPermissionFilterImpl(helperZuulRoutesProperties, permissionProperties, permissionMapper)
        PowerMockito.mockStatic(DetailsHelper.class)
        def userDetail = Mock(CustomUserDetails)
        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(userDetail)
        def zuulRoute = Mock(ZuulRoute)
        PowerMockito.mockStatic(ZuulPathUtils.class)
        PowerMockito.when(ZuulPathUtils.getRoute(Mockito.anyString(), Mockito.anyMap())).thenReturn(zuulRoute)
        PowerMockito.when(ZuulPathUtils.getRequestTruePath(Mockito.anyString(), Mockito.anyString())).thenReturn("aaa")

        when: "调用"
        def value9 = impl.permission(request)

        then: "预期和结果"
        2 * userDetail.getAdmin() >> false
        2 * userDetail.getUserId() >> 1L
        1 * permissionProperties.isEnabled() >> true
        1 * request.getRequestURI() >> "/zuul/iam/v1/users1"
        1 * request.getMethod() >> "get"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload1"]
        1 * permissionMapper.selectPublicOrLoginAccessPermissionsByServiceName(_) >> new ArrayList<>()
        1 * permissionMapper.selectByUserIdAndServiceName(_, _) >> list
        value9 == true

        and: "鉴权失败"
        permission.setSourceType(ResourceLevel.PROJECT.value())

        when: "调用"
        def value10 = impl.permission(request)

        then: "预期和结果"
        2 * userDetail.getAdmin() >> false
        2 * userDetail.getUserId() >> 1L
        1 * permissionProperties.isEnabled() >> true
        1 * request.getRequestURI() >> "/zuul/iam/v1/users1"
        1 * request.getMethod() >> "get"
        1 * permissionProperties.getSkipPaths() >> ["/iam/v1/upload1"]
        1 * permissionMapper.selectPublicOrLoginAccessPermissionsByServiceName(_) >> new ArrayList<>()
        1 * permissionMapper.selectByUserIdAndServiceName(_, _) >> new ArrayList<>()
        value10 == false

    }

    def "passProjectOrOrgPermission"() {
        given: "准备数据"
        def requestPermissionFilterImpl = new RequestPermissionFilterImpl(null, null, null)

        def permission = Mock(PermissionDO)
        def requestInfo = Whitebox.invokeConstructor(RequestPermissionFilterImpl.RequestInfo.class, "iam/v1/projects/1", "/v1/projects/1", "iam-service", "get")

        when: "调用私有方法"
        def value =
                Whitebox.invokeMethod(requestPermissionFilterImpl, "passProjectOrOrgPermission", permission, requestInfo)
        then: ""
        _ * permission.getPath() >> path
        _ * permission.getSourceType() >> type
        _ * permission.getSourceId() >> 1L
        value == result

        and: ""
        def requestInfo1 = Whitebox.invokeConstructor(RequestPermissionFilterImpl.RequestInfo.class, "iam/v1/users", "/v1/users", "iam-service", "get")
        when: ""
        def value1 =
                Whitebox.invokeMethod(requestPermissionFilterImpl, "passProjectOrOrgPermission", permission, requestInfo1)

        then: ""
        1 * permission.getPath() >> "/v1/users"
        value1 == true



        where: ""
        path                             | type           || result
        "/v1/projects/{project_id}"      | "project"      || true
        "/v1/projects/{organization_id}" | "organization" || true
        "/v1/projects/{project_id}"      | "user"         || false


    }

}

package io.choerodon.gateway.helper.api.service

import io.choerodon.gateway.helper.api.service.impl.PermissionServiceImpl
import io.choerodon.gateway.helper.domain.PermissionDO
import io.choerodon.gateway.helper.infra.exception.PermissionMultiplyMatchException
import io.choerodon.gateway.helper.infra.mapper.PermissionMapper
import io.choerodon.gateway.helper.infra.properties.HelperProperties
import spock.lang.Specification

class PermissionServiceSpec extends Specification {

    def ''() {
        given: '创建HelperProperties和PermissionMapper'
        def helperProperties = new HelperProperties()
        helperProperties.setPermission(new HelperProperties.Permission())
        def permissionMapper = Mock(PermissionMapper) {
            selectPermissionByMethodAndService(_, _) >> Arrays.asList(new PermissionDO('/v1/projects/{id}'), new PermissionDO('/v1/projects/name'))
        }
        def permissionService = new PermissionServiceImpl(permissionMapper, helperProperties)

        when: '权限匹配，不开启multiplyMatch校验'
        def permission1 = permissionService.selectPermissionByRequest("/v1/projects/name:::get:::iam-service")

        then: '验证permission不为空'
        permission1 != null

        when: '权限匹配，开启multiplyMatch校验，匹配多个'
        helperProperties.getPermission().setCheckMultiplyMatch(true)
        permissionService.selectPermissionByRequest("/v1/projects/name:::get:::iam-service")

        then: '验证异常'
        thrown(PermissionMultiplyMatchException)

        when: '权限匹配，开启multiplyMatch校验，匹配一个'
        helperProperties.getPermission().setCheckMultiplyMatch(true)
        def permission2 = permissionService.selectPermissionByRequest("/v1/projects/23:::get:::iam-service")

        then: '验证permission不为空'
        noExceptionThrown()
        permission2 != null


        when: '权限匹配，开启multiplyMatch校验，匹配一个'
        helperProperties.getPermission().setCheckMultiplyMatch(true)
        def permission3 = permissionService.selectPermissionByRequest("/v1/test:::get:::iam-service")

        then: '验证permission为空'
        permission3 == null

    }
}
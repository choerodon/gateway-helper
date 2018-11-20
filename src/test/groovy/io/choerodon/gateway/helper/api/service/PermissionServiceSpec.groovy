package io.choerodon.gateway.helper.api.service

import io.choerodon.gateway.helper.api.service.impl.PermissionServiceImpl
import io.choerodon.gateway.helper.domain.PermissionDO
import io.choerodon.gateway.helper.infra.mapper.PermissionMapper
import spock.lang.Specification

class PermissionServiceSpec extends Specification {

    def ''() {
        given: '创建HelperProperties和PermissionMapper'
        def permissionMapper = Mock(PermissionMapper) {
            selectPermissionByMethodAndService(_, _) >> Arrays.asList(new PermissionDO('/v1/projects/{id}'), new PermissionDO('/v1/projects/name'))
        }
        def permissionService = new PermissionServiceImpl(permissionMapper)

        when: '权限匹配，不开启multiplyMatch校验'
        def permission1 = permissionService.selectPermissionByRequest("/v1/projects/name:::get:::iam-service")

        then: '验证permission不为空'
        permission1 != null
    }
}
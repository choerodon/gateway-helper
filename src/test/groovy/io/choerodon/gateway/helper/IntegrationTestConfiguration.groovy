package io.choerodon.gateway.helper

import io.choerodon.gateway.helper.infra.mapper.PermissionMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct

/**
 * @author superlee
 */

@TestConfiguration
class IntegrationTestConfiguration {

    private final detachedMockFactory = new DetachedMockFactory()

    @PostConstruct
    void init() {
    }

    @Bean
    PermissionMapper permissionMapper() {
        detachedMockFactory.Mock(PermissionMapper)
    }

}
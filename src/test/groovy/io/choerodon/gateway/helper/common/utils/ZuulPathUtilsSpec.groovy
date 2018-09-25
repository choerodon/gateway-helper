package io.choerodon.gateway.helper.common.utils

import io.choerodon.gateway.helper.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/9/25.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ZuulPathUtilsSpec extends Specification {

    def "GetRequestTruePath"() {
        when: ""
        def value = ZuulPathUtils.getRequestTruePath("/iam/v1/users", "/iam/**")
        then: ""
        value == "/v1/users"
    }
}

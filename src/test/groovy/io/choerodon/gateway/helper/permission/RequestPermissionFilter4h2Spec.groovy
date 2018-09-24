package io.choerodon.gateway.helper.permission

import io.choerodon.gateway.helper.IntegrationTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/9/21.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RequestPermissionFilter4h2Spec extends Specification {

    @Autowired
    TestRestTemplate testRestTemplate

    def "get请求测流程"() {
        when:""
        def entity = testRestTemplate.getForEntity("/v1/swaggers/resources", String)

        then:""
        entity.statusCode.is4xxClientError()
    }
}

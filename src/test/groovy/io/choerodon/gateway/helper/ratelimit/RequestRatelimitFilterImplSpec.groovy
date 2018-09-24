package io.choerodon.gateway.helper.ratelimit

import io.choerodon.gateway.helper.IntegrationTestConfiguration
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/9/24.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RequestRatelimitFilterImplSpec extends Specification {
    def "Through"() {
        given: ""
        def requestRatelimitFilterImpl = new RequestRatelimitFilterImpl()
        def request = Mock(HttpServletRequest)
        when: ""
        def value = requestRatelimitFilterImpl.through(request)
        then: ""
        value == true
    }
}

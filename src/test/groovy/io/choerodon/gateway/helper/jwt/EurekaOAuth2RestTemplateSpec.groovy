package io.choerodon.gateway.helper.jwt

import io.choerodon.core.convertor.ApplicationContextHelper
import io.choerodon.gateway.helper.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/9/25.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class EurekaOAuth2RestTemplateSpec extends Specification {
    def "GetRequestFactory"() {
        when: ""
        def eurekaOAuth2RestTemplate =
                ApplicationContextHelper.getSpringFactory().getBean("oauth_rest", RestTemplate.class)
        def value = eurekaOAuth2RestTemplate.getRequestFactory()

        then: ""
        value != null
        value instanceof ClientHttpRequestFactory
    }
}

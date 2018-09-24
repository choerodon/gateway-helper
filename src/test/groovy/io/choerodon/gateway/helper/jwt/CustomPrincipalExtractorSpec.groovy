package io.choerodon.gateway.helper.jwt

import io.choerodon.core.oauth.CustomClientDetails
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.gateway.helper.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/9/24.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class CustomPrincipalExtractorSpec extends Specification {

    def map = new HashMap()

    def "ExtractPrincipal"() {

        given: "return client"
        map.put("principal", "client")
        def customPrincipalExtractor = new CustomPrincipalExtractor()

        when: "调用"
        def obj = customPrincipalExtractor.extractPrincipal(map)
        map.clear()

        then: "预期和结果"
        obj instanceof CustomClientDetails
    }

    def "returnUser"() {
        given: ""
        map.put("userId", 1)
        map.put("username", "kangkang")
        map.put("language", "zh_CN")
        map.put("admin", true)
        map.put("timeZone", "CTT")
        map.put("organizationId", 1)
        def customPrincipalExtractor = new CustomPrincipalExtractor()

        when: ""
        def obj = customPrincipalExtractor.extractPrincipal(map)
        map.clear()

        then: ""
        obj instanceof CustomUserDetails
    }

    def "return client"() {
        given: ""
        map.put("username", "kangkang")
        map.put("organizationId", "1")
        def customPrincipalExtractor = new CustomPrincipalExtractor()

        when: ""
        def obj = customPrincipalExtractor.extractPrincipal(map)

        then: ""
        obj instanceof CustomClientDetails
        def client = (CustomClientDetails) obj
        client.getClientId() == "kangkang"
        client.getOrganizationId() == 1L
    }
}

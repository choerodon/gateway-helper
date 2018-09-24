package io.choerodon.gateway.helper.jwt

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
class ClearContextZuulFilterSpec extends Specification {

    def clearContextZuulFilter = new ClearContextZuulFilter()

    def "FilterType"() {
        when: ""
        def value = clearContextZuulFilter.filterType()
        then: ""
        value == "post"
    }

    def "FilterOrder"() {
        when: ""
        def value = clearContextZuulFilter.filterOrder()
        then: ""
        value == 0
    }

    def "ShouldFilter"() {
        when:""
        def value = clearContextZuulFilter.shouldFilter()

        then:""
        value==true
    }

    def "Run"() {
    }
}

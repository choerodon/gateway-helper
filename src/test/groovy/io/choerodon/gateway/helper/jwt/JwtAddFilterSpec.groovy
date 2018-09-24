package io.choerodon.gateway.helper.jwt

import com.netflix.zuul.context.RequestContext
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.core.oauth.DetailsHelper
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.security.jwt.Jwt
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.Signer
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse


/**
 * Created by superlee on 2018/9/24.
 */
@PrepareForTest([RequestContext.class, DetailsHelper.class, JwtHelper.class])
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class JwtAddFilterSpec extends Specification {

    def jwtSigner = Mock(Signer)
    def jwtAddFilter = new JwtAddFilter(jwtSigner)

    def "ShouldFilter"() {
        when: ""
        def value = jwtAddFilter.shouldFilter()
        then: ""
        value == true
    }

    def "Run"() {
        given: ""
        PowerMockito.mockStatic(RequestContext.class)
        def ctx = Mock(RequestContext)
        PowerMockito.when(RequestContext.getCurrentContext()).thenReturn(ctx)

        PowerMockito.mockStatic(DetailsHelper.class)
        def details = Mock(CustomUserDetails)
        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(details)

        PowerMockito.mockStatic(JwtHelper.class)
        def jwt = Mock(Jwt)
        PowerMockito.when(JwtHelper.encode(Mockito.anyString(), Mockito.any())).thenReturn(jwt)

        def response = Mock(HttpServletResponse)

        when: ""
        def obj = jwtAddFilter.run()
        then: ""
        1 * jwt.getEncoded() >> "jwt"
        2 * ctx.getResponse() >> response
        obj == null
    }

    def "FilterOrder"() {
    }

    def "FilterType"() {
    }
}

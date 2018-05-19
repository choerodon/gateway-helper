package io.choerodon.gateway.helper.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author xausky
 */
@Component
public class EurekaOAuth2RestTemplate extends OAuth2RestTemplate {

    @Autowired
    @Qualifier("oauth_rest")
    private RestTemplate restTemplate;

    private static AuthorizationCodeResourceDetails defaultResourceDetails;

    static {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId("<N/A>");
        details.setUserAuthorizationUri("Not a URI because there is no client");
        details.setAccessTokenUri("Not a URI because there is no client");
        defaultResourceDetails = details;
    }

    public EurekaOAuth2RestTemplate(OAuth2ProtectedResourceDetails resource) {
        super(defaultResourceDetails);
    }

    @Override
    public ClientHttpRequestFactory getRequestFactory() {
        return restTemplate.getRequestFactory();
    }
}

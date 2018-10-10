package io.choerodon.gateway.helper.api.filter;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.gateway.helper.domain.CheckState;
import io.choerodon.gateway.helper.domain.RequestContext;
import io.choerodon.gateway.helper.infra.properties.HelperProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * 根据access_token获取对应的userDetails
 */
@Component
public class GetUserDetailsFilter implements HelperFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserDetailsFilter.class);

    private static final String PRINCIPAL = "principal";

    private static final String ADDITION_INFO = "additionInfo";

    private HelperProperties helperProperties;

    private RestTemplate restTemplate;

    public GetUserDetailsFilter(HelperProperties helperProperties, RestTemplate restTemplate) {
        this.helperProperties = helperProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public int filterOrder() {
        return 40;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean run(RequestContext context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, context.request.accessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(helperProperties.getOauthInfoUri(), HttpMethod.GET, entity, Map.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                CustomUserDetails customUserDetails = extractPrincipal(responseEntity.getBody());
                context.setCustomUserDetails(customUserDetails);
                return true;
            } else {
                context.response.setStatus(CheckState.EXCEPTION_OAUTH_SERVER);
                context.response.setMessage("oauth server exception, can't get userDetails, oauth statusCode: "
                        + responseEntity.getStatusCodeValue());
                return false;
            }
        } catch (Exception e) {
            context.response.setStatus(CheckState.EXCEPTION_OAUTH_SERVER);
            context.response.setMessage("oauth server exception, can't get userDetails, exception: " + e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private CustomUserDetails extractPrincipal(Map<String, Object> map) {
        if (map.get(PRINCIPAL) != null) {
            map = (Map) map.get(PRINCIPAL);
        }
        if (map.containsKey("userId")) {
            CustomUserDetails user = new CustomUserDetails((String) map.get("username"),
                    "unknown password", Collections.emptyList());
            user.setUserId((long) (Integer) map.get("userId"));
            user.setLanguage((String) map.get("language"));
            user.setAdmin((Boolean) map.get("admin"));
            user.setTimeZone((String) map.get("timeZone"));
            user.setOrganizationId((long) (Integer) map.get("organizationId"));
            if (map.get("email") != null) {
                user.setEmail((String) map.get("email"));
            }
            try {
                if (map.get(ADDITION_INFO) != null) {
                    user.setAdditionInfo((Map) map.get(ADDITION_INFO));
                }
            } catch (Exception e) {
                LOGGER.warn("parser addition info error:{}", e);
            }
            return user;
        }
        return null;
    }

}

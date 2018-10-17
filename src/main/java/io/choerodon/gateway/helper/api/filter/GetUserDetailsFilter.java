package io.choerodon.gateway.helper.api.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

    private static final String OAUTH_TOKEN_ERROR_CODE = "invalid_token";

    private static final String OAUTH_TOKEN_INVALID = "Invalid access token";

    private static final String OAUTH_TOKEN_EXPIRED = "Access token expired";

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        String accessToken = context.request.accessToken;
        if (StringUtils.isEmpty(accessToken)) {
            context.response.setStatus(CheckState.PERMISSION_ACCESS_TOKEN_NULL);
            context.response.setMessage("Access_token is empty, Please login and set access_token by HTTP header 'Authorization'");
            return false;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, context.request.accessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(helperProperties.getOauthInfoUri(), HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                CustomUserDetails customUserDetails = extractPrincipal(objectMapper.readValue(responseEntity.getBody(), Map.class));
                context.setCustomUserDetails(customUserDetails);
                return true;
            } else {
                if (!setResponseWhenGetJwtFailed(responseEntity, context)) {
                    context.response.setStatus(CheckState.PERMISSION_GET_USE_DETAIL_FAILED);
                    context.response.setMessage("Get userDetail from oauth-server failed, Please re-login and retry。 oauth-server message: " + responseEntity.getBody());
                }
            }
        } catch (RestClientException e) {
            context.response.setStatus(CheckState.EXCEPTION_OAUTH_SERVER);
            context.response.setMessage("Oauth server exception, can't get userDetails, exception: " + e);
        } catch (IOException e) {
            context.response.setStatus(CheckState.EXCEPTION_GATEWAY_HELPER);
            context.response.setMessage("gateway helper error happened: " + e.toString());
        }
        return false;
    }

    private boolean setResponseWhenGetJwtFailed(final ResponseEntity<String> responseEntity, final RequestContext context) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        JsonNode errorNode = jsonNode.get("error");
        JsonNode errorDescriptionNode = jsonNode.get("error_description");
        if (errorNode != null && errorDescriptionNode != null && OAUTH_TOKEN_ERROR_CODE.equals(errorNode.textValue())) {
            if (errorDescriptionNode.textValue().contains(OAUTH_TOKEN_INVALID)) {
                context.response.setStatus(CheckState.PERMISSION_ACCESS_TOKEN_INVALID);
                context.response.setMessage("Access_token is invalid, Please re-login and set correct access_token by HTTP header 'Authorization'");
                return true;
            } else if (errorDescriptionNode.textValue().contains(OAUTH_TOKEN_EXPIRED)) {
                context.response.setStatus(CheckState.PERMISSION_ACCESS_TOKEN_EXPIRED);
                context.response.setMessage("Access_token is expired, Please re-login and set correct access_token by HTTP header 'Authorization'");
                return true;
            }
        }
        return false;
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
                LOGGER.warn("Parser addition info error:{}", e);
            }
            return user;
        }
        return null;
    }

}

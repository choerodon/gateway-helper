package io.choerodon.gateway.helper.api.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.gateway.helper.api.service.GetUserDetailsService;
import io.choerodon.gateway.helper.domain.CheckState;
import io.choerodon.gateway.helper.domain.CustomUserDetailsWithResult;
import io.choerodon.gateway.helper.infra.properties.HelperProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
public class GetUserDetailsServiceImpl implements GetUserDetailsService {


    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserDetailsService.class);

    private static final String PRINCIPAL = "principal";

    private static final String ADDITION_INFO = "additionInfo";

    private static final String OAUTH_TOKEN_ERROR_CODE = "invalid_token";

    private static final String OAUTH_TOKEN_INVALID = "Invalid access token";

    private static final String OAUTH_TOKEN_EXPIRED = "Access token expired";


    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestTemplate restTemplate;

    private HelperProperties helperProperties;

    public GetUserDetailsServiceImpl(RestTemplate restTemplate, HelperProperties helperProperties) {
        this.restTemplate = restTemplate;
        this.helperProperties = helperProperties;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(value = "user", key = "'choerodon:userdetails:'+#token", unless = "#result.customUserDetails == null")
    public CustomUserDetailsWithResult getUserDetails(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(helperProperties.getOauthInfoUri(), HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                CustomUserDetails userDetails = extractPrincipal(objectMapper.readValue(responseEntity.getBody(), Map.class));
                return new CustomUserDetailsWithResult(userDetails, CheckState.SUCCESS_PASS_SITE);
            } else {
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                JsonNode errorNode = jsonNode.get("error");
                JsonNode errorDescriptionNode = jsonNode.get("error_description");
                if (errorNode != null && errorDescriptionNode != null && OAUTH_TOKEN_ERROR_CODE.equals(errorNode.textValue())) {
                    if (errorDescriptionNode.textValue().contains(OAUTH_TOKEN_INVALID)) {
                        return new CustomUserDetailsWithResult(CheckState.PERMISSION_ACCESS_TOKEN_INVALID,
                                "Access_token is invalid, Please re-login and set correct access_token by HTTP header 'Authorization'");
                    } else if (errorDescriptionNode.textValue().contains(OAUTH_TOKEN_EXPIRED)) {
                        return new CustomUserDetailsWithResult(CheckState.PERMISSION_ACCESS_TOKEN_EXPIRED,
                                "Access_token is expired, Please re-login and set correct access_token by HTTP header 'Authorization'");
                    }
                }
                return new CustomUserDetailsWithResult(CheckState.PERMISSION_GET_USE_DETAIL_FAILED,
                        "Get userDetail from oauth-server failed, Please re-login and retry。 " +
                                "oauth-server message: " + responseEntity.getBody());
            }
        } catch (RestClientException e) {
            return new CustomUserDetailsWithResult(CheckState.EXCEPTION_OAUTH_SERVER,
                    "Oauth server exception, can't get userDetails, exception: " + e);
        } catch (IOException e) {
            return new CustomUserDetailsWithResult(CheckState.EXCEPTION_GATEWAY_HELPER,
                    "Gateway helper error happened: " + e.toString());
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
                LOGGER.warn("Parser addition info error:{}", e);
            }
            return user;
        }
        return null;
    }
}
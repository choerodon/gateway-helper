package io.choerodon.gateway.helper.jwt;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.stereotype.Component;

import io.choerodon.core.oauth.CustomClientDetails;
import io.choerodon.core.oauth.CustomUserDetails;

/**
 * Created by xausky on 5/3/17.
 */
@Component
public class CustomPrincipalExtractor implements PrincipalExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomPrincipalExtractor.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String PRINCIPAL = "principal";

    private static final String ADDITION_INFO = "additionInfo";

    @Override
    @SuppressWarnings("unchecked")
    public Object extractPrincipal(Map<String, Object> map) {
        if (map.get(PRINCIPAL) != null) {
            if (map.get(PRINCIPAL).equals("client")) {
                CustomClientDetails client = new CustomClientDetails();
                client.setClientId((String) map.get("name"));
                client.setAuthorities(Collections.emptyList());
                client.setOrganizationId(1L);
                return client;
            }
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

            try {
                if (map.get(ADDITION_INFO) != null) {
                    user.setAdditionInfo((Map) map.get(ADDITION_INFO));
                }
            } catch (Exception e) {
                LOGGER.warn("parser addition info error:{}", e);
            }
            return user;
        } else {
            CustomClientDetails client = new CustomClientDetails();
            client.setClientId((String) map.get("username"));
            client.setAuthorities(Collections.emptyList());
            client.setOrganizationId(Long.parseLong((String) map.get("organizationId")));

            try {
                if (map.get(ADDITION_INFO) != null) {
                    client.setAdditionalInformation(MAPPER.readValue((String) map.get(ADDITION_INFO), Map.class));
                }
            } catch (Exception e) {
                LOGGER.warn("parser addition info error:{}", e);
            }
            return client;
        }
    }

}

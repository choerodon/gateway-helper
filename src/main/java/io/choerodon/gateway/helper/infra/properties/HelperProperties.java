package io.choerodon.gateway.helper.infra.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "choerodon.helper")
public class HelperProperties {

    private Permission permission = new Permission();

    private String jwtKey = "choerodon";

    private String oauthInfoUri = "http://oauth-server/oauth/api/user";

    public String getOauthInfoUri() {
        return oauthInfoUri;
    }

    public void setOauthInfoUri(String oauthInfoUri) {
        this.oauthInfoUri = oauthInfoUri;
    }

    public String getJwtKey() {
        return jwtKey;
    }

    public void setJwtKey(String jwtKey) {
        this.jwtKey = jwtKey;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public static class Permission {
        private Boolean enabled = true;

        private List<String> skipPaths = Arrays.asList("/**/skip/**", "/oauth/**");

        private Long cacheSeconds = 600L;

        private Long cacheSize = 5000L;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getSkipPaths() {
            return skipPaths;
        }

        public void setSkipPaths(List<String> skipPaths) {
            this.skipPaths = skipPaths;
        }

        public Long getCacheSeconds() {
            return cacheSeconds;
        }

        public void setCacheSeconds(Long cacheSeconds) {
            this.cacheSeconds = cacheSeconds;
        }

        public Long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(Long cacheSize) {
            this.cacheSize = cacheSize;
        }

    }
}

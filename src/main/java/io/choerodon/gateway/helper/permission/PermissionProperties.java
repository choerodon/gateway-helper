package io.choerodon.gateway.helper.permission;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 权限配置信息类
 *
 * @author flyleft
 * @date 2018/3/14
 */
@ConfigurationProperties("choerodon.permission")
public class PermissionProperties {

    private boolean enabled = true;

    private String[] skipPaths = new String[]{};


    @NestedConfigurationProperty
    private Check check = new Check();

    @NestedConfigurationProperty
    private Jwt jwt = new Jwt();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Check getCheck() {
        return check;
    }

    public void setCheck(Check check) {
        this.check = check;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public String[] getSkipPaths() {
        return skipPaths;
    }

    public void setSkipPaths(String[] skipPaths) {
        this.skipPaths = skipPaths;
    }

    public static class Check {

        private boolean service = false;
        private boolean url = false;

        public boolean isService() {
            return service;
        }

        public void setService(boolean service) {
            this.service = service;
        }

        public boolean isUrl() {
            return url;
        }

        public void setUrl(boolean url) {
            this.url = url;
        }
    }

    public static class Jwt {

        private String key = "choerodon";

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }


}

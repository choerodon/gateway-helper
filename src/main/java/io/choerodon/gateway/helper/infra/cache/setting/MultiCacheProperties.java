package io.choerodon.gateway.helper.infra.cache.setting;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.cache.multi")
public class MultiCacheProperties {

    private L1 l1;

    private L2 l2;

    private Map<String, Cache> caches = new LinkedHashMap<>();

    public L1 getL1() {
        return l1;
    }

    public void setL1(L1 l1) {
        this.l1 = l1;
    }

    public L2 getL2() {
        return l2;
    }

    public void setL2(L2 l2) {
        this.l2 = l2;
    }

    public Map<String, Cache> getCaches() {
        return caches;
    }

    public void setCaches(Map<String, Cache> caches) {
        this.caches = caches;
    }


    public static class L1 {
        private String type = "caffeine";
        private boolean enabled = true;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class L2 {
        private String type = "redis";
        private boolean enabled = true;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }


    public static class Cache {
        private boolean l1Enabled = true;
        private String l1Spec = "";
        private boolean l1AllowNullValues = true;
        private boolean l2Enabled = true;
        private String l2Spec = "";
        private boolean l2AllowNullValues = false;

        public boolean isL1Enabled() {
            return l1Enabled;
        }

        public void setL1Enabled(boolean l1Enabled) {
            this.l1Enabled = l1Enabled;
        }

        public String getL1Spec() {
            return l1Spec;
        }

        public void setL1Spec(String l1Spec) {
            this.l1Spec = l1Spec;
        }

        public boolean isL2Enabled() {
            return l2Enabled;
        }

        public void setL2Enabled(boolean l2Enabled) {
            this.l2Enabled = l2Enabled;
        }

        public boolean isL1AllowNullValues() {
            return l1AllowNullValues;
        }

        public void setL1AllowNullValues(boolean l1AllowNullValues) {
            this.l1AllowNullValues = l1AllowNullValues;
        }

        public String getL2Spec() {
            return l2Spec;
        }

        public void setL2Spec(String l2Spec) {
            this.l2Spec = l2Spec;
        }

        public boolean isL2AllowNullValues() {
            return l2AllowNullValues;
        }

        public void setL2AllowNullValues(boolean l2AllowNullValues) {
            this.l2AllowNullValues = l2AllowNullValues;
        }
    }
}
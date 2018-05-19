package io.choerodon.gateway.helper.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

/**
 * zuul路由信息配置类
 * @author zhipeng.zuo
 * @date 2018/2/28
 */
@ConfigurationProperties("zuul")
public class ZuulRoutesProperties {

    private Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();

    public Map<String, ZuulProperties.ZuulRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, ZuulProperties.ZuulRoute> routes) {
        this.routes = routes;
    }
}

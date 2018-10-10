package io.choerodon.gateway.helper.api.filter;

import io.choerodon.gateway.helper.domain.RequestContext;
import io.choerodon.gateway.helper.domain.CheckState;
import org.springframework.cloud.config.client.ZuulRoute;
import org.springframework.cloud.config.helper.HelperZuulRoutesProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Map;

/**
 * 根据请求前缀获取对应的zuul路由
 * 如果为/zuul/开头的文件上传请求，则去除之后再获取
 */
@Component
public class GetRequestRouteFilter implements HelperFilter {

    private static final String ZUUL_SERVLET_PATH = "zuul/";

    private final AntPathMatcher matcher = new AntPathMatcher();

    private HelperZuulRoutesProperties helperZuulRoutesProperties;

    public GetRequestRouteFilter(HelperZuulRoutesProperties helperZuulRoutesProperties) {
        this.helperZuulRoutesProperties = helperZuulRoutesProperties;
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        //如果是文件上传的url，以/zuul/开否，则去除了/zuul再进行校验权限
        String requestUri = context.request.uri;
        if (requestUri.startsWith("/" + ZUUL_SERVLET_PATH)) {
            requestUri = requestUri.substring(5, requestUri.length());

        }
        //根据请求uri获取zuulRoute
        ZuulRoute route = getRoute(requestUri, helperZuulRoutesProperties.getRoutes());
        if (route == null) {
            context.response.setStatus(CheckState.PERMISSION_SERVICE_ROUTE);
            context.response.setMessage("This request mismatch any routes, uri: "
                    + requestUri + " , all routes: " + helperZuulRoutesProperties.getRoutes().values());
            return false;
        } else {
            context.setTrueUri(getRequestTruePath(requestUri, route.getPath()));
            context.setRoute(route);
            context.setRequestKey(generateKey(requestUri, context.request.method, route.getServiceId()));
            return true;
        }
    }

    private  String generateKey(String uri, String method, String service) {
        return uri + "::" + method + "::" + service;
    }


    private String getRequestTruePath(String uri, String routePath) {
        return "/" + matcher.extractPathWithinPattern(routePath, uri);
    }

    private ZuulRoute getRoute(final String requestUri,
                               final Map<String, ZuulRoute> routeMap) {
        for (ZuulRoute zuulRoute : routeMap.values()) {
            if (matcher.match(zuulRoute.getPath(), requestUri)) {
                return zuulRoute;
            }
        }
        return null;
    }

}

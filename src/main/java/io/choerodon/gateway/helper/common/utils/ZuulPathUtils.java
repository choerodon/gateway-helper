package io.choerodon.gateway.helper.common.utils;

import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.config.client.ZuulRoute;
import org.springframework.util.AntPathMatcher;

/**
 * zuul有关的工具包
 *
 * @author fyleft
 */
public class ZuulPathUtils {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private ZuulPathUtils() {
    }

//    /**
//     * 获取服务前缀，形如uaa
//     *
//     * @param uri 形如/uaa/v1/testPermission
//     * @return 服务前缀，形如uaa
//     */
//    public static Optional<String> getServicePrefixByUri(String uri) {
//        String[] uris = uri.split("/");
//        if (uri.length() > 1) {
//            return Optional.of(uris[1]);
//        }
//        return Optional.empty();
//    }

    public static ZuulRoute getRoute(final String requestUri,
                                     final Map<String, ZuulRoute> routeMap) {
        for (ZuulRoute zuulRoute : routeMap.values()) {
            if (MATCHER.match(zuulRoute.getPath(), requestUri)) {
                return zuulRoute;
            }
        }
        return null;
    }


    /**
     * 获取请求真实访问uri
     *
     * @param uri       形如/uaa/v1/testPermission
     * @param routePath 形如／uaa／**
     * @return 形如/v1/testPermission
     */
    public static String getRequestTruePath(String uri, String routePath) {
        return "/" + MATCHER.extractPathWithinPattern(routePath, uri);
    }
}
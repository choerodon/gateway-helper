package io.choerodon.gateway.helper.permission;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.gateway.helper.common.utils.ZuulPathUtils;
import io.choerodon.gateway.helper.permission.domain.PermissionDO;
import io.choerodon.gateway.helper.permission.mapper.PermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.client.ZuulRoute;
import org.springframework.cloud.config.helper.HelperZuulRoutesProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author flyleft
 * @date 2018/5/3
 */
@Service
public class RequestPermissionFilterImpl implements RequestPermissionFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPermissionFilter.class);

    private static final String ZUUL_SERVLET_PATH = "/zuul/";

    @Value("${choerodon.permission.cacheTime:1800000}")
    private Long permissionCacheTime;

    private HelperZuulRoutesProperties helperZuulRoutesProperties;

    private PermissionProperties permissionProperties;

    private PermissionMapper permissionMapper;

    private static final Pattern NUM_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    private static final String PROJECT_PATH_ID = "project_id";

    private static final String ORG_PATH_ID = "organization_id";

    public RequestPermissionFilterImpl(HelperZuulRoutesProperties helperZuulRoutesProperties,
                                       PermissionProperties permissionProperties,
                                       PermissionMapper permissionMapper) {
        this.helperZuulRoutesProperties = helperZuulRoutesProperties;
        this.permissionProperties = permissionProperties;
        this.permissionMapper = permissionMapper;
    }

    private final Map<String, Long> publicPermissionMap = new HashMap<>();

    private final Map<String, Long> loginPermissionMap = new HashMap<>();

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean permission(final HttpServletRequest request) {
        if (!permissionProperties.isEnabled()) {
            return true;
        }
        //如果是文件上传的url，以/zuul/开否，则去除了/zuul再进行校验权限
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith(ZUUL_SERVLET_PATH)) {
            requestURI = requestURI.substring(5, requestURI.length());
        }
        //skipPath直接返回true
        for (String skipPath : permissionProperties.getSkipPaths()) {
            if (matcher.match(skipPath, requestURI)) {
                return true;
            }
        }
        //如果获取不到该服务的路由信息，则不允许通过
        ZuulRoute route = ZuulPathUtils.getRoute(requestURI, helperZuulRoutesProperties.getRoutes());
        if (route == null) {
            LOGGER.info("error.permissionVerifier.permission, can't find request service route, "
                    + "request uri {}, zuulRoutes {}", request.getRequestURI(), helperZuulRoutesProperties.getRoutes());
            return false;
        }
        String requestTruePath = ZuulPathUtils.getRequestTruePath(requestURI, route.getPath());
        final RequestInfo requestInfo = new RequestInfo(requestURI, requestTruePath,
                route.getServiceId(), request.getMethod());
        final CustomUserDetails details = DetailsHelper.getUserDetails();
        //如果是超级管理员用户，则跳过权限校验
        if (details != null && details.getAdmin() != null && details.getAdmin()) {
            return true;
        }
        //判断是不是public接口获取loginAccess接口
        if (passPublicOrLoginAccessPermission(requestInfo, details)) {
            return true;
        }
        if (details == null || details.getUserId() == null) {
            LOGGER.info("error.permissionVerifier.permission, can't find userDetail {}", requestInfo);
            return false;
        }
        //其他接口权限权限审查
        if (passSourcePermission(requestInfo, details.getUserId())) {
            return true;
        }
        LOGGER.info("error.permissionVerifier.permission when passSourcePermission {}", requestInfo);
        return false;
    }

    private boolean passSourcePermission(final RequestInfo requestInfo, final long userId) {
        final List<PermissionDO> resourcePermissions = permissionMapper.selectByUserIdAndServiceName(userId, requestInfo.service);
        for (PermissionDO permissionDO : resourcePermissions) {
            boolean match = matcher.match(permissionDO.getPath(), requestInfo.trueUri)
                    && requestInfo.method.equalsIgnoreCase(permissionDO.getMethod());
            if (match) {
                return permissionDO.getSourceType().equals(ResourceLevel.SITE.value())
                        || passProjectOrOrgPermission(permissionDO, requestInfo);
            }
        }
        return false;
    }

    private boolean passProjectOrOrgPermission(final PermissionDO permissionDO, final RequestInfo requestInfo) {
        Map<String, String> map = matcher.extractUriTemplateVariables(permissionDO.getPath(), requestInfo.trueUri);
        if (map.size() < 1) {
            return true;
        }
        if (permissionDO.getSourceType().equals(ResourceLevel.PROJECT.value()) && map.containsKey(PROJECT_PATH_ID)) {
            String projectId = map.get(PROJECT_PATH_ID);
            return isInteger(projectId) && Long.parseLong(projectId) == permissionDO.getSourceId();
        } else if (permissionDO.getSourceType().equals(ResourceLevel.ORGANIZATION.value()) && map.containsKey()) {
            String organizationId = map.get(ORG_PATH_ID);
            return isInteger(organizationId) && Long.parseLong(organizationId) == permissionDO.getSourceId();
        }
        return false;
    }

    private static boolean isInteger(String str) {
        return !StringUtils.isEmpty(str) && NUM_PATTERN.matcher(str).matches();
    }

    private boolean passPublicOrLoginAccessPermission(final RequestInfo requestInfo,
                                                      final CustomUserDetails details) {
        Long permissionTime = publicPermissionMap.get(requestInfo.key);
        if (permissionTime != null) {
            if (System.currentTimeMillis() - permissionTime < permissionCacheTime) {
                return true;
            } else {
                publicPermissionMap.remove(requestInfo.key);
            }
        }
        permissionTime = loginPermissionMap.get(requestInfo.key);
        if (permissionTime != null && details != null) {
            if (System.currentTimeMillis() - permissionTime < permissionCacheTime) {
                return true;
            } else {
                loginPermissionMap.remove(requestInfo.key);
            }
        }
        final List<PermissionDO> publicOrLoginPermissions = permissionMapper.selectPublicOrLoginAccessPermissionsByServiceName(requestInfo.service);
        for (PermissionDO permissionDO : publicOrLoginPermissions) {
            boolean match = matcher.match(permissionDO.getPath(), requestInfo.trueUri)
                    && requestInfo.method.equalsIgnoreCase(permissionDO.getMethod());
            if (match && permissionDO.getLoginAccess() && details != null) {
                loginPermissionMap.put(requestInfo.key, System.currentTimeMillis());
                return true;
            }
            if (match && permissionDO.getPublicAccess()) {
                publicPermissionMap.put(requestInfo.key, System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }


    private static class RequestInfo {
        final String uri;
        final String trueUri;
        final String service;
        final String method;
        final String key;

        private RequestInfo(String uri, String trueUri, String service, String method) {
            this.uri = uri;
            this.trueUri = trueUri;
            this.service = service;
            this.method = method;
            this.key = uri + method;
        }

        @Override
        public String toString() {
            return "RequestInfo{"
                    + "uri='" + uri + '\''
                    + ", trueUri='" + trueUri + '\''
                    + ", service='" + service + '\''
                    + ", method='" + method + '\''
                    + ", key='" + key + '\''
                    + '}';
        }
    }
}

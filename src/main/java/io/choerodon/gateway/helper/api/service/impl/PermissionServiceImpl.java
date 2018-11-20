package io.choerodon.gateway.helper.api.service.impl;

import io.choerodon.gateway.helper.api.service.PermissionService;
import io.choerodon.gateway.helper.domain.PermissionDO;
import io.choerodon.gateway.helper.infra.mapper.PermissionMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.stream.Collectors;

import static io.choerodon.gateway.helper.api.filter.GetRequestRouteFilter.REQUEST_KEY_SEPARATOR;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private static final String SLASH_END = "/";

    private PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    @Cacheable(value = "permission", key = "'choerodon:permission:'+#requestKey", unless = "#result == null")
    public PermissionDO selectPermissionByRequest(String requestKey) {
        String[] request = requestKey.split(REQUEST_KEY_SEPARATOR);
        String uri = request[0];
        String method = request[1];
        List<PermissionDO> permissionDOS = permissionMapper.selectPermissionByMethodAndService(method, request[2]);
        List<PermissionDO> matchPermissions = permissionDOS.stream().filter(t -> matcher.match(t.getPath(), uri)).collect(Collectors.toList());
        if (matchPermissions.size() > 1) {
            for (PermissionDO permissionDO : matchPermissions) {
                if (equalsPath(uri, permissionDO.getPath())) {
                    return permissionDO;
                }
            }
        } else if (matchPermissions.size() == 1) {
            return matchPermissions.get(0);
        }
        return null;
    }

    private boolean equalsPath(String path1, String path2) {
        if (path1.endsWith(SLASH_END)) {
            path1 = path1.substring(0, path1.length() - 1);
        }
        if (path2.endsWith(SLASH_END)) {
            path2 = path2.substring(0, path2.length() - 1);
        }
        return path1.equals(path2);
    }

}

package io.choerodon.gateway.helper.api.service.impl;

import io.choerodon.gateway.helper.api.service.PermissionService;
import io.choerodon.gateway.helper.domain.PermissionDO;
import io.choerodon.gateway.helper.infra.exception.PermissionMultiplyMatchException;
import io.choerodon.gateway.helper.infra.mapper.PermissionMapper;
import io.choerodon.gateway.helper.infra.properties.HelperProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.stream.Collectors;

import static io.choerodon.gateway.helper.api.filter.GetRequestRouteFilter.REQUEST_KEY_SEPARATOR;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionMapper permissionMapper;

    private HelperProperties helperProperties;

    public PermissionServiceImpl(PermissionMapper permissionMapper,
                                 HelperProperties helperProperties) {
        this.permissionMapper = permissionMapper;
        this.helperProperties = helperProperties;
    }

    @Override
    @Cacheable(value="permission", key="#requestKey")
    public PermissionDO selectPermissionByRequest(String requestKey) {
        String[] request = requestKey.split(REQUEST_KEY_SEPARATOR);
        String uri = request[0];
        String method =  request[1];
        List<PermissionDO> permissionDOS = permissionMapper.selectPermissionByMethodAndService(method, request[2]);
        if (helperProperties.getPermission().getCheckMultiplyMatch()) {
            List<PermissionDO> matchPermissions = permissionDOS.stream().filter(t -> matcher.match(t.getPath(), uri)).collect(Collectors.toList());
            if (matchPermissions.size() > 1) {
                throw new PermissionMultiplyMatchException(uri, method, matchPermissions);
            } else if (matchPermissions.size() == 1) {
                return matchPermissions.get(0);
            }
        } else {
            for (PermissionDO permissionDO : permissionDOS) {
                if (matcher.match(permissionDO.getPath(), uri)) {
                    return permissionDO;
                }
            }
        }
        return null;
    }

}

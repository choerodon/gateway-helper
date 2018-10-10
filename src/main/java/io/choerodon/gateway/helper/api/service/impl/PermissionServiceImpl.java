package io.choerodon.gateway.helper.api.service.impl;

import io.choerodon.gateway.helper.api.service.PermissionService;
import io.choerodon.gateway.helper.domain.PermissionDO;
import io.choerodon.gateway.helper.infra.exception.PermissionMutiplyMatchException;
import io.choerodon.gateway.helper.infra.mapper.PermissionMapper;
import io.choerodon.gateway.helper.infra.properties.HelperProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.stream.Collectors;

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
    public PermissionDO selectPermissionByRequest(String uri, String method, String service) {
        List<PermissionDO> permissionDOS = permissionMapper.selectPermissionByMethodAndService(method, service);
        if (helperProperties.getPermission().getCheckMultiplyMatch()) {
            List<PermissionDO> matchPermissions = permissionDOS.stream().filter(t -> matcher.match(t.getPath(), uri)).collect(Collectors.toList());
            if (matchPermissions.size() > 1) {
                throw new PermissionMutiplyMatchException(uri, method, matchPermissions);
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

package io.choerodon.gateway.helper.infra.exception;

import io.choerodon.gateway.helper.domain.PermissionDO;

import java.util.List;

public class PermissionMutiplyMatchException extends RuntimeException {

    private final transient List<PermissionDO> permissionDOS;

    public PermissionMutiplyMatchException(String uri, String method, List<PermissionDO> permissionDOS) {
        super("Request uri: " + uri + " method: " + method + " match multiply permissions, permissionDOS: " + permissionDOS);
        this.permissionDOS = permissionDOS;
    }

    public List<PermissionDO> getPermissionDOS() {
        return permissionDOS;
    }
}

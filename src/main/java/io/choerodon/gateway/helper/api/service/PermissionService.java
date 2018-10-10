package io.choerodon.gateway.helper.api.service;

import io.choerodon.gateway.helper.domain.PermissionDO;

public interface PermissionService {

    PermissionDO selectPermissionByRequest(String uri, String method, String service);

}

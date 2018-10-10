package io.choerodon.gateway.helper.api.filter;

import com.google.common.cache.Cache;
import io.choerodon.gateway.helper.domain.RequestContext;
import io.choerodon.gateway.helper.api.service.PermissionService;
import io.choerodon.gateway.helper.domain.CheckState;
import io.choerodon.gateway.helper.domain.PermissionDO;
import org.springframework.stereotype.Component;

/**
 * 根据接口uri，method和service获取匹配到的权限
 * 匹配不到或者接口类型为内部接口，返回失败，不再向下执行
 */
@Component
public class GetPermissionFilter implements HelperFilter {

    private PermissionService permissionService;

    private Cache<String, PermissionDO> cache;


    public GetPermissionFilter(PermissionService permissionService, Cache<String, PermissionDO> cache) {
        this.permissionService = permissionService;
        this.cache = cache;
    }

    @Override
    public int filterOrder() {
        return 20;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        String key = context.getRequestKey();
        PermissionDO permissionDO = cache.getIfPresent(key);
        if (permissionDO == null) {
            PermissionDO selectPermission = permissionService.selectPermissionByRequest(context.getTrueUri(),
                    context.request.method, context.getRoute().getServiceId());
            if (selectPermission == null) {
                context.response.setStatus(CheckState.PERMISSION_MISMATCH);
                context.response.setMessage("This request mismatch any permission");
                return false;
            } else {
                cache.put(key, selectPermission);
                context.setPermission(selectPermission);
            }
        } else if (permissionDO.getWithin()) {
            context.response.setStatus(CheckState.PERMISSION_WITH_IN);
            context.response.setMessage("No access to within interface");
            return false;
        } else {
            context.setPermission(permissionDO);
        }
        return true;
    }

}

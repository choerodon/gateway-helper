package io.choerodon.gateway.helper.api.filter;

import io.choerodon.gateway.helper.domain.RequestContext;
import io.choerodon.gateway.helper.domain.CheckState;
import org.springframework.stereotype.Component;


/**
 * 公共接口的权限校验
 */
@Component
public class PublicRequestFilter implements HelperFilter {

    @Override
    public int filterOrder() {
        return 30;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return context.getPermission().getPublicAccess();
    }

    @Override
    public boolean run(RequestContext context) {
        context.response.setStatus(CheckState.SUCCESS_PUBLIC_ACCESS);
        context.response.setMessage("Have access to this 'publicAccess' interface, permission: " + context.getPermission());
        return false;
    }

}

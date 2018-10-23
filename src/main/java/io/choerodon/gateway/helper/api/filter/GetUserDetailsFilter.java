package io.choerodon.gateway.helper.api.filter;

import io.choerodon.gateway.helper.api.service.GetUserDetailsService;
import io.choerodon.gateway.helper.domain.CheckState;
import io.choerodon.gateway.helper.domain.CustomUserDetailsWithResult;
import io.choerodon.gateway.helper.domain.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 根据access_token获取对应的userDetails
 */
@Component
public class GetUserDetailsFilter implements HelperFilter {

    private GetUserDetailsService getUserDetailsService;

    public GetUserDetailsFilter(GetUserDetailsService getUserDetailsService) {
        this.getUserDetailsService = getUserDetailsService;
    }

    @Override
    public int filterOrder() {
        return 40;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean run(RequestContext context) {
        String accessToken = context.request.accessToken;
        if (StringUtils.isEmpty(accessToken)) {
            context.response.setStatus(CheckState.PERMISSION_ACCESS_TOKEN_NULL);
            context.response.setMessage("Access_token is empty, Please login and set access_token by HTTP header 'Authorization'");
            return false;
        }
        CustomUserDetailsWithResult result = getUserDetailsService.getUserDetails(accessToken);
        if (result.getCustomUserDetails() == null) {
            context.response.setStatus(result.getState());
            context.response.setMessage(result.getMessage());
            return false;
        }
        context.setCustomUserDetails(result.getCustomUserDetails());
        return true;
    }

}

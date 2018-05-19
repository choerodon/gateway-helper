package io.choerodon.gateway.helper.permission;

import javax.servlet.http.HttpServletRequest;

/**
 * @author flyleft
 * @date 2018/5/3
 */
public interface RequestPermissionFilter {

    boolean permission(final HttpServletRequest request);

}

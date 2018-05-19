package io.choerodon.gateway.helper.ratelimit;

import javax.servlet.http.HttpServletRequest;

/**
 * @author flyleft
 * @date 2018/5/3
 */
public interface RequestRatelimitFilter {

    boolean through(final HttpServletRequest request);

}

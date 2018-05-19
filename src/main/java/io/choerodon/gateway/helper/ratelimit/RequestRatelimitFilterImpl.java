package io.choerodon.gateway.helper.ratelimit;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author flyleft
 * @date 2018/5/3
 */
@Service
public class RequestRatelimitFilterImpl implements RequestRatelimitFilter {

    @Override
    public boolean through(HttpServletRequest request) {
        return true;
    }

}

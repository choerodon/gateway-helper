package io.choerodon.gateway.helper.jwt;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.zuul.ZuulFilter;
import org.springframework.stereotype.Component;

@Component
public class ClearContextZuulFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
        return null;
    }
}

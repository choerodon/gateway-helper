package io.choerodon.gateway.helper.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.gateway.helper.common.utils.FilterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_TOKEN;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 定义filter的config
 *
 * @author flyleft
 * @date 18-1-4
 */
@Component
public class JwtAddFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAddFilter.class);

    private static final String DEFAULT_PASS = "unknown";

    private Signer jwtSigner;

    private ObjectMapper mapper;

    private CustomUserDetails defaultUser;

    public JwtAddFilter(Signer jwtSigner) {
        this.jwtSigner = jwtSigner;
        mapper = new ObjectMapper();
        defaultUser = new CustomUserDetails("admin", DEFAULT_PASS, Collections.emptyList());
        defaultUser.setUserId(1L);
        defaultUser.setOrganizationId(1L);
        defaultUser.setLanguage("zh_CN");
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }
        final CustomUserDetails details = DetailsHelper.getUserDetails();
        if (details == null) {
            LOGGER.info("can't get customUserDetails, add JWT failed, request uri {} method {} ",
                    ctx.getRequest().getRequestURI(), ctx.getRequest().getMethod());
        } else {
            try {
                String token = mapper.writeValueAsString(details);
                String jwt = "Bearer " + JwtHelper.encode(token, jwtSigner).getEncoded();
                ctx.getResponse().setHeader(HEADER_TOKEN, jwt);
                ctx.setResponseStatusCode(HttpStatus.OK.value());
                ctx.setResponseBody("");
                ctx.getResponse().setContentType("text/plain");
                ctx.set(FilterConstants.ACCESS_TOKEN, jwt);
            } catch (JsonProcessingException e) {
                LOGGER.warn("error happened when add JWT : {}", e.toString());
            }
        }
        return null;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.ACCESS_TOKEN_VERIFY_FILTER;
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }


}

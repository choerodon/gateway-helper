package io.choerodon.gateway.helper.api.filter;

import io.choerodon.gateway.helper.domain.CheckRequest;
import io.choerodon.gateway.helper.domain.CheckResponse;
import io.choerodon.gateway.helper.domain.CheckState;
import io.choerodon.gateway.helper.domain.RequestContext;
import io.choerodon.gateway.helper.infra.exception.PermissionMultiplyMatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_JWT;
import static io.choerodon.core.variable.RequestVariableHolder.HEADER_TOKEN;

@Component
public class RootServletFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootServletFilter.class);

    private List<HelperFilter> helperFilters;

    public RootServletFilter(Optional<List<HelperFilter>> optionalHelperFilters) {
        helperFilters = optionalHelperFilters.orElseGet(Collections::emptyList)
                .stream()
                .sorted(Comparator.comparing(HelperFilter::filterOrder))
                .collect(Collectors.toList());
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        RequestContext requestContext = new RequestContext(new CheckRequest(req.getHeader(HEADER_TOKEN),
                req.getRequestURI(), req.getMethod().toLowerCase()), new CheckResponse());
        CheckResponse checkResponse = requestContext.response;
        try {
            for (HelperFilter t : helperFilters) {
                if (t.shouldFilter(requestContext) && !t.run(requestContext)) {
                    break;
                }
            }
        } catch (PermissionMultiplyMatchException e) {
            checkResponse.setStatus(CheckState.API_ERROR_MATCH_MULTIPLY);
            checkResponse.setMessage("This request match multiply permission, permissions: " + e.getPermissionDOS());
        } catch (Exception e) {
            checkResponse.setStatus(CheckState.EXCEPTION_GATEWAY_HELPER);
            checkResponse.setMessage("gateway helper error happened: " + e.toString());
            LOGGER.info("Check permission error", e);
        }
        request.setCharacterEncoding("utf-8");
        res.setHeader("Content-type", "text/html;charset=UTF-8");
        res.setCharacterEncoding("utf-8");
        if (checkResponse.getStatus().getValue() < 300) {
            res.setStatus(200);
            LOGGER.debug("Request 200, context: {}", requestContext);
        } else if (checkResponse.getStatus().getValue() < 500) {
            res.setStatus(403);
            LOGGER.info("Request 403, context: {}", requestContext);
        } else {
            res.setStatus(500);
            LOGGER.info("Request 500, context: {}", requestContext);
        }
        if (checkResponse.getJwt() != null) {
            res.setHeader(HEADER_JWT, checkResponse.getJwt());
        }
        if (checkResponse.getMessage() != null) {
            res.setHeader("request-message", checkResponse.getMessage());
        }
        res.setHeader("request-status", checkResponse.getStatus().name());
        res.setHeader("request-code", checkResponse.getStatus().getCode());
        try (PrintWriter out = res.getWriter()) {
            out.flush();
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }

}

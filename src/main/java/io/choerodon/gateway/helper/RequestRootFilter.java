package io.choerodon.gateway.helper;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.helper.HelperZuulRoutesProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.choerodon.gateway.helper.common.utils.RequestRibbonForwardUtils;
import io.choerodon.gateway.helper.permission.RequestPermissionFilter;
import io.choerodon.gateway.helper.ratelimit.RequestRatelimitFilter;

/**
 * 权限校验器
 *
 * @author zhipeng.zuo
 */
@RefreshScope
@Service("requestRootFilter")
public class RequestRootFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRootFilter.class);

    private HelperZuulRoutesProperties helperZuulRoutesProperties;

    private RequestPermissionFilter requestPermissionFilter;

    private RequestRatelimitFilter requestRatelimitFilter;

    private RibbonCommandFactory<?> ribbonCommandFactory;

    @SuppressWarnings("rawtypes")
    @Autowired(required = false)
    private List<RibbonRequestCustomizer> requestCustomizers = Collections.emptyList();

    @Autowired
    public RequestRootFilter(HelperZuulRoutesProperties helperZuulRoutesProperties,
                             RequestPermissionFilter requestPermissionFilter,
                             RequestRatelimitFilter requestRatelimitFilter,
                             RibbonCommandFactory<?> ribbonCommandFactory) {
        this.helperZuulRoutesProperties = helperZuulRoutesProperties;
        this.requestPermissionFilter = requestPermissionFilter;
        this.requestRatelimitFilter = requestRatelimitFilter;
        this.ribbonCommandFactory = ribbonCommandFactory;
    }

    public boolean filter(final HttpServletRequest request) {
        String uri = RequestRibbonForwardUtils.buildZuulRequestUri(request);
        String service = RequestRibbonForwardUtils.getHelperServiceByUri(helperZuulRoutesProperties, uri);
        if (StringUtils.isEmpty(service)) {
            return requestPermissionFilter.permission(request) && requestRatelimitFilter.through(request);
        }
        return customGatewayHelperFilter(request, service, uri);
    }

    private boolean customGatewayHelperFilter(final HttpServletRequest request, final String service, final String uri) {
        ClientHttpResponse clientHttpResponse = null;
        try {
            RibbonCommandContext commandContext = RequestRibbonForwardUtils.buildCommandContext(request, requestCustomizers, service, uri);
            clientHttpResponse = RequestRibbonForwardUtils.forward(commandContext, ribbonCommandFactory);
            return clientHttpResponse.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            LOGGER.warn("error.customGatewayHelperFilter");
            return false;
        } finally {
            if (clientHttpResponse != null) {
                clientHttpResponse.close();
            }
        }
    }

}

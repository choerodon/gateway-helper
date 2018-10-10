package io.choerodon.gateway.helper.domain;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.gateway.helper.domain.CheckRequest;
import io.choerodon.gateway.helper.domain.CheckResponse;
import io.choerodon.gateway.helper.domain.PermissionDO;
import org.springframework.cloud.config.client.ZuulRoute;

public class RequestContext {

    public final CheckRequest request;

    public final CheckResponse response;

    private String requestKey;

    private PermissionDO permission;

    private ZuulRoute route;

    private String trueUri;

    private CustomUserDetails customUserDetails;

    public RequestContext(CheckRequest request, CheckResponse builder) {
        this.request = request;
        this.response = builder;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public PermissionDO getPermission() {
        return permission;
    }

    public void setPermission(PermissionDO permission) {
        this.permission = permission;
    }

    public ZuulRoute getRoute() {
        return route;
    }

    public void setRoute(ZuulRoute route) {
        this.route = route;
    }

    public String getTrueUri() {
        return trueUri;
    }

    public void setTrueUri(String trueUri) {
        this.trueUri = trueUri;
    }

    public CustomUserDetails getCustomUserDetails() {
        return customUserDetails;
    }

    public void setCustomUserDetails(CustomUserDetails customUserDetails) {
        this.customUserDetails = customUserDetails;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "request=" + request +
                ", response=" + response +
                ", requestKey='" + requestKey + '\'' +
                ", permission=" + permission +
                ", route=" + route +
                ", trueUri='" + trueUri + '\'' +
                ", customUserDetails=" + customUserDetails +
                '}';
    }
}


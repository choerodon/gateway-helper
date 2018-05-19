package io.choerodon.event.store.controller;

import io.choerodon.gateway.helper.common.ZuulRoutesProperties;
import io.choerodon.gateway.helper.common.utils.ZuulPathUtils;
import org.junit.Test;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xausky on 5/16/17.
 */
public class BaseControllerTest {

    @Test
    public void zuulUtilsTest() {
        Map<String, ZuulProperties.ZuulRoute> routeMap = new HashMap<>();
        ZuulProperties.ZuulRoute org  = new ZuulProperties.ZuulRoute();
        org.setId("org");
        org.setPath("/org/**");

        ZuulProperties.ZuulRoute file = new ZuulProperties.ZuulRoute();
        file.setId("file");
        file.setPath("/file/**");


        ZuulProperties.ZuulRoute left = new ZuulProperties.ZuulRoute();
        left.setId("left");
        left.setPath("/left/**");

        routeMap.put("organization", org);
        routeMap.put("files", file);
        routeMap.put("leftsss", left);

        ZuulProperties.ZuulRoute org1 = ZuulPathUtils.getRoute("/org/jsjdjs/sdjsd/sds?jskds=2", routeMap);
        ZuulProperties.ZuulRoute files = ZuulPathUtils.getRoute("/file/jsjdjs/sdjsd/sds?jskds=2", routeMap);
        ZuulProperties.ZuulRoute lefts = ZuulPathUtils.getRoute("/lefts/jsjdjs/sdjsd/sds?jskds=2", routeMap);

    }
}

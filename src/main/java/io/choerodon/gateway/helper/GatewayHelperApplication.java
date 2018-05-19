package io.choerodon.gateway.helper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.ZuulProxyAutoConfiguration;
import org.springframework.cloud.netflix.zuul.ZuulServerAutoConfiguration;
import org.springframework.cloud.security.oauth2.proxy.OAuth2ProxyAutoConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 运行主类
 * 排除spring cloud封装的zuul
 *
 * @author zhipeng.zuo
 * @date 17-12-26
 */
@EnableRetry
@EnableEurekaClient
@SpringBootApplication(exclude = {
        OAuth2ProxyAutoConfiguration.class,
        ZuulProxyAutoConfiguration.class,
        ZuulServerAutoConfiguration.class})
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class GatewayHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayHelperApplication.class, args);
    }

}
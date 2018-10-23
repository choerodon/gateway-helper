package io.choerodon.gateway.helper.infra.config;

import io.choerodon.gateway.helper.api.filter.RootServletFilter;
import io.choerodon.gateway.helper.infra.properties.HelperProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(HelperProperties.class)
public class HelperFilterConfig {

    @Bean
    public FilterRegistrationBean gatewayHelperFilterRegistrationBean(RootServletFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("rootServletFilter");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public Signer jwtSigner(HelperProperties helperProperties) {
        return new MacSigner(helperProperties.getJwtKey());
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}

package io.choerodon.gateway.helper.infra.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.choerodon.gateway.helper.api.filter.RootServletFilter;
import io.choerodon.gateway.helper.domain.PermissionDO;
import io.choerodon.gateway.helper.infra.properties.HelperProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(HelperProperties.class)
public class HelperFilterConfig {

    @Autowired
    private HelperProperties helperProperties;

    @Bean
    public Cache<String, PermissionDO> requestPermissionCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(helperProperties.getPermission().getCacheSize())
                .expireAfterWrite(helperProperties.getPermission().getCacheSeconds(), TimeUnit.SECONDS)
                .build();
    }

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
    public Signer jwtSigner() {
        return new MacSigner(helperProperties.getJwtKey());
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}

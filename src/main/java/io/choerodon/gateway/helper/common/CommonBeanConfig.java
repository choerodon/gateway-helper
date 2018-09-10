package io.choerodon.gateway.helper.common;

import javax.servlet.Filter;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

/**
 * @author flyleft
 * @date 2018/4/17
 */
@Configuration
public class CommonBeanConfig {

    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean("oauth_rest")
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}

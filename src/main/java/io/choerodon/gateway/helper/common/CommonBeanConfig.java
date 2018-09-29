package io.choerodon.gateway.helper.common;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author flyleft
 */
@Configuration
public class CommonBeanConfig {

    @Bean("oauth_rest")
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}

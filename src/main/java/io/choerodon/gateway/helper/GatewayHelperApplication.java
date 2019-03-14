package io.choerodon.gateway.helper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author flyleft
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableCaching
public class GatewayHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayHelperApplication.class, args);
    }

}
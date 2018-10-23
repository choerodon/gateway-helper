package io.choerodon.gateway.helper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author flyleft
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableCaching
public class GatewayHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayHelperApplication.class, args);
    }

}
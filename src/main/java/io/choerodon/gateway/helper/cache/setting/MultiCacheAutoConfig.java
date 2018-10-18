package io.choerodon.gateway.helper.cache.setting;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MultiCacheProperties.class)
public class MultiCacheAutoConfig {
}

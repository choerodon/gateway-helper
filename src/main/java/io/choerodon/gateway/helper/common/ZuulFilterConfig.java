package io.choerodon.gateway.helper.common;

import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.http.ZuulServlet;
import com.netflix.zuul.monitoring.CounterFactory;
import com.netflix.zuul.monitoring.TracerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.zuul.ZuulFilterInitializer;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.cloud.netflix.zuul.filters.route.apache.HttpClientRibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.metrics.DefaultCounterFactory;
import org.springframework.cloud.netflix.zuul.metrics.EmptyCounterFactory;
import org.springframework.cloud.netflix.zuul.metrics.EmptyTracerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 定义filter的config
 *
 * @author zhipeng.zuo
 * @date 18-1-4
 */
@Configuration
@ConditionalOnClass(ZuulServlet.class)
@EnableConfigurationProperties(ZuulProperties.class)
public class ZuulFilterConfig {

    @Autowired
    protected ServerProperties server;

    /**
     * 注册ZuulServlet
     *
     * @return ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean zuulServlet() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(new ZuulServlet(), "/*");
        servlet.addInitParameter("buffer-requests", "false");
        return servlet;
    }

    @Configuration
    protected static class ZuulFilterConfiguration {
        @Autowired
        private Map<String, ZuulFilter> filters;

        @Bean
        public ZuulFilterInitializer zuulFilterInitializer(
                CounterFactory counterFactory, TracerFactory tracerFactory) {
            FilterLoader filterLoader = FilterLoader.getInstance();
            FilterRegistry filterRegistry = FilterRegistry.instance();
            return new ZuulFilterInitializer(this.filters, counterFactory, tracerFactory, filterLoader, filterRegistry);
        }
    }

    @Configuration
    @ConditionalOnClass(CounterService.class)
    protected static class ZuulCounterFactoryConfiguration {
        @Bean
        @ConditionalOnBean(CounterService.class)
        public CounterFactory counterFactory(CounterService counterService) {
            return new DefaultCounterFactory(counterService);
        }
    }

    @Configuration
    protected static class ZuulMetricsConfiguration {
        @Bean
        @ConditionalOnMissingBean(CounterFactory.class)
        public CounterFactory counterFactory() {
            return new EmptyCounterFactory();
        }

        @ConditionalOnMissingBean(TracerFactory.class)
        @Bean
        public TracerFactory tracerFactory() {
            return new EmptyTracerFactory();
        }

    }

    @Autowired(required = false)
    private Set<ZuulFallbackProvider> zuulFallbackProviders = Collections.emptySet();

    @Bean
    @ConditionalOnMissingBean
    public RibbonCommandFactory<?> ribbonCommandFactory(
            SpringClientFactory clientFactory, ZuulProperties zuulProperties) {
        return new HttpClientRibbonCommandFactory(clientFactory, zuulProperties, zuulFallbackProviders);
    }

}
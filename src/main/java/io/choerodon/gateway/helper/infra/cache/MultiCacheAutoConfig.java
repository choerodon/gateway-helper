package io.choerodon.gateway.helper.infra.cache;

import io.choerodon.gateway.helper.infra.cache.l1.L1CacheManager;
import io.choerodon.gateway.helper.infra.cache.l1.caffeine.CaffeineL1CacheManager;
import io.choerodon.gateway.helper.infra.cache.l1.guava.GuavaL1CacheManager;
import io.choerodon.gateway.helper.infra.cache.l2.L2CacheManager;
import io.choerodon.gateway.helper.infra.cache.l2.redis.RedisL2CacheManager;
import io.choerodon.gateway.helper.infra.cache.multi.MultiCacheManager;
import io.choerodon.gateway.helper.infra.cache.setting.MultiCacheProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * redis config copy from RedisAutoConfiguration and RedisCacheConfiguration
 */
@Configuration
@EnableConfigurationProperties({MultiCacheProperties.class, RedisProperties.class})
public class MultiCacheAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(
            ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
        return new CacheManagerCustomizers(customizers.getIfAvailable());
    }

    @Bean
    @Primary
    @ConditionalOnExpression("#{'${spring.cache.multi.l1.enabled}'=='true' || " +
            "'${spring.cache.multi.l1.enabled}'=='true'}")
    public MultiCacheManager multiCacheManager(Optional<L2CacheManager> l2CacheManagerOptional,
                                               MultiCacheProperties multiCacheProperties) {
        L1CacheManager l1CacheManager = null;
        if (multiCacheProperties.getL1().isEnabled()) {
            if (CaffeineL1CacheManager.type().equals(multiCacheProperties.getL1().getType())) {
                l1CacheManager = new CaffeineL1CacheManager();
            } else if (GuavaL1CacheManager.type().equals(multiCacheProperties.getL1().getType())) {
                l1CacheManager = new GuavaL1CacheManager();
            }
        }
        L2CacheManager l2CacheManager = null;
        if (l2CacheManagerOptional.isPresent()) {
            l2CacheManager = l2CacheManagerOptional.get();
        }
        return new MultiCacheManager(l1CacheManager, l2CacheManager, multiCacheProperties);

    }

    @Configuration
    @ConditionalOnExpression("#{'${spring.cache.multi.l2.enabled}'=='true' &&" +
            " '${spring.cache.multi.l2.type}'=='redis'}")
    @ConditionalOnClass({JedisConnection.class, RedisOperations.class, Jedis.class})
    public static class RedisConfig {

        private final RedisProperties properties;

        private final RedisSentinelConfiguration sentinelConfiguration;

        private final RedisClusterConfiguration clusterConfiguration;

        public RedisConfig(RedisProperties properties,
                           ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                           ObjectProvider<RedisClusterConfiguration> clusterConfiguration) {
            this.properties = properties;
            this.sentinelConfiguration = sentinelConfiguration.getIfAvailable();
            this.clusterConfiguration = clusterConfiguration.getIfAvailable();
        }

        @Bean
        @ConditionalOnMissingBean(RedisConnectionFactory.class)
        public JedisConnectionFactory redisConnectionFactory() {
            return applyProperties(createJedisConnectionFactory());
        }

        protected final JedisConnectionFactory applyProperties(
                JedisConnectionFactory factory) {
            configureConnection(factory);
            if (this.properties.isSsl()) {
                factory.setUseSsl(true);
            }
            factory.setDatabase(this.properties.getDatabase());
            if (this.properties.getTimeout() > 0) {
                factory.setTimeout(this.properties.getTimeout());
            }
            return factory;
        }

        private void configureConnection(JedisConnectionFactory factory) {
            if (StringUtils.hasText(this.properties.getUrl())) {
                configureConnectionFromUrl(factory);
            } else {
                factory.setHostName(this.properties.getHost());
                factory.setPort(this.properties.getPort());
                if (this.properties.getPassword() != null) {
                    factory.setPassword(this.properties.getPassword());
                }
            }
        }

        private void configureConnectionFromUrl(JedisConnectionFactory factory) {
            String url = this.properties.getUrl();
            if (url.startsWith("rediss://")) {
                factory.setUseSsl(true);
            }
            try {
                URI uri = new URI(url);
                factory.setHostName(uri.getHost());
                factory.setPort(uri.getPort());
                if (uri.getUserInfo() != null) {
                    String password = uri.getUserInfo();
                    int index = password.indexOf(':');
                    if (index >= 0) {
                        password = password.substring(index + 1);
                    }
                    factory.setPassword(password);
                }
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url,
                        ex);
            }
        }

        protected final RedisSentinelConfiguration getSentinelConfig() {
            if (this.sentinelConfiguration != null) {
                return this.sentinelConfiguration;
            }
            RedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();
            if (sentinelProperties != null) {
                RedisSentinelConfiguration config = new RedisSentinelConfiguration();
                config.master(sentinelProperties.getMaster());
                config.setSentinels(createSentinels(sentinelProperties));
                return config;
            }
            return null;
        }

        protected final RedisClusterConfiguration getClusterConfiguration() {
            if (this.clusterConfiguration != null) {
                return this.clusterConfiguration;
            }
            if (this.properties.getCluster() == null) {
                return null;
            }
            RedisProperties.Cluster clusterProperties = this.properties.getCluster();
            RedisClusterConfiguration config = new RedisClusterConfiguration(
                    clusterProperties.getNodes());

            if (clusterProperties.getMaxRedirects() != null) {
                config.setMaxRedirects(clusterProperties.getMaxRedirects());
            }
            return config;
        }

        private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
            List<RedisNode> nodes = new ArrayList<>();
            for (String node : StringUtils
                    .commaDelimitedListToStringArray(sentinel.getNodes())) {
                try {
                    String[] parts = StringUtils.split(node, ":");
                    Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                    nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
                } catch (RuntimeException ex) {
                    throw new IllegalStateException(
                            "Invalid redis sentinel " + "property '" + node + "'", ex);
                }
            }
            return nodes;
        }

        private JedisConnectionFactory createJedisConnectionFactory() {
            JedisPoolConfig poolConfig = (this.properties.getPool() != null
                    ? jedisPoolConfig() : new JedisPoolConfig());

            if (getSentinelConfig() != null) {
                return new JedisConnectionFactory(getSentinelConfig(), poolConfig);
            }
            if (getClusterConfiguration() != null) {
                return new JedisConnectionFactory(getClusterConfiguration(), poolConfig);
            }
            return new JedisConnectionFactory(poolConfig);
        }

        private JedisPoolConfig jedisPoolConfig() {
            JedisPoolConfig config = new JedisPoolConfig();
            RedisProperties.Pool props = this.properties.getPool();
            config.setMaxTotal(props.getMaxActive());
            config.setMaxIdle(props.getMaxIdle());
            config.setMinIdle(props.getMinIdle());
            config.setMaxWaitMillis(props.getMaxWait());
            return config;
        }

        @Bean
        @ConditionalOnMissingBean(name = "redisTemplate")
        public RedisTemplate<Object, Object> redisTemplate(
                RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        @ConditionalOnMissingBean(StringRedisTemplate.class)
        public StringRedisTemplate stringRedisTemplate(
                RedisConnectionFactory redisConnectionFactory) {
            StringRedisTemplate template = new StringRedisTemplate();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        @ConditionalOnMissingBean(L2CacheManager.class)
        public L2CacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate,
                                           CacheManagerCustomizers customizerInvoker) {
            RedisL2CacheManager cacheManager = new RedisL2CacheManager(redisTemplate);
            cacheManager.setUsePrefix(true);
            return customizerInvoker.customize(cacheManager);
        }
    }

}

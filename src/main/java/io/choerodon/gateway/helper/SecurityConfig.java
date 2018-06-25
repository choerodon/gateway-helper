package io.choerodon.gateway.helper;

import io.choerodon.core.oauth.CustomTokenConverter;
import io.choerodon.gateway.helper.jwt.CustomPrincipalExtractor;
import io.choerodon.gateway.helper.jwt.EurekaOAuth2RestTemplate;
import io.choerodon.gateway.helper.permission.PermissionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;

/**
 * JWT获取和权限校验配置类
 *
 * @author flyleft
 */
@Configuration
@EnableResourceServer
@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
@EnableConfigurationProperties(PermissionProperties.class)
public class SecurityConfig extends ResourceServerConfigurerAdapter {

    private ApplicationContext applicationContext;

    private CustomPrincipalExtractor customPrincipalExtractor;

    private UserInfoTokenServices userInfoTokenServices;

    private EurekaOAuth2RestTemplate eurekaOAuth2RestTemplate;

    private PermissionProperties permissionProperties;

    @Autowired
    public SecurityConfig(ApplicationContext applicationContext, CustomPrincipalExtractor customPrincipalExtractor,
                          UserInfoTokenServices userInfoTokenServices, EurekaOAuth2RestTemplate eurekaOAuth2RestTemplate,
                          PermissionProperties permissionProperties) {
        this.applicationContext = applicationContext;
        this.customPrincipalExtractor = customPrincipalExtractor;
        this.userInfoTokenServices = userInfoTokenServices;
        this.eurekaOAuth2RestTemplate = eurekaOAuth2RestTemplate;
        this.permissionProperties = permissionProperties;
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        userInfoTokenServices.setPrincipalExtractor(customPrincipalExtractor);
        userInfoTokenServices.setRestTemplate(eurekaOAuth2RestTemplate);
        config.resourceId("default")
                .expressionHandler(oauthWebSecurityExpressionHandler(applicationContext))
                .tokenServices(userInfoTokenServices);
    }

    /**
     * 设置默认AccessTokenConverter
     *
     * @return 自定义的AccessTokenConverter
     */
    @Bean
    @Primary
    public AccessTokenConverter accessTokenConverter() {
        return new CustomTokenConverter();
    }

    /**
     * 为 OAuth2WebSecurityExpressionHandler 添加 applicationContext
     *
     * @param applicationContext Spring ApplicationContext
     * @return 添加后的 OAuth2WebSecurityExpressionHandler
     */
    @Bean
    public OAuth2WebSecurityExpressionHandler oauthWebSecurityExpressionHandler(
            ApplicationContext applicationContext) {
        OAuth2WebSecurityExpressionHandler expressionHandler =
                new OAuth2WebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }

    @Bean
    public Signer jwtSigner() {
        return new MacSigner(permissionProperties.getJwt().getKey());
    }

    /**
     * 配置权限校验
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest()
                .access("@requestRootFilter.filter(request)");
    }
}

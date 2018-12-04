package com.sodo.xmarketing.auth;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class OAuth2ServerConfiguration {

  private static final String RESOURCE_ID = "sodo-sod";
  private static final String CUSTOMER_APP = "customer";
  private static final String EMPLOYEE_APP = "employee";

  private static final String CUSTOMER_PASS = "sod-customer";
  private static final String EMPLOYEE_PASS = "sod-employee";

  private static final String PASS = "password";

  @Configuration
  @EnableResourceServer
  protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
      resources.resourceId(RESOURCE_ID).tokenStore(tokenStore);
    }
    
    @Override
    public void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http.csrf().disable().authorizeRequests()
      .antMatchers("/api/customer/exist/**").permitAll()
      .antMatchers("/api/customer/register/**").permitAll()
      .antMatchers("/api/customer/email/**").permitAll()
      .antMatchers("/api/customer/username/**").permitAll()
      .antMatchers("/api/customer/{username}/**").permitAll()
      .antMatchers("/api/v1/domains/**").permitAll()
      .antMatchers("/api/v1/domains/{domainName}").permitAll()
      .antMatchers("/api/v1/domains/cms/domains/{domainName}").permitAll()
      .antMatchers("/api/order/agency").permitAll()
      .antMatchers("/api/order/agency/{order-code}").permitAll()
      .antMatchers("/api/service-price/agency").permitAll()
      .antMatchers("/api/**").authenticated();
      // @formatter:on
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();

      // @formatter:off
      List<String> allowedDomains = Arrays.asList("*");
      // @formatter:on

      config.setAllowCredentials(true);
      config.setAllowedOrigins(allowedDomains);
      config.addAllowedHeader("*");
      config.addAllowedMethod("*");
      source.registerCorsConfiguration("/**", config);

      FilterRegistrationBean<CorsFilter> bean =
          new FilterRegistrationBean<>(new CorsFilter(source));

      bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
      return bean;
    }

  }

  @Configuration
  @EnableAuthorizationServer
  protected static class AuthorizationServerConfiguration
  extends AuthorizationServerConfigurerAdapter {

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private TokenStore tokenStore;

    /**
     * A PasswordEncoder instance to hash clear test password values.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

      TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
      tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter));

      endpoints.userDetailsService(userDetailsService);
      endpoints.tokenStore(tokenStore).tokenEnhancer(tokenEnhancerChain)
      .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      //@formatter:off
      clients.inMemory()
      .withClient(CUSTOMER_APP)
      .authorizedGrantTypes(PASS, "refresh_token")
      .scopes("read", "write")
      .resourceIds(RESOURCE_ID)
      .secret(passwordEncoder.encode(CUSTOMER_PASS))
      .accessTokenValiditySeconds(72 * 60 * 60)
      .refreshTokenValiditySeconds(72 * 60 * 60)
      .and()
      .withClient(EMPLOYEE_APP)
      .authorizedGrantTypes(PASS, "refresh_token")
      .scopes("read", "write")
      .resourceIds(RESOURCE_ID)
      .secret(passwordEncoder.encode(EMPLOYEE_PASS))
      .accessTokenValiditySeconds(12 * 60 * 60)
      .refreshTokenValiditySeconds(12 * 60 * 60);
      //@formatter:on
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
      return new CustomTokenEnhancer();
    }
    
  }
  
  @Configuration
  @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true )
  class CustomGlobalMethodSecurity extends GlobalMethodSecurityConfiguration {

		@Bean
	    protected AccessDecisionManager accessDecisionManager() {
	        AffirmativeBased accessDecisionManager = (AffirmativeBased) super.accessDecisionManager();

	        //Remove the ROLE_ prefix from RoleVoter for @Secured and hasRole checks on methods
	        accessDecisionManager.getDecisionVoters().stream()
	                .filter(RoleVoter.class::isInstance)
	                .map(RoleVoter.class::cast)
	                .forEach(it -> it.setRolePrefix(""));

	        return accessDecisionManager;
	    }
	}

}

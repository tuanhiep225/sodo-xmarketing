/**
 * 
 */
package com.sodo.xmarketing.auth;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.utils.Constant;

/**
 * @author tuanhiep225
 *
 */
@Service
public class SodFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

  public static ThreadLocal<Map<String, Object>> additionalInfo = new ThreadLocal<>();
  @Autowired
  private TokenStore tokenStore;
  /**
   * A Spring Security UserDetailsService implementation based upon the Account entity model.
   */
  @Autowired
  private CustomUserDetailsService userDetailsService;
  
  @Autowired
  private SodFilterInvocationSecurityMetadataSource securityMetadataSource;


  @Autowired
  public void setSodAccessDecisionManager(SodAccessDecisionManager sodAccessDecisionManager) {
    super.setAccessDecisionManager(sodAccessDecisionManager);
  }

  @Override
  public void init(FilterConfig filterConfig) {
    // Do nothing here
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof OAuth2Authentication) {
      OAuth2Authentication oauth2 = (OAuth2Authentication) authentication;

      // Check customer
      if (oauth2.getOAuth2Request() != null
          && Constant.CUSTOMER_APP.equals(oauth2.getOAuth2Request().getClientId())) {
        userDetailsService.setCustomer(true);
      } else if (oauth2.getOAuth2Request() != null
          && Constant.EMPLOYEE_APP.equals(oauth2.getOAuth2Request().getClientId())) {
        userDetailsService.setCustomer(false);
      }

      // Get user information
      OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) oauth2.getDetails();
      OAuth2AccessToken token = tokenStore.readAccessToken(details.getTokenValue());

      Map<String, Object> additionalInfo = token.getAdditionalInformation();

      SodFilterSecurityInterceptor.additionalInfo.set(additionalInfo);


      userDetailsService.loadUserByUsername((String) authentication.getPrincipal());
    }

    FilterInvocation fi = new FilterInvocation(request, response, chain);
    invoke(fi);
  }

  public void invoke(FilterInvocation fi) throws IOException, ServletException {
    InterceptorStatusToken token = super.beforeInvocation(fi);

    try {
      fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
    } finally {
      super.afterInvocation(token, null);
    }
  }

  @Override
  public void destroy() {
    // Do nothing here
  }

  @Override
  public Class<?> getSecureObjectClass() {
    return FilterInvocation.class;
  }

@Override
public SecurityMetadataSource obtainSecurityMetadataSource() {
	return securityMetadataSource;
}

}

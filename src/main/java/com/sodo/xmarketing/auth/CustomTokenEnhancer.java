/**
 *
 */
package com.sodo.xmarketing.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.sodo.xmarketing.auth.CustomUserDetailsService.CustomUser;

/**
 * @author tuanh
 */
public class CustomTokenEnhancer implements TokenEnhancer {

  public static final String CUSTOMER_APP = "customer";
  public static final String EMPLOYEE_APP = "employee";

  @Autowired
  CustomUserDetailsService userDetailsService;

  @Override
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
      OAuth2Authentication authentication) {
    Map<String, Object> additionalInfo = new HashMap<>();

    if (authentication.getOAuth2Request() != null
        && CUSTOMER_APP.equals(authentication.getOAuth2Request().getClientId())) {
      userDetailsService.setCustomer(true);
    } else if (authentication.getOAuth2Request() != null
        && EMPLOYEE_APP.equals(authentication.getOAuth2Request().getClientId())) {
      userDetailsService.setCustomer(false);
    }

    CustomUser user = (CustomUser) userDetailsService.loadUserByUsername(authentication.getName());

    additionalInfo.put("isCustomer", userDetailsService.isCustomer());
    additionalInfo.put("email", user.getEmail());
    additionalInfo.put("roles", user.getRoles());
    additionalInfo.put("code", user.getCode());
    additionalInfo.put("username", user.getUsername());
    additionalInfo.put("culture", user.getCulture());
    additionalInfo.put("name", user.getName());

    ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

    return accessToken;
  }
}

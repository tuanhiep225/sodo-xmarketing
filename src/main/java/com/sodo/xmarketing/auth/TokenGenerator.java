package com.sodo.xmarketing.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

  @Autowired
  AuthorizationServerTokenServices tokenService;

  public OAuth2AccessToken generate(String username, boolean isCustomer) {
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

    Map<String, String> requestParameters = new HashMap<>();

    String clientId = "employee";
    if (isCustomer) {
      clientId = "customer";
    }

    boolean approved = true;

    Set<String> scope = new HashSet<>();
    scope.add("scope");

    Set<String> resourceIds = new HashSet<>();
    resourceIds.add("sodo-sod");

    Set<String> responseTypes = new HashSet<>();
    responseTypes.add("code");
    Map<String, Serializable> extensionProperties = new HashMap<>();

    OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId, authorities,
        approved, scope, resourceIds, null, responseTypes, extensionProperties);

    User userPrincipal = new User(username, "", true, true, true, true, authorities);

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
    OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);
    return tokenService.createAccessToken(auth);
  }
}

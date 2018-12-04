/**
 * 
 */
package com.sodo.xmarketing.auth;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * @author tuanhiep225
 *
 */
@Service
public class SodAccessDecisionManager implements AccessDecisionManager {

  @Override
  public void decide(Authentication authentication, Object object,
      Collection<ConfigAttribute> configAttributes) {

    if (null == configAttributes || configAttributes.isEmpty()) {
      return;
    }

    ConfigAttribute config;
    String role;

    for (Iterator<ConfigAttribute> iter = configAttributes.iterator(); iter.hasNext(); ) {
      config = iter.next();
      role = config.getAttribute();
      for (GrantedAuthority ga : authentication.getAuthorities()) {
        if (role.trim().equals(ga.getAuthority())) {
          return;
        }
      }
    }

    throw new AccessDeniedException("No Permission.");
  }

  @Override
  public boolean supports(ConfigAttribute attribute) {
    return true;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return true;
  }

}

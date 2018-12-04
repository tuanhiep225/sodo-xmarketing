package com.sodo.xmarketing.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Created by tahi1990 on 27/06/2017.
 */

@Component
public class AccountAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  /**
   * A Spring Security UserDetailsService implementation based upon the Account entity model.
   */
  @Autowired
  private CustomUserDetailsService userDetailsService;

  /**
   * A PasswordEncoder instance to hash clear test password values.
   */
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails,
      UsernamePasswordAuthenticationToken token) {
    if (token.getCredentials() == null || userDetails.getPassword() == null) {
      throw new BadCredentialsException("Credentials may not be null.");
    }

    if (!passwordEncoder.matches((String) token.getCredentials(), userDetails.getPassword())) {
      throw new BadCredentialsException("Invalid credentials.");
    }
  }

  @Override
  protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken token) {
    return userDetailsService.loadUserByUsername(username);
  }

}

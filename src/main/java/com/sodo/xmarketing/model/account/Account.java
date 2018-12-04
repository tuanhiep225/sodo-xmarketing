package com.sodo.xmarketing.model.account;

import com.sodo.xmarketing.model.entity.BaseEntity;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class Account extends BaseEntity<String> {

  @Id
  private transient String id;

  @Size(min = 3, max = 50)
  @Indexed(unique = true)
  @NotEmpty
  private String username;

  @NotEmpty
  private String password;

  private boolean enabled = false;

  private String code;

  private boolean isCustomer;

  private boolean expired = false;

  private boolean locked = false;

  private Set<String> roles;

  @NotEmpty
  @Size(min = 13, max = 50)
  @Indexed(unique = true)
  private String email;

  private boolean credentialsExpired = false;

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the enabled
   */
  public boolean getEnabled() {
    return enabled;
  }

  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * @param code the code to set
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return the isCustomer
   */
  public boolean isCustomer() {
    return isCustomer;
  }

  /**
   * @param isCustomer the isCustomer to set
   */
  public void setCustomer(boolean isCustomer) {
    this.isCustomer = isCustomer;
  }

  /**
   * @return the credentialsExpired
   */
  public boolean getCredentialsExpired() {
    return credentialsExpired;
  }

  /**
   * @param credentialsExpired the credentialsExpired to set
   */
  public void setCredentialsExpired(boolean credentialsExpired) {
    this.credentialsExpired = credentialsExpired;
  }

  /**
   * @return the expired
   */
  public boolean getExpired() {
    return expired;
  }

  /**
   * @param expired the expired to set
   */
  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  /**
   * @return the locked
   */
  public boolean getLocked() {
    return locked;
  }

  /**
   * @param locked the locked to set
   */
  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  /**
   * @return the roles
   */
  public Set<String> getRoles() {
    return roles;
  }

  /**
   * @param roles the roles to set
   */
  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }
}

package com.sodo.xmarketing.auth;

import com.sodo.xmarketing.model.account.Account;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.repository.CustomerRepository;
import com.sodo.xmarketing.repository.employee.EmployeeRepository;
import com.sodo.xmarketing.service.RoleService;
import com.sodo.xmarketing.utils.Constant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.stereotype.Service;


/**
 * Created by tahi1990 on 27/06/2017.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  boolean customer;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private EmployeeRepository employeeRepository;
  
  @Autowired
  private RoleService roleService;

  /**
   * @return the customer
   */
  public boolean isCustomer() {
    return customer;
  }

  /**
   * @param customer the customer to set
   */
  public void setCustomer(boolean customer) {
    this.customer = customer;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();

    String clientId = "";
    if (a != null && a.getPrincipal() instanceof User) {
      clientId = ((User) a.getPrincipal()).getUsername();
    } else {
      if (isCustomer()) {
        clientId = "customer";
      } else {
        clientId = "employee";
      }
    }

    CustomUser customUser = null;
    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

    if (Constant.CUSTOMER_APP.equals(clientId)) {
      Customer customer = customerRepository.findByUsernameOrEmailIgnoreCase(username);
      

      if (customer == null) {
        // Not found...
        throw new UsernameNotFoundException("User or email " + username + " not found.");
      }
//      if (customer.getExpired()) {
//          throw new UserDeniedAuthorizationException("Tài khoản đang bị khóa");
//        }
      if(customer.getRoles() != null)
    	  grantedAuthorities = customer.getRoles().stream()
    		  .map(role-> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());
      customUser =
          new CustomUser(customer.getUsername(), customer.getCode(), customer.getEmail(),
              customer.getFormat().getLang(),
              customer.getRoles(), customer.getPassword(), customer.getEnabled(),
              !customer.getExpired(),
              !customer.getCredentialsExpired(), !customer.getLocked(), grantedAuthorities, customer.getName());
    } else if (Constant.EMPLOYEE_APP.equals(clientId)) {
      Employee employee = employeeRepository.findByUsername(username);

      if (employee == null) {
        // Not found...
        throw new UsernameNotFoundException("User or email " + username + " not found.");
      }
//      if (employee.getExpired()) {
//    	  throw new InvalidGrantException("");
//        }
      if(employee.getRoles() != null)
      {
    	 
    	  Set<String> roles = roleService.getPermissionsByRole(employee.getRoles());
    	  grantedAuthorities = roles.stream()
    			  .map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
      }
      customUser =
          new CustomUser(employee.getUsername(), employee.getCode(), employee.getEmail(), null,
              employee.getRoles(), employee.getPassword(), employee.getEnabled(),
              !employee.getExpired(),
              !employee.getCredentialsExpired(), !employee.getLocked(), grantedAuthorities, employee.getName());
    }

    return customUser;
  }

  static final class UserRepositoryUserDetails extends Account implements UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return getRoles().stream()
    		  .map(role-> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
      return !getExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
      return !getLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return !getCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
      return !getEnabled();
    }
  }

  /**
   * Created by tuanhiep225 on 24/7/2017.
   */

  static final class CustomUser extends User {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String email;
    private transient Set<String> roles;
    private String code;
    private String culture;
    private String name;

    public CustomUser(String username, String code, String email, String culture, Set<String> roles,
        String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
        boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String name) {
      super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
          authorities);
      this.email = email;
      this.roles = roles;
      this.code = code;
      this.culture = culture;
      this.name =name;
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


    public String getCulture() {
      return culture;
    }

    public void setCulture(String culture) {
      this.culture = culture;
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

    /**
     * @return the customerCode
     */
    public String getCode() {
      return code;
    }

    /**
     * @param code the customerCode to set
     */
    public void setCode(String code) {
      this.code = code;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

  }
}

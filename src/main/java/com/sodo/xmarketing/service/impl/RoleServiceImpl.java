package com.sodo.xmarketing.service.impl;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.sodo.xmarketing.model.account.Role;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.repository.CustomerRepository;
import com.sodo.xmarketing.repository.RoleRepository;
import com.sodo.xmarketing.repository.employee.EmployeeRepository;
import com.sodo.xmarketing.service.RoleService;
import org.apache.commons.lang3.StringUtils;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Override
  public Role createNewRole(Role role) {
    StringBuilder codeBuilder = new StringBuilder("ROLE_");
    String code = StringUtils.stripAccents(role.getName()).replaceAll("\\s", "");

    codeBuilder.append(code.toUpperCase());
    role.setCode(codeBuilder.toString());

    return roleRepository.add(role);
  }

  @Override
  public List<Role> getRoles() {
    return (List<Role>) roleRepository.getAll();
  }

  @Override
  public Role findById(String id) {
    return roleRepository.get(id);
  }

  @Override
  public void delete(String id) {
    roleRepository.remove(id);
  }

  @Override
  public Role updateRole(Role role) {
    return roleRepository.update(role);
  }

  @Override
  public Set<String> getPermissionsByRole(Set<String> roles) {

    List<Role> foundRoles = roleRepository.findByCodes(roles);

    return foundRoles.stream().flatMap(role -> role.getPermissions().stream())
        .collect(Collectors.toSet());
  }

  @Override
  public boolean checkRoleExist(Role role) {

    List<Customer> customers = customerRepository.findByRole (role.getCode());

    List<Employee> employees = employeeRepository.findByRole(role.getCode());

    return (!customers.isEmpty() || !employees.isEmpty());
  }

  @Override
  public boolean isRoleExist(String code) {

    Role role = roleRepository.findByCode(code);

    if (role == null) {
      return false;
    } else {
      return true;
    }
  }

}

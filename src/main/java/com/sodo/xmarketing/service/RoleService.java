package com.sodo.xmarketing.service;

import java.util.List;
import java.util.Set;

import com.sodo.xmarketing.model.account.Role;

public interface RoleService {

  Role createNewRole(Role role);

  List<Role> getRoles();

  Role findById(String id);

  Role updateRole(Role role);

  void delete(String id);

  Set<String> getPermissionsByRole(Set<String> roles);

  boolean checkRoleExist(Role role);

  boolean isRoleExist(String code);

}

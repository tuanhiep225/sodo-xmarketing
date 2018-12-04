package com.sodo.xmarketing.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.account.Role;
import com.sodo.xmarketing.service.RoleService;


@RestController
@RequestMapping(value = "/api/role")
public class RoleController {

  @Autowired
  private RoleService roleService;

  @Autowired
  private CurrentUserService currentUserService;

  @RequestMapping(value = "/all", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Role>> getAll() {
    return new ResponseEntity<>(roleService.getRoles(), HttpStatus.OK);
  }

  @RequestMapping(value = "/get/{id}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Role> findById(@PathVariable("id") String id) {
    return new ResponseEntity<>(roleService.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/add", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Role> add(@Valid @RequestBody Role role, BindingResult errors)
      throws SodException {

    if (errors.hasErrors()) {
      throw new SodException(errors.getAllErrors().get(0).getDefaultMessage(),
          errors.getAllErrors().get(0).getCode());
    }

    Role createdRole = roleService.createNewRole(role);
    return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Boolean> delete(@PathVariable("id") String id) throws SodException {

    Role role = roleService.findById(id);

    if (roleService.checkRoleExist(role)) {
      throw new SodException("Cannot delete role", "DELETE");
    }

    roleService.delete(id);
    return new ResponseEntity<>(true, HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Role> update(@Valid @RequestBody Role role, BindingResult errors)
      throws SodException {

    if (errors.hasErrors()) {
      throw new SodException(errors.getAllErrors().get(0).getDefaultMessage(),
          errors.getAllErrors().get(0).getCode());
    }

    Role updatedRole = roleService.updateRole(role);

    return new ResponseEntity<>(updatedRole, HttpStatus.OK);
  }

  @RequestMapping(value = "/get-permissions", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Set<String>> getByCodes(@RequestParam("roles") Set<String> roles) {

    Set<String> permissions = roleService.getPermissionsByRole(roles);
    CurrentUser currentUser = currentUserService.getCurrentUser();
    return new ResponseEntity<>(permissions, HttpStatus.OK);
  }
}

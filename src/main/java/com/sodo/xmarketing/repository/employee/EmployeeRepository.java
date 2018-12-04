package com.sodo.xmarketing.repository.employee;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.dto.EmployeeSearch;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.repository.BaseRepository;

@Repository
public interface EmployeeRepository extends BaseRepository<Employee, String> , EmployeeCustomRepository{

  @Query("{'username': {$regex : '^?0$', $options: 'i'}}")
  Employee findByUsername(String value);


  @Query(value = "{ 'isDelete' : false}", count = true)
  Long countAllEmployees();

  // Khi tieu chi tim kiem co trang thai
  @Query(
      value = "{ '$and' : [{ 'isDelete' : false}, {'agency.code': { $regex: ?0 }}, {'name': { $regex: ?1 }},{'roles.code':{$regex:?2}},{'credentialsExpired':?3}]}")
  Page<Employee> filtering(String agency, String name, String rolecode, Boolean status,
      Pageable pageable);

  @Query(
      value = "{ '$and' : [{ 'isDelete' : false}, {'agency': { $regex: ?0 }}, {'name': { $regex: ?1 }},{'roles.code':{$regex:?2}},{'credentialsExpired':?3}]}",
      count = true)
  Long countWhenFiltering(String agency, String name, String rolecode, Boolean status);

  // Khi tieu chi tim kiem khong co trang thai
  @Query(
      value = "{ '$and' : [{ 'isDelete' : false}, {'agency': { $regex: ?0 }}, {'name': { $regex: ?1 }},{'roles.code':{$regex:?2}}]}")
  Page<Employee> filtering(String agency, String name, String rolecode, Pageable pageable);

  @Query(
      value = "{ '$and' : [{ 'isDelete' : false}, {'agency': { $regex: ?0 }}, {'name': { $regex: ?1 }},{'roles.code':{$regex:?2}}]}",
      count = true)
  Long countWhenFiltering(String agency, String name, String rolecode);

  @Query("{'roles' : ?0 }")
  List<Employee> findByRole(String role);

  void removeByUsername(String username);

  @Query("{'$or':[{'name':{'$regex' : '?0'}},{'email':{'$regex' : '?0'}},{'username':{'$regex' : '?0'}}],'isDelete':false}")
  Collection<Employee> findByRegex(String name);


  @ExistsQuery("{'username': {$regex : '^?0$', $options: 'i'},'isDelete':false}")
  boolean existsByUsername(String userName);

  @ExistsQuery("{'email': {$regex : '^?0$', $options: 'i'},'isDelete':false}")
  boolean existsByEmail(String userName);


}

package com.sodo.xmarketing.repository;

import com.sodo.xmarketing.dto.CustomerSearch;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends BaseRepository<Customer, String> , CustomerCustomRepository{

  @ExistsQuery("{'username': {$regex : '^?0$', $options: 'i'},'isDelete':false}")
  boolean existsByUsername(String userName);

  @ExistsQuery("{'email': {$regex : '^?0$', $options: 'i'},'isDelete':false}")
  boolean existsByEmail(String userName);

  Customer findOneByCodeIgnoreCaseAndIsDelete(String orderCode, boolean isDelete);

  @Query("{'username': {$regex : '^?0$', $options: 'i'},'isDelete':false}")
  Customer findByUsername(String username);

  @Query("{'username': {$regex : '^?0$', $options: 'i'}}")
  Customer findByUsernameIgnoreIsDelete(String username);

  @Query("{'email': {$regex : '^?0$', $options: 'i'},'isDelete':false}")
  Customer findByEmail(String email);

  @Query("{'$or':[{'username':{$regex : '^?0$', $options: 'i'}},{'email':{$regex : '^?0$', $options: 'i'}}],'isDelete':false}")
  Customer findByUsernameOrEmailIgnoreCase(String value);
  
  @Query("{'roles' : ?0 }")
  List<Customer> findByRole(String role);

}

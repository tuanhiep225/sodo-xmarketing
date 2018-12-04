package com.sodo.xmarketing.repository;


import java.util.List;
import java.util.Set;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.account.Role;

@Repository
public interface RoleRepository extends BaseRepository<Role, String> {

  @Query(value = "{ 'code' : { '$in' : ?0 } }")
  List<Role> findByCodes(Set<String> roles);

  @Query(value = "{ 'code' : ?0 }")
  Role findByCode(String code);

}

package com.sodo.xmarketing.repository;

import java.util.List;
import com.sodo.xmarketing.model.Categories;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends BaseRepository<Categories, String> {

    @Query(value = "{'code': ?0}")
    Categories getCategoriesByCode(String code);

    @Query(value = "{'name': ?0} ")
    Categories getCategoriesByName(String name);

    @Query(value = "{'parent': ?0} ")
    List<Categories> getCategoriesByParent(String parent);

}

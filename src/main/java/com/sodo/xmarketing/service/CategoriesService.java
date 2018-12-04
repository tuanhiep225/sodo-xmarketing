package com.sodo.xmarketing.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Categories;
import com.sodo.xmarketing.model.CategoriesTree;
import com.sodo.xmarketing.model.common.SodResult;

public interface CategoriesService {
    Categories createCategory(Categories categories) throws SodException;

    List<Categories> getCategoriesByParent(String parent);

    List<CategoriesTree> getCategoriesTreeByParent(String parent);

    Categories getCategoriesByCode(String code);

    CategoriesTree getCategoriesTreeWithPathByParent(String parent);

    SodResult<Boolean> updateNameCategories(Categories categories);

    Categories getCategoriesByName(String name);

    Map<String, String> getMapCategoriesByParent(String parent);

    public void initData() throws IOException;
}

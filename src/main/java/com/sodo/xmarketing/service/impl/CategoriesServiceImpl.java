package com.sodo.xmarketing.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Categories;
import com.sodo.xmarketing.model.CategoriesTree;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.repository.CategoriesRepository;
import com.sodo.xmarketing.service.CategoriesService;
import com.sodo.xmarketing.service.InitDataHelper;
import com.sodo.xmarketing.utils.ConfigHelper;
import com.sodo.xmarketing.utils.DateUtils;
import com.sodo.xmarketing.utils.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    private InitDataHelper initDataHelper;

    @Autowired
    private ConfigHelper configHelper;

    @Autowired
    public CategoriesServiceImpl(Properties properties, MongoTemplate mongoTemplate,
            ConfigHelper configHelper) {
        initDataHelper = new InitDataHelper(properties, mongoTemplate, configHelper);

    }

    @Override
    public Categories createCategory(Categories categories) throws SodException {
        if (categoriesRepository.getCategoriesByName(categories.getName()) != null) {
            throw new SodException("Tên danh mục bị trùng", "DUPLICATE");
        }

        if (categories.getParent() == null) {
            categories.setPath(",");
        } else {
            Categories parent = categoriesRepository.getCategoriesByCode(categories.getParent());
            categories.setPath(parent.getPath() + parent.getCode() + ",");
        }

        return categoriesRepository.save(categories);
    }

    @Override
    public List<Categories> getCategoriesByParent(String parent) {
        return categoriesRepository.getCategoriesByParent(parent);
    }

    @Override
    public List<CategoriesTree> getCategoriesTreeByParent(String parent) {
        return setValueCode(parent);
    }

    // Đệ quy lấy dữ liệu tree
    private List<CategoriesTree> setValueCode(String parent) {

        List<Categories> categoriesList = categoriesRepository.getCategoriesByParent(parent);

        List<CategoriesTree> categoriesTrees = new ArrayList<>();

        if (categoriesList == null || categoriesList.isEmpty()) {
            return new ArrayList<>();
        }

        categoriesList.forEach(category -> {
            CategoriesTree tree = new CategoriesTree();
            tree.setText(category.getName());
            tree.setValue(category.getCode());
            tree.setChildren(setValueCode(category.getCode()));
            categoriesTrees.add(tree);
        });

        return categoriesTrees;
    }

    @Override
    public Categories getCategoriesByCode(String code) {
        if (DateUtils.isNullOrEmpty(code)) {
            return new Categories();
        }
        return categoriesRepository.getCategoriesByCode(code);
    }

    @Override
    public CategoriesTree getCategoriesTreeWithPathByParent(String parent) {
        CategoriesTree categoriesTree = new CategoriesTree();

        Categories rootCategories = categoriesRepository.getCategoriesByCode(parent);

        categoriesTree.setText(rootCategories.getName());
        categoriesTree.setValue(rootCategories.getPath() + rootCategories.getCode() + ",");
        categoriesTree.setChildren(setValuePath(parent));
        return categoriesTree;
    }

    private List<CategoriesTree> setValuePath(String parent) {

        List<Categories> categoriesList = categoriesRepository.getCategoriesByParent(parent);

        List<CategoriesTree> categoriesTrees = new ArrayList<>();

        if (categoriesList == null || categoriesList.isEmpty()) {
            return new ArrayList<>();
        }

        categoriesList.forEach(category -> {
            CategoriesTree tree = new CategoriesTree();
            tree.setText(category.getName());
            tree.setValue(category.getPath() + category.getCode() + ",");
            tree.setChildren(setValuePath(category.getCode()));
            categoriesTrees.add(tree);
        });
        return categoriesTrees;
    }

    @Override
    public SodResult<Boolean> updateNameCategories(Categories categories) {
        SodResult<Boolean> result = new SodResult<>();

        Categories currentCategories =
                categoriesRepository.getCategoriesByCode(categories.getCode());

        if (currentCategories == null) {
            result.setError(true);
            result.setCode("ERROR_EXISTS");
            result.setMessage("Bản ghi không tồn tại");
            return result;
        }

        if (currentCategories.getParent() == null || categories.getParent() == null
                || !currentCategories.getParent().equals(categories.getParent())
                || !currentCategories.getCode().equals(categories.getCode())) {
            result.setError(true);
            result.setCode("ERROR_DATA");
            result.setMessage("Dữ liệu bản ghi không hợp lệ");
            return result;
        }

        currentCategories.setName(categories.getName());
        categoriesRepository.save(currentCategories);

        result.setError(false);
        result.setCode("success");
        result.setMessage("Thành công");
        return result;
    }

    @Override
    public Categories getCategoriesByName(String name) {
        return categoriesRepository.getCategoriesByName(name);
    }

    @Override
    public Map<String, String> getMapCategoriesByParent(String parent) {
        List<Categories> list = categoriesRepository.getCategoriesByParent(parent);
        Map<String, String> result = new HashMap<>();
        list.forEach(category -> result.put(category.getCode(), category.getName()));
        return result;
    }

    @Override
    public void initData() throws IOException {
        initDataHelper.initCategories();
    }

}

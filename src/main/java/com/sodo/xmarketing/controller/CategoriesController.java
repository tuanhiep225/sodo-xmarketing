package com.sodo.xmarketing.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sodo.xmarketing.constants.Constants.FundCategories;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Categories;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.service.CategoriesService;
import com.sodo.xmarketing.service.impl.NextSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "v1/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private NextSequenceService nextSequenceService;

    /**
     * @author Ha
     * @throws SodException
     */
    @ApiOperation(value = "Tạo danh mục")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<SodResult<Categories>> create(@RequestBody Categories categories)
            throws SodException {
        SodResult<Categories> result = new SodResult<>();

        if (categories.getCode() == null) {
            categories.setCode(nextSequenceService.genCategories());
        }

        Categories createdCategories = categoriesService.createCategory(categories);


        if (createdCategories == null) {
            result.setError(true);
            result.setCode("error_create");
            result.setMessage("Không tạo được danh mucj");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        result.setError(false);
        result.setCode("create_success");
        result.setMessage("tạo thành công danh mục");
        result.setResult(createdCategories);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * @author Ha
     * @throws SodException
     */
    @ApiOperation(value = "Tạo danh mục nhóm quỹ chính")
    @RequestMapping(value = "createPrimaryFundGroup", method = RequestMethod.POST)
    public ResponseEntity<SodResult<Categories>> createPrimaryFundGroup(
            @RequestBody Categories categories) throws SodException {
        SodResult<Categories> result = new SodResult<>();

        if (categories.getCode() == null) {
            categories.setCode(nextSequenceService.genCategories());
        }

        categories.setParent(FundCategories.FUND_GROUP);

        Categories createdCategories = categoriesService.createCategory(categories);

        if (createdCategories == null) {
            result.setError(true);
            result.setCode("error_create");
            result.setMessage("Không tạo được danh mục");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        result.setError(false);
        result.setCode("create_success");
        result.setMessage("tạo thành công danh mục");
        result.setResult(createdCategories);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * @author Ha
     */
    @ApiOperation(value = "Lấy danh mục theo parent")
    @RequestMapping(value = "getCategoriesByParent", method = RequestMethod.GET)
    public ResponseEntity<List<Categories>> getCategoriesByParent(
            @RequestParam("parent") String parent) {
        List<Categories> categories = categoriesService.getCategoriesByParent(parent);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * @author Ha
     * @throws SodException
     */
    @ApiOperation(value = "Tạo danh mục chi nhánh quỹ chính")
    @RequestMapping(value = "createPrimaryFundBranchSystem", method = RequestMethod.POST)
    public ResponseEntity<SodResult<Categories>> createPrimaryFundBranchSystem(
            @RequestBody Categories categories) throws SodException {
        SodResult<Categories> result = new SodResult<>();

        if (categories.getCode() == null) {
            categories.setCode(nextSequenceService.genCategories());
        }

        categories.setParent(FundCategories.FUND_BRANCH_SYSTEM);

        Categories createdCategories = categoriesService.createCategory(categories);

        if (createdCategories == null) {
            result.setError(true);
            result.setCode("error_create");
            result.setMessage("Không tạo được danh mục");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        result.setError(false);
        result.setCode("create_success");
        result.setMessage("tạo thành công danh mục");
        result.setResult(createdCategories);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * @author Ha
     */
    @ApiOperation(value = "Lấy Map(code,name) của danh mục chi nhánh quỹ")
    @RequestMapping(value = "getMapFundBranchSystem", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getMapFundBranchSystem() {
        List<Categories> list =
                categoriesService.getCategoriesByParent(FundCategories.FUND_BRANCH_SYSTEM);
        Map<String, String> result = new HashMap<>();
        list.forEach(category -> result.put(category.getCode(), category.getName()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * @author Ha
     */
    @ApiOperation(value = "Lấy Map(code,name) của danh mục nhóm quỹ")
    @RequestMapping(value = "getMapFundGroup", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getMapFundGroup() {
        return new ResponseEntity<>(
                categoriesService.getMapCategoriesByParent(FundCategories.FUND_GROUP),
                HttpStatus.OK);
    }

    /**
     * @author Ha
     */
    @ApiOperation(value = "Lấy Danh mục theo mã")
    @RequestMapping(value = "getCategoriesByCode", method = RequestMethod.GET)
    public ResponseEntity<Categories> getCategoriesByCode(
            @RequestParam(value = "code", required = false) String code) {
        return new ResponseEntity<>(categoriesService.getCategoriesByCode(code), HttpStatus.OK);
    }

    /**
     * @author Ha
     * @throws SodException
     */
    @ApiOperation(value = "Sửa tên danh mục")
    @RequestMapping(value = "updateNameCategories", method = RequestMethod.PUT)
    public ResponseEntity<SodResult<Boolean>> updateNameCategories(
            @RequestBody Categories categories) throws SodException {
        if (categoriesService.getCategoriesByName(categories.getName()) != null) {
            throw new SodException("Tên danh mục bị trùng", "DUPLICATE");
        }
        SodResult<Boolean> result = categoriesService.updateNameCategories(categories);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/init-data")
    public Boolean initData() throws IOException {
        categoriesService.initData();
        return true;
    }

}

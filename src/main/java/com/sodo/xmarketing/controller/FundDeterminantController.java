/**
 *
 */
package com.sodo.xmarketing.controller;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.fund.FundDeterminant;
import com.sodo.xmarketing.service.FundDeterminantService;
import com.sodo.xmarketing.utils.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ANH MINH - PC
 */
@RestController
@RequestMapping(value = "/api/fund-determinant")
public class FundDeterminantController {

  @Autowired
  FundDeterminantService fundDeterminantService;

  @Autowired
  Properties properties;
  @Autowired
  CurrentUserService currentUserService;

  /**
   *
   * @param page
   * @param size
   * @param query
   * @param sort
   * @param direction
   * @return
   * @throws SodException
   */
  @RequestMapping(value = "/filter", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> getFundsFilter(@RequestParam("page") int page,
      @RequestParam("size") int size, @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sort,
      @RequestParam(value = "direction", required = false) String direction) throws SodException {
    // Pageable and Sort
    PageRequest request ;
    if (sort != null) {
      if (direction.equals("asc")) {
        request = PageRequest.of(page, size, new Sort(Sort.Direction.ASC, sort));
      } else if (direction.equals("desc")) {
        request = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, sort));
      }
    } else {
      request = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "lastModifiedDate"));
    }
    request =  PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "lastModifiedDate"));
    Gson gson = new Gson();
    JsonObject queryParam = gson.fromJson(query, JsonObject.class);

    String name = queryParam.get("name").getAsString();
    String parent = queryParam.get("parent").getAsString();
    String type = queryParam.get("type").getAsString();
    String statusParam = queryParam.get("status").getAsString();
    Boolean absoluteParent = queryParam.get("parentAbsolute").getAsBoolean();
    Boolean status;
    if (statusParam.equals("")) {
      status = null;
    } else {
      status = Boolean.parseBoolean(statusParam);
    }

    Map<String, Object> funds =
        fundDeterminantService.getFundByFilter(absoluteParent, name, parent, type, status, request);
    return new ResponseEntity<>(funds, HttpStatus.OK);
  }


  @RequestMapping(value = "/findByCode/{code}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FundDeterminant> findByCode(@PathVariable("code") String code)
      throws SodException {
    FundDeterminant fundFind = fundDeterminantService.findByCode(code);
    if (fundFind == null) {
      throw new SodException("Fund determinant not found", "FIND BY CODE");
    }

    return new ResponseEntity<>(fundFind, HttpStatus.OK);
  }

  @RequestMapping(value = "/suggest-fund-deter", method = RequestMethod.GET)
  public ResponseEntity<List<FundDeterminant>> suggestFund(
      @RequestParam(value = "numberRecord", required = false) int numberRecord,
      @RequestParam(value = "query", required = false) String query) throws SodException {
    List<FundDeterminant> funds =
        fundDeterminantService.suggestFundDeter(query.toLowerCase(), numberRecord);

    return new ResponseEntity<>(funds, HttpStatus.OK);
  }

  @RequestMapping(value = "/get-determinal-tree", method = RequestMethod.GET)
  public ResponseEntity<List<FundDeterminant>> getDeterminalTree(
      @RequestParam(value = "maxLevel", required = false) int maxLevel,
      @RequestParam(value = "containSystem", required = false) boolean containSystem)
      throws SodException {
    List<FundDeterminant> funds = fundDeterminantService.getDeterminalTree(maxLevel, containSystem);

    return new ResponseEntity<>(funds, HttpStatus.OK);
  }



  @RequestMapping(value = "/get-names-from-codes", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, FundDeterminant>> getNamesfromCode(
      @RequestBody Map<String, List<String>> data) throws SodException {
    List<String> codes = data.get("data");
    Map<String, FundDeterminant> fundDeterminals =
        fundDeterminantService.getFundDeterminantsFromCodes(codes);

    return new ResponseEntity<>(fundDeterminals, HttpStatus.OK);
  }


  @RequestMapping(value = "/tree-deter", method = RequestMethod.GET)
  public ResponseEntity<List<FundDeterminant>> getListTreeDeter(
      @RequestParam(value = "treeCode", required = false) List<String> treeCodes) {

    return new ResponseEntity<>(fundDeterminantService.getListTreeDeter(treeCodes), HttpStatus.OK);
  }
  
  @GetMapping("/init-data")
  public Boolean initData() throws SodException {
	  fundDeterminantService.initData(currentUserService.getCurrentUser());
//	  fundDeterminantService.initAccountEntryContent();
	  return true;
  }
}

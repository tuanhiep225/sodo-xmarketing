/**
 * 
 */
package com.sodo.xmarketing.model.account;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser {
	 private String code;
	  private String email;
	  private String userName;
	  private String fullName;
	  private boolean isCustomer;
	  private String unit;
	  private String accessToken;
	  private String culture;
	  private String timeZone;

	  // Mã tenent của tài khoản.
	  private String system ;
	  // Chứa thông tin về loại tanent
	  private String systemType;
	  // Chứa mã tanent của systemType = CUSTOMER
	  private String systemOwner;

	  private String idPath;
	  private Boolean isManager;
	  private String position;
	  private String unitType;
	  private List<String> roles;

	  public String getSystem() {
	    return system;
	  }

	  public void setSystem(String system) {
	    this.system = system;
	  }

	  public String getCode() {
	    return code;
	  }

	  public void setCode(String code) {
	    this.code = code;
	  }

	  public String getEmail() {
	    return email;
	  }

	  public void setEmail(String email) {
	    this.email = email;
	  }

	  public String getUserName() {
	    return userName;
	  }

	  public void setUserName(String userName) {
	    this.userName = userName;
	  }

	  public String getFullName() {
	    return fullName;
	  }

	  public void setFullName(String fullName) {
	    this.fullName = fullName;
	  }

	  public boolean isCustomer() {
	    return isCustomer;
	  }

	  public void setCustomer(boolean customer) {
	    isCustomer = customer;
	  }

	  /**
	   * @return the unit
	   */
	  public String getUnit() {
	    return unit;
	  }

	  /**
	   * @param unit the unit to set
	   */
	  public void setUnit(String unit) {
	    this.unit = unit;
	  }

	  public String getAccessToken() {
	    return accessToken;
	  }

	  public void setAccessToken(String accessToken) {
	    this.accessToken = accessToken;
	  }

	  public String getCulture() {
	    return culture;
	  }

	  public void setCulture(String culture) {
	    this.culture = culture;
	  }

	  public String getTimeZone() {
	    return timeZone;
	  }

	  public void setTimeZone(String timeZone) {
	    this.timeZone = timeZone;
	  }

	  /**
	   * @return the idPath
	   */
	  public String getIdPath() {
	    return idPath;
	  }

	  /**
	   * @param idPath the idPath to set
	   */
	  public void setIdPath(String idPath) {
	    this.idPath = idPath;
	  }

	  /**
	   * @return the isManager
	   */
	  public Boolean getIsManager() {
	    return isManager;
	  }

	  /**
	   * @param isManager the isManager to set
	   */
	  public void setIsManager(Boolean isManager) {
	    this.isManager = isManager;
	  }

	  /**
	   * @return the position
	   */
	  public String getPosition() {
	    return position;
	  }

	  /**
	   * @param position the position to set
	   */
	  public void setPosition(String position) {
	    this.position = position;
	  }

	  /**
	   * @return the unitType
	   */
	  public String getUnitType() {
	    return unitType;
	  }

	  /**
	   * @param unitType the unitType to set
	   */
	  public void setUnitType(String unitType) {
	    this.unitType = unitType;
	  }

	  public String getSystemType() {
	    return systemType;
	  }

	  public void setSystemType(String systemType) {
	    this.systemType = systemType;
	  }

	  public String getSystemOwner() {
	    return systemOwner;
	  }

	  public void setSystemOwner(String systemOwner) {
	    this.systemOwner = systemOwner;
	  }

	  public List<String> getRoles() {
	    return roles;
	  }

	  public void setRoles(List<String> roles) {
	    this.roles = roles;
	  }
}

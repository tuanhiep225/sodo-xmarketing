/**
 * 
 */
package com.sodo.xmarketing.utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tuanhiep225
 *
 */

@Component
@ConfigurationProperties("sodo")
public class Properties {

	  private String url;
	  private String account;
	  private String pass;
	  private String method;
	  private String urlV2;
	  private String pathToSave;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrlV2() {
		return urlV2;
	}

	public void setUrlV2(String urlV2) {
		this.urlV2 = urlV2;
	}

	public String getPathToSave() {
		return pathToSave;
	}

	public void setPathToSave(String pathToSave) {
		this.pathToSave = pathToSave;
	}
	  
	  
}

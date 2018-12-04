/**
 *
 */
package com.sodo.xmarketing.auth;

import com.sodo.xmarketing.model.account.CurrentUser;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author tuanhiep225
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentUserService {

	private CurrentUser currentUser;
	private HttpServletRequest httpServletRequest;

	@Autowired
	public CurrentUserService(HttpServletRequest httpServletRequest) {

		this.httpServletRequest = httpServletRequest;
	}

	public CurrentUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}

		if (currentUser != null) {
			return currentUser;
		}

		// String clientId = ((OAuth2Authentication)
		// authentication).getOAuth2Request().getClientId();
		Map<String, Object> additionalInfo = SodFilterSecurityInterceptor.additionalInfo.get();
		currentUser = new CurrentUser();

		currentUser.setCustomer((boolean) additionalInfo.get("isCustomer"));
		currentUser.setCode((String) additionalInfo.get("code"));
		currentUser.setEmail((String) additionalInfo.get("email"));
		currentUser.setUserName((String) additionalInfo.get("username"));
		currentUser.setFullName((String) additionalInfo.get("name"));
		currentUser.setCulture((String) additionalInfo.get("culture"));

		// identify data permission

		if (additionalInfo.containsKey("roles")) {
			currentUser.setRoles((List<String>) additionalInfo.get("roles"));
		}

		// Fix currentUser
		currentUser.setAccessToken(getToken());
		currentUser.setTimeZone(getTimeZone());

		return currentUser;
	}

	public String getToken() {
		return httpServletRequest.getHeader("Authorization");
	}

	public String getTimeZone() {

		return httpServletRequest.getHeader("TimeZone") != null ? httpServletRequest.getHeader("TimeZone") : "0";
	}

	public String getCulture() {
		return httpServletRequest.getHeader("Culture");
	}

}

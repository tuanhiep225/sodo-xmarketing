/**
 * 
 */
package com.sodo.xmarketing.controller;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.support.FAQ;
import com.sodo.xmarketing.service.FaqService;

/**
 * @author tuanhiep225
 *
 */

@RestController
@RequestMapping("/api/support")
public class SupportController {

	private static final Log LOGGER = LogFactory.getLog(SupportController.class);
	
	@Autowired
	private FaqService  faqService;
	
	@Autowired
	private CurrentUserService currenUserService;
	
	@GetMapping("")
	public Collection<FAQ> get() {
		return faqService.getAll();
	}
	
	@PostMapping("")
	public SodResult<Collection<FAQ>> multilCreate(@RequestBody Collection<FAQ> entities) {
		CurrentUser currentUser = currenUserService.getCurrentUser();
		if(currentUser == null)
			return SodResult.<Collection<FAQ>>builder().isError(true).code("CUSTOMER_INVALID").message("Customer invalid !").build();
		return faqService.create(entities, currentUser);
	}
}

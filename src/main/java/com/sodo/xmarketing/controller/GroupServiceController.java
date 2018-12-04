/**
 * 
 */
package com.sodo.xmarketing.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sodo.xmarketing.model.GroupService;
import com.sodo.xmarketing.service.GroupServiceService;

/**
 * @author tuanhiep225
 *
 */
@RestController
@RequestMapping("/api/group-service")
public class GroupServiceController {
	
	@Autowired
	private GroupServiceService groupServiceImpl;
	
	@PostMapping("")
	public GroupService create(@RequestBody GroupService entity) {
		return groupServiceImpl.create(entity);
		
	}
	
	@GetMapping("")
	public Collection<GroupService> gets() {
		return groupServiceImpl.getAll();
		
	}
	
	@PostMapping("/collection")
	public Collection<GroupService> multiCreate(@RequestBody Collection<GroupService> entity) {
		return groupServiceImpl.create(entity);
		
	}
}

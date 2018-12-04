/**
 * 
 */
package com.sodo.xmarketing.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.model.GroupService;
import com.sodo.xmarketing.repository.customer.GroupServiceRepository;
import com.sodo.xmarketing.service.GroupServiceService;

/**
 * @author tuanhiep225
 *
 */
@Service
public class GroupServiceServiceImpl implements GroupServiceService {
	
	
	@Autowired
	private GroupServiceRepository groupRepository;

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.GroupServiceService#create(com.sodo.xmarketing.model.GroupService)
	 */
	@Override
	public GroupService create(GroupService block) {
		return groupRepository.add(block);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.GroupServiceService#getAll()
	 */
	@Override
	public Collection<GroupService> getAll() {
		return groupRepository.getAll();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.GroupServiceService#getByCode(java.lang.String)
	 */
	@Override
	public GroupService getByCode(String code) {
		return groupRepository.getByCode(code);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.GroupServiceService#create(java.util.Collection)
	 */
	@Override
	public Collection<GroupService> create(Collection<GroupService> entities) {
		// TODO Auto-generated method stub
		return groupRepository.insert(entities);
	}

}

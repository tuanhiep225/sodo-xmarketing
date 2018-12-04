/**
 * 
 */
package com.sodo.xmarketing.service;

import java.util.Collection;
import com.sodo.xmarketing.model.GroupService;

/**
 * @author tuanhiep225
 *
 */
public interface GroupServiceService {
	public GroupService create(GroupService block);
	public Collection<GroupService> getAll();
	public GroupService getByCode(String code);
	/**
	 * @param entity
	 * @return
	 */
	public Collection<GroupService> create(Collection<GroupService> entity);
}

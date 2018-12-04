/**
 * 
 */
package com.sodo.xmarketing.model.account;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Action {

	private static final Log LOGGER = LogFactory.getLog(Action.class);
	
	  private String url;
	  private String method;
}

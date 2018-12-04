/**
 * 
 */
package com.sodo.xmarketing.api.model;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderLivestreamFacebook {

	private String account_user;
	private String account_pass;
	private String method;
	private String video_id;
	private Integer view;
	private Integer minute;
}

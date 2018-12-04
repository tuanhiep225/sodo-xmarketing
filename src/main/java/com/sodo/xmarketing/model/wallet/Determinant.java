/**
 * 
 */
package com.sodo.xmarketing.model.wallet;
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
public class Determinant {

	private String code;
	
	private String name;
	
	private String treeCode;
}

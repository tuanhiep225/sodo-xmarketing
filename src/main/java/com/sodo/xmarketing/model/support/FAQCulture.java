/**
 * 
 */
package com.sodo.xmarketing.model.support;

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
public class FAQCulture {
	public String answer;
	public String question;
}

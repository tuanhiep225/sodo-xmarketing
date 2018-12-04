/**
 * 
 */
package com.sodo.xmarketing.dto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sodo.xmarketing.dto.FundSearchDTO.FundSearchDTOBuilder;

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
public class FundInfo {
	
    private String code;
    private String name;
    private String content;
}

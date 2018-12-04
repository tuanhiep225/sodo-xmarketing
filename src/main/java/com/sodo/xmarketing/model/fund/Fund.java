/**
 * 
 */
package com.sodo.xmarketing.model.fund;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import com.querydsl.core.annotations.QueryEntity;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.bank.BankAccount;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.entity.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
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
@QueryEntity
@Document(collection = "fund")
public class Fund extends BaseEntity<String> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private transient String id;

	private String code;

	@NotEmpty(message = "type is not empty")
	private String type;

	@NotEmpty(message = "fund name is not empty")
	@Size(min = 5, max = 100, message = "account character length from 5 to 100")
	private String name;

	private StaffDTO manager;

	private boolean enabled;

	private String note;

	private List<StaffDTO> allowedEmployees;

	private BigDecimal balance = BigDecimal.ZERO;

	private String categoryGroupFundCode;

	private Format format;

}

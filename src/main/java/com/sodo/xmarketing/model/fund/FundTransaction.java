/**
 * 
 */
package com.sodo.xmarketing.model.fund;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.sodo.xmarketing.dto.FundInfo;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.entity.BaseEntity;
import com.sodo.xmarketing.model.wallet.Determinant;
import com.sodo.xmarketing.model.wallet.TargetObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * @author tuanhiep225
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "fund-transaction")
public class FundTransaction extends BaseEntity<String> {

	private String code;

	// Target đến đối tượng
	private TargetObject target;

	private FundInfo fund;

	@NotEmpty(message = "employeeCreate is not empty")
	private String employeeCreate;

	@NotEmpty(message = "transactionType is not empty")
	private String transactionType;

	private Determinant determinant;

	private Format format;

	@NotEmpty(message = "content is not empty")
	private String content;

	@NotNull(message = "amount is not null")
	private BigDecimal amount;

	private String note;

	@NotNull(message = "status is not null")
	private String status;

	private boolean createdByAccountant;

	@Id
	private transient String id;

	// Thêm trường số dư trước và sau
	private BigDecimal balanceBefore;

	private BigDecimal balanceAfter;

	// tài khoản nhân viên xác nhận giao dịch nếu có
	private String accountantAccept;

	// thời gian xác nhận giao dịch nếu có
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime timeAccept;

	private BigDecimal exchangeRate;

	private String linkedWalletTransact;

	private String treeCode;

	// Ngày chứng từ
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate timeReport;

	// Mã quỹ nhận
	private String targetFundCode;

	// Phí chuyển khoản nếu có
	private BigDecimal transferFee;

	// Phí giao dịch nếu có
	private BigDecimal tradeFee;

	// Mã giao dịch liên kết dùng trong giao dịch chuyển quỹ
	private String transactionChainCode;

	// Số tiền quỹ đích nhận
	private BigDecimal targetAmount;

}

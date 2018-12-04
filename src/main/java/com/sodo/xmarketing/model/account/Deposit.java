/**
 * 
 */
package com.sodo.xmarketing.model.account;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sodo.xmarketing.dto.FundTransactDTO;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.entity.BaseEntity;
import com.sodo.xmarketing.model.wallet.TransactionStatus;

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
@Document(collection = "deposit")
public class Deposit extends BaseEntity<String>{

	  @Id
	  private String id;

	  private String code;

	  private String receiverAccount;

	  private String userName;

	  private String customerName;

	  private String employeeName = "";

	  private String senderName;

	  private String cardNumber;

	  private BigDecimal moneyAmount;

	  private String tradeCode;

	  private String tradeContent;
	  
	  private StaffDTO staffHandle;
	  
	  private Format format;

	  @JsonFormat(pattern = "dd/MM/yyyy")
	  private transient LocalDate dateDeposit;

	  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
	  @JsonSerialize(using = LocalDateTimeSerializer.class)
	  private transient LocalDateTime acceptDate;

	  private String status = TransactionStatus.WAITTING.name();
	  private String note;
	  private FundTransactDTO fundTransactDTO;

}

/**
 * 
 */
package com.sodo.xmarketing.model;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sodo.xmarketing.model.entity.BaseEntity;

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
@Document(collection = "transaction-chain")
public class TransactionChain extends BaseEntity<String> {


	  private String id;
	  // mã code
	  private String code;
	  // tài khoản gửi tiền quỹ/ ví
	  private String sender;
	  // tài khoản nhận tiền quỹ/ ví
	  private String receiver;
	  // Thứ tự thực hiện giao dịch nếu có chứa mã code các giao dịch
	  private List<String> absoluteOrder;
	  // Danh sách các giao dịch đầu vào
	  @Indexed(unique = true)
	  private List<String> inputTransaction;
	  // Danh sách các giao dịch đầu ra
	  @Indexed(unique = true)
	  private List<String> outputTransaction;
	  // trạng thái của chuỗi giao dịch
	  private String status;
	  // Giao dịch cuối cùng đã thực hiện
	  private String currentTransaction;

	  private List<HistoryTransaction> historyTransaction;
}

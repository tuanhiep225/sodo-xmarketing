/**
 * 
 */
package com.sodo.xmarketing.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.querydsl.core.annotations.QueryEntity;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.Order.OrderBuilder;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.status.Distributor;
import com.sodo.xmarketing.status.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderWithNCC extends OrderExcel{
	List<OrderDistributor> orderDistributors;
}

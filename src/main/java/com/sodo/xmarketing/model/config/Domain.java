package com.sodo.xmarketing.model.config;

import com.sodo.xmarketing.model.entity.BaseEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 09/45
 */
@Document(collection = "domain")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Domain extends BaseEntity<String> {

  @Id
  private String id;

  // Tên domain
  private String name;

  // Ngôn ngữ mặc định, culture mặc định (Dạng vi, vn, en,...).
  private String defaultLang;

  // Danh sách các ngôn ngữ support (Dạng [vi, vn, en,..])
  private List<Format> langs;

  // Loại domain (CMS hoặc Customer)
  private String type;

  // Tên các domain customer
  private List<String> domains;

  // Tiền tố mã khách hàng
  private String codePrefix;
  
  private String tawtoId;
}

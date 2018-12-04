package com.sodo.xmarketing.model.config;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 09/45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DomainInfo {

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
}

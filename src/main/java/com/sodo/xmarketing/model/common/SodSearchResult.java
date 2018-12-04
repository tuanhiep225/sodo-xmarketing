package com.sodo.xmarketing.model.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 17/54
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SodSearchResult<T> {

  private List<T> items;

  private long totalRecord;

  private int totalPages;
}

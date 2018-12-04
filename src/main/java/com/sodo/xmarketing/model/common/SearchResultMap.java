package com.sodo.xmarketing.model.common;

import java.util.List;
import java.util.Map;

/**
 * Dữ liệu trả về group theo 1 trường
 *
 * @author Ha
 */
public class SearchResultMap<T> {
  private Map<String, List<T>> mapResult;

  private long totalRecord;

  public Map<String, List<T>> getMapResult() {
    return mapResult;
  }

  public void setMapResult(Map<String, List<T>> mapResult) {
    this.mapResult = mapResult;
  }

  public long getTotalRecord() {
    return totalRecord;
  }

  public void setTotalRecord(long totalRecord) {
    this.totalRecord = totalRecord;
  }
}

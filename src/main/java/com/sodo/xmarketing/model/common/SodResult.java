package com.sodo.xmarketing.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 17/53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SodResult<T> {

  private T result;
  private boolean isError;
  private String message;
  private String code;
}

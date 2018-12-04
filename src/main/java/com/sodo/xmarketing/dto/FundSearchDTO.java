package com.sodo.xmarketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FundSearchDTO {
    private String fundType;
    private String currency;
    private String name;
    private String manager;
    private Boolean status;
    private String fundGroupCode;
}

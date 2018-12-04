package com.sodo.xmarketing.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesTree {
    private List<CategoriesTree> children = new ArrayList<>();
    private String text;
    private String value;
}

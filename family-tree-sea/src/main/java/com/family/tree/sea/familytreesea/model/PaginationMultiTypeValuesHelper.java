package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaginationMultiTypeValuesHelper {
    private Integer count;
    private Integer page;
    private Object results;
    private Long total;
}
